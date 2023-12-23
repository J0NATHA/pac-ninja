package com.bngames.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.bngames.entities.Entity;
import com.bngames.entities.Enemy;
import com.bngames.entities.Player;
import com.bngames.entities.Red;
import com.bngames.graficos.Spritesheet;
import com.bngames.graficos.UI;
import com.bngames.world.World;
import com.bngames.world.Camera;

public class Game extends Canvas implements Runnable, KeyListener, MouseListener, MouseMotionListener
{
	private static final long serialVersionUID = 1L;
	
	public static final String GAME_VERSION = "v1.3.0";
	public static JFrame frame;

	private Thread thread;
	private Color keysColor;
	
	private boolean isRunning = true, tutUp, tutDown, tutLeft, tutRight, 
					tutBar, tutShift, tutCdown, color, damagePlayer;
	public static boolean randomize, hideSprite, spawnEnemies, restartGame, fadeToBlack, finished;
	public boolean saveGame, spawnBlue, npcSpawn, showMessageGameOver, fadeFromBlack, fadeMenu;
	
	private int framesGameOver, bossFrames, randFrames, blackoutFrames, blackinFrames, nextlvlFrames,
				pauseFrames, musicFrames, tut, initFrames = 0;
	
	public int frames, rectH = 1, rectaY = 47;
	public static int orbContagem = 0, orbAtual = 0, orbsPicked = 0, redFrames = 0, 
					  bossTimer = 0, bossTimerFrames = 0, sceneFrames = 0, curLevel;
	
	public static final int MAX_LEVEL = 10, WIDTH = 240, HEIGHT = 240, SCALE = 3;
	
	private Integer space = 0;
	
	public BufferedImage[] spacebar;
	public BufferedImage redmap;
	private BufferedImage image;
	
	public static List<Entity> entities;
	public static List<Enemy> enemies;
	public static Spritesheet spritesheet;
	public static World world;
	public static Player player;
	public static Enemy enemy;
	public static Red red;

	public UI ui;

	public static String gameState = "SPLASH_SCREEN";
	
	public Game()
	{
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);

		this.setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		initFrame();

		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		
		try
		{ redmap = ImageIO.read(getClass().getResource("/red.png")); }

		catch (IOException e1)
		{ e1.printStackTrace(); }

		curLevel = SaveGame.loadFile().getLastPlayedLevel();
		
		int latestCompletedLevel = SaveGame.latestCompletedLevel();
		
		if(latestCompletedLevel == Game.MAX_LEVEL)
		{ finished = true; }
		
		spritesheet = new Spritesheet("/spritesheet.png");
		
		player = new Player(0, 0, 16, 16, 1, spritesheet.getSprite(32, 0, 16, 16));

		entities = new ArrayList<Entity>();
		enemies = new ArrayList<Enemy>();
		
		world = new World(curLevel);
		ui = new UI();
		
		spacebar = new BufferedImage[2];
		spacebar[0] = spritesheet.getSprite(101, 116, 40, 11);
		spacebar[1] = spritesheet.getSprite(59, 116, 40, 11);
		
		red = new Red(0, 0, 14, 16, 1, null);
		
		keysColor = new Color(
						new Random().nextInt(256),
						new Random().nextInt(256),
						new Random().nextInt(256) );
		
		entities.add(player);
		
		Camera.place(curLevel);
	}

	public void initFrame()
	{
		frame = new JFrame("Pac-Ninja");
		frame.add(this);
		frame.setResizable(false);
		frame.pack();
		Image imagem = null;

		try
		{ imagem = ImageIO.read(getClass().getResource("/icon.png")); }
		
		catch (IOException e)
		{ e.printStackTrace(); }

		frame.setIconImage(imagem);
		frame.setAlwaysOnTop(true);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	public synchronized void start()
	{
		thread = new Thread(this);
		isRunning = true;
		thread.start();
	}

	public synchronized void stop()
	{
		isRunning = false;

		try
		{ thread.join(); }
		
		catch (InterruptedException e)
		{ e.printStackTrace(); }
	}

	public static void main(String[] args)
	{
		Game game = new Game();
		game.start();
	}

	public void tick()
	{
		if (randomize)
		{ randomize(); }

		if (gameState == "NORMAL" && curLevel == MAX_LEVEL && Red.curLife == 0)
		{
			SaveGame.save(String.valueOf(curLevel));
			SaveGame.saveBossDefeated();
			gameState = "TRANSITION2";
		}

		if (curLevel != MAX_LEVEL && !gameState.equals("SPLASH_SCREEN") || gameState.equals("MENU"))
		{ Sound.bgm.loop(); } 
		
		List<String> states = new ArrayList<String>(
				Arrays.asList("SCENE1", "SCENE2", "NORMAL", "PAUSE", "GAME_OVER") );
		
		if(curLevel == MAX_LEVEL && (states.contains(gameState) 
				|| SaveGame.latestCompletedLevel() < MAX_LEVEL))
		{
			musicFrames++;
			
			if (musicFrames == 1)
			{ Sound.boss_opening.play(); }
			
			if(musicFrames == 2370)
			{
				Sound.boss_opening.terminate();
				Sound.boss_loop.loop();
			}
			
			
			Sound.bgm.terminate();
		}

		if (gameState == "GAME_OVER")
		{
			framesGameOver++;
			
			if (framesGameOver == 30)
			{
				framesGameOver = 0;
				
				if (this.showMessageGameOver)
				{ showMessageGameOver = false; }

				else
				{ this.showMessageGameOver = true; }
			}
		}
		else if (gameState == "NORMAL")
		{
			for (int i = 0; i < entities.size(); i++)
			{
				Entity e = entities.get(i);
				e.tick();
			}
			
			if (Game.orbAtual == Game.orbContagem && curLevel != MAX_LEVEL)
			{
				if (Game.orbsPicked == 20)
				{ Player.superHealth = true; }
				
				gameState = "TRANSITION";
			}
		}

		if (gameState == "TRANSITION")
		{
			nextlvlFrames++;
			
			if (nextlvlFrames == 1)
			{ fadeToBlack = true; }
			
			if (nextlvlFrames == 60)
			{
				nextlvlFrames = 0;
				
				SaveGame.save(String.valueOf(curLevel));
				
				curLevel++;
				
				if (curLevel > MAX_LEVEL)
				{ curLevel = 1; }
				
				SaveGame.saveLastPlayedLevel(curLevel);
				World.restartGame(curLevel);

				gameState = "SCENE1";
			}
		}

		if (restartGame)
		{
			restartGame = false;

			Game.enemies.removeAll(enemies);
			Game.gameState = "SCENE1";
			
			blackoutFrames = 0;
			
			World.restartGame(curLevel);
			Game.enemies = new ArrayList<Enemy>();
			
			if (curLevel == MAX_LEVEL)
			{
				Red.curLife = 0;
				this.bossFrames = 0;
				bossTimer = 0;
			}
		}

		if (gameState == "TRANSITION2")
		{
			nextlvlFrames++;
			
			if(nextlvlFrames == 1)
			{ fadeToBlack = true; }
			
			if (nextlvlFrames == 60)
			{ 
				fadeFromBlack = true;
				nextlvlFrames = 0;
				gameState = "SCENE3";
			}
		}
	}

	public void randomize()
	{
		int playerHealth = player.life; 
		if (randomize)
		{
			randFrames++;
			
			if (randFrames == 30)
			{ 
				Sound.get().bossound2.play();
				Sound.get().hit.play(); 
				
				if(damagePlayer)
				{ player.life--; }
				
				if (player.life == 0)
				{ bossFrames = 0; }
			}
			
			if (randFrames > 30 && randFrames < 60)
			{
				color = true;
				World.restartGame(curLevel);
				bossTimer = 0;
			}
			
			else if (randFrames > 60)
			{
				if(damagePlayer)
				{
					player.life = playerHealth - 1;
					damagePlayer = false;
				}
				
				randomize = false;
				color = false;
				randFrames = 0;
			}
		}
	}

	public void countdown()
	{
		if (gameState == "NORMAL")
		{
			bossTimerFrames++;
			
			if (bossTimerFrames == 60)
			{
				if (bossTimer < 20)
				{ bossTimer++; }
				
				bossTimerFrames = 0;
			}
			if (bossTimer == 20 - (Red.maxLife - Red.curLife))
			{
				randomize = true;
				damagePlayer = true;
			}
		}
	}

	public void render()
	{
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null)
		{
			this.createBufferStrategy(3);
			return;
		}
		Graphics g = image.getGraphics();
		g.setColor(new Color(0, 0, 0));
		g.fillRect(0, 0, WIDTH, HEIGHT);

		world.render(g);

		Collections.sort(entities, Entity.nodeSorter);
		
		if (Game.curLevel == MAX_LEVEL)
		{
			if (gameState != "SCENE3" && gameState != "LEVEL_SELECT_CHANGED")
			{ 
				g.drawImage(
					Game.spritesheet.getSprite(43, 136, 20, 20), 158 - Camera.x, 0 - Camera.y, null); 
			}
			
			if (gameState == "SCENE3")
			{
				if (sceneFrames < 240)
				{ 
					g.drawImage(
						Game.spritesheet.getSprite(43, 136, 20, 20), 158 - Camera.x, 0 - Camera.y, null); 
				}
				
				else if (sceneFrames >= 240)
				{ 
					g.drawImage(
						Game.spritesheet.getSprite(120, 136, 20, 20), 158 - Camera.x, 0 - Camera.y, null); 
				}
			}
		}

		for (int i = 0; i < entities.size(); i++)
		{
			Entity e = entities.get(i);
			e.render(g);
		}

		if (color)
		{
			g.setColor(new Color(250, 0, 0, 200));
			g.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
		}

		if (gameState == "MENU")
		{
			g.setColor(new Color(0, 0, 0, 100));
			g.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
			g.setColor(Color.green);
			g.fillRect(89, 80, 62, 16);
			g.setColor(new Color(210, 140, 0));
			g.fillRect(90, 81, 60, 14);
			g.setFont(new Font("arial", Font.CENTER_BASELINE, 12));
			g.setColor(Color.green);
			g.drawString("Pac-Ninja", (Game.WIDTH) / 2 - 26, (Game.HEIGHT) / 2 - 28);
		}

		if (curLevel == MAX_LEVEL)
		{
			if (gameState == "NORMAL")
			{
				spawnEnemies = true;
				g.setColor(Color.white);
				countdown();
				ui.renderBoss(g);
			}
		}

		if (gameState == "PAUSE")
		{
			pauseFrames++;
			g.setColor(Color.white);
			
			if (pauseFrames > 30)
			{ g.drawString("PAUSED", 92, 110); }
			
			if (pauseFrames == 60)
			{ pauseFrames = 0; }
		}
		
		// Level intro
		if (gameState == "SCENE1")
		{
			boolean canChangeState = initFrames == 120;
			
			if(!canChangeState)
			{ ++initFrames; }
			
			if (initFrames == 1)
			{ 
				Camera.place(curLevel);
				Sound.get().start.play(); 
			}
			
			canChangeState = player.getY() < 32 ?
					ui.waitLevelIntro(g, initFrames) :
					ui.animateLevelIntro(g, initFrames);
		
			if(canChangeState)
			{
				initFrames = 0;
				gameState = curLevel == Game.MAX_LEVEL ? "SCENE2" : "NORMAL";
			}	
		}

		// Boss intro
		if (gameState == "SCENE2")
		{
			bossFrames++;

			ui.renderBoss(g);

			if (bossFrames == 1)
			{
				Camera.place(MAX_LEVEL);
				Red.curLife = 0;
			}

			if (Red.curLife < 5)
			{
				if (bossFrames == 10)
				{
					Red.curLife++;
					Sound.get().boss1.play();
					Camera.x += 5;
				}
				if (bossFrames == 20)
				{
					Red.curLife++;
					Camera.x -= 5;
				}
				if (bossFrames == 30)
				{
					Red.curLife++;
					Camera.x += 5;
				}
				if (bossFrames == 40)
				{
					Red.curLife++;
					Camera.x -= 5;
				}
				if (bossFrames == 50)
				{
					Red.curLife++;
					Camera.x += 5;
				}
			}
			if (bossFrames > 50 && bossFrames < 147)
			{
				Camera.x = 40;
				Camera.y += 1; 
			}
			
			if (bossFrames == 180)
			{
				randomize = true;
				
				if (Red.curLife == Red.maxLife)
				{ gameState = "NORMAL"; }
			}
		}
		
		if(randomize)
		{ ui.fadeToRed(g, randFrames); }

		if (gameState == "SCENE3")
		{
			sceneFrames++;

			Camera.y = 0;
			Camera.x = 47;
			
			if (sceneFrames == 1)
			{
				player.currentDirection = 1;
				Sound.get().scream.play();
			}
			if (sceneFrames > 10)
			{
				if (rectaY > 32)
				{ rectaY--; }
				
				if (rectH < 16)
				{ rectH++; }
				
				g.setColor(new Color(0, 240, 0, 100));
				g.fillRect(113, rectaY, 14, rectH);

			}
			if (sceneFrames > 30)
			{
				hideSprite = true;
				
				g.drawImage(spritesheet.getSprite(103, 135, 14, 16), 113, 32, null);
				
				if (player.getX() < 128)
				{
					player.animate();
					player.x++;
				}
			}
			
			if (sceneFrames == 63)
			{ player.currentDirection = 2; }
			
			if (sceneFrames > 65)
			{
				if (player.getY() > 16)
				{
					player.y--;
					player.animate();
				}

			}
			
			if (sceneFrames == 100)
			{ player.currentDirection = 1; }
			
			if (sceneFrames > 100)
			{
				if (player.getX() < 161)
				{
					player.x++;
					player.animate();
				}
			}
			
			if (sceneFrames == 162)
			{
				player.index = 0;
				player.currentDirection = 2;
			}
			
			if (sceneFrames == 170)
			{
				Sound.get().portal.play();
				Camera.y += 5;
			}
			
			if (sceneFrames == 180)
			{ Camera.x += 5; }
			
			if (sceneFrames == 190)
			{ Camera.y -= 5; }
			
			if (sceneFrames == 200)
			{ Camera.x -= 5; }
			
			if (sceneFrames > 240)
			{
				if (player.getY() > 2)
				{ player.y--; }
			}
			
			if (sceneFrames > 250)
			{
				fadeToBlack = true;
				gameState = "END";
				sceneFrames = 0;
			}
		}

		if (Game.player.life == 1)
		{
			redFrames++;
			
			if (redFrames <= 15)
			{ g.drawImage(redmap, 0, 0, null); }
			
			else if (redFrames >= 45)
			{ redFrames = 0; }
		}

		if (fadeToBlack == true)
		{
			Graphics2D g2 = (Graphics2D) g;
			blackoutFrames++;

			boolean done = ui.fadeBlack(g2, blackoutFrames, 1, true);
			
			if(done)
			{
				blackoutFrames = 0;
				fadeToBlack = false;
			}
		}

		if (fadeFromBlack == true)
		{
			Graphics2D g2 = (Graphics2D) g;
			blackinFrames++;
			boolean done = ui.fadeBlack(g2, blackinFrames, 1, false);
			
			if(done)
			{
				blackinFrames = 0;
				fadeFromBlack = false;
			}
		}

		// SCALE IS DONE HERE
		g.dispose();
		g = bs.getDrawGraphics();
		g.drawImage(image, 0, 0, WIDTH * SCALE, HEIGHT * SCALE, null);
		// SCALE IS DONE HERE

		if (gameState == "TUT")
		{
			g.setColor(new Color(0, 0, 0, 245));
			g.fillRect(0, 0, WIDTH * SCALE, HEIGHT * SCALE);
			g.setColor(Color.gray);
			g.drawRect(109 * SCALE, 89 * SCALE, 12 * SCALE, 12 * SCALE);
			g.drawRect(109 * SCALE, 104 * SCALE, 12 * SCALE, 12 * SCALE);
			g.drawRect(124 * SCALE, 104 * SCALE, 12 * SCALE, 12 * SCALE);
			g.drawRect(94 * SCALE, 104 * SCALE, 12 * SCALE, 12 * SCALE);
			g.drawRect(94 * SCALE, 119 * SCALE, 42 * SCALE, 12 * SCALE);
			g.drawRect(54 * SCALE, 119 * SCALE, 21 * SCALE, 12 * SCALE);

			g.setColor(Color.white);
			g.drawRect(110 * SCALE, 90 * SCALE, 10 * SCALE, 10 * SCALE);
			g.drawRect(110 * SCALE, 105 * SCALE, 10 * SCALE, 10 * SCALE);
			g.drawRect(125 * SCALE, 105 * SCALE, 10 * SCALE, 10 * SCALE);
			g.drawRect(95 * SCALE, 105 * SCALE, 10 * SCALE, 10 * SCALE);
			g.drawRect(95 * SCALE, 120 * SCALE, 40 * SCALE, 10 * SCALE);
			g.drawRect(55 * SCALE, 120 * SCALE, 19 * SCALE, 10 * SCALE);

			if (tutUp)
			{
				g.setColor(keysColor);
				g.fillRect(110 * SCALE, 90 * SCALE, 10 * SCALE, 10 * SCALE);
			}
			if (tutDown)
			{
				g.setColor(keysColor);
				g.fillRect(110 * SCALE, 105 * SCALE, 10 * SCALE, 10 * SCALE);
			}
			if (tutLeft)
			{
				g.setColor(keysColor);
				g.fillRect(95 * SCALE, 105 * SCALE, 10 * SCALE, 10 * SCALE);
			}
			if (tutRight)
			{
				g.setColor(keysColor);
				g.fillRect(125 * SCALE, 105 * SCALE, 10 * SCALE, 10 * SCALE);
			}
			if (tutBar)
			{
				g.setColor(keysColor);
				g.fillRect(95 * SCALE, 120 * SCALE, 40 * SCALE, 10 * SCALE);
			}

			if (tutShift)
			{
				g.setColor(keysColor);
				g.fillRect(55 * SCALE, 120 * SCALE, 19 * SCALE, 10 * SCALE);
			}

			if (tutCdown)
			{
				tut++;
				
				if(tut % 4 == 0)
				{
					keysColor = new Color(
							new Random().nextInt(256),
							new Random().nextInt(256),
							new Random().nextInt(256) );
				}
				
				
				if (tut >= 20)
				{
					tutCdown = false;
					Sound.get().keys.terminate();
				
					if (tutUp && tutDown && tutLeft && tutRight && tutBar && tutShift)
					{
						gameState = "MENU";
					}
				}
			} 
			else
			{ tut = 0; }
		}
		
		if (gameState.equals("SPLASH_SCREEN"))
		{
			boolean done = ui.drawSplashScreen(g);
			
			if(done)
			{
				gameState = "MENU";
				
				if(SaveGame.latestCompletedLevel() == 0)
				{ gameState = "TUT";}
				
				if(curLevel == MAX_LEVEL && SaveGame.latestCompletedLevel() < MAX_LEVEL)
				{
					fadeFromBlack = true;
					gameState = "SCENE1"; 
				}
			}
		}
		
		if (gameState.equals("LEVEL_SELECT") || gameState.equals("LEVEL_SELECT_CHANGED"))
		{
			ui.drawLevelSelectMenu(g, curLevel);
			space = ui.animateSpaceBar(g, space, spacebar, 260, 560, 3);
		}
		
		if (gameState.equals("NORMAL") || gameState.equals("PAUSE"))
		{ ui.render(g); }

		if (gameState.equals("GAME_OVER"))
		{
			if(!fadeToBlack)
			{
				g.setColor(Color.black);
				g.fillRect(0, 0, WIDTH * SCALE, WIDTH * SCALE); 
			}
			int xpos = 280;
			g.setColor(Color.gray);
			g.setFont(new Font("consolas", Font.BOLD, 33));
			g.drawString("You died!", xpos - 2, Game.HEIGHT * SCALE / 2 + 2);
			g.setColor(Color.red);
			g.setFont(new Font("consolas", Font.BOLD, 33));
			g.drawString("You died!", xpos, Game.HEIGHT * SCALE / 2);

			if (showMessageGameOver)
			{
				g.setColor(Color.gray);
				g.setFont(new Font("consolas", Font.BOLD, 20));
				g.drawString("Press spacebar to try again", 212, Game.HEIGHT * SCALE / 2 + 32);
				g.setColor(Color.red);
				g.setFont(new Font("consolas", Font.BOLD, 20));
				g.drawString("Press spacebar to try again", 214, Game.HEIGHT * SCALE / 2 + 30);
			}
		}
		
		if (gameState.equals("NORMAL") || gameState.equals("PAUSE"))
		{
			if (Game.orbsPicked == 20)
			{ space = ui.animateSpaceBar(g, space, spacebar); }
		}

		if (gameState == "MENU")
		{
			if(!fadeMenu)	
			{
				++blackoutFrames;
				
				fadeMenu = ui.fadeBlack(g, blackoutFrames, 3, false);
				
				if(fadeMenu)
				{ blackoutFrames = 0; }
			}
			
			pauseFrames++;
			
			g.setColor(new Color(120, 200, 0));
			g.setFont(new Font("consolas", Font.BOLD, 20));
			g.drawString("Version: " + GAME_VERSION, 10, Game.HEIGHT * SCALE - 18);
			g.drawString("Made by J0NATHA", 500, Game.HEIGHT * SCALE - 18);
			
			if (pauseFrames > 30)
			{ g.drawString("Move/click to start!", 258, Game.HEIGHT * SCALE / 2); }
			
			if (pauseFrames == 60)
			{ pauseFrames = 0; } 
		}

		if (curLevel == MAX_LEVEL && gameState == "NORMAL")
		{
			g.setColor(Color.black);
			g.setFont(new Font("consolas", Font.BOLD, 26));
			g.drawString("RedNinja", 295, 52);
		}

		if (gameState == "END")
		{
			
			Sound.boss_loop.terminate();
			sceneFrames++;

			g.setColor(Color.black);
			g.fillRect(0, 0, WIDTH * SCALE, HEIGHT * SCALE);

			if (sceneFrames > 30 && sceneFrames < 150)
			{
				g.setColor(Color.white);
				g.setFont(new Font("consolas", Font.LAYOUT_LEFT_TO_RIGHT, 26));
				g.drawString("To be continued...", WIDTH, HEIGHT + 100);
			}
			if (sceneFrames > 150 && sceneFrames < 270)
			{
				g.setColor(Color.white);
				g.setFont(new Font("consolas", Font.LAYOUT_LEFT_TO_RIGHT, 26));
				g.drawString("Created by J0natha Menezes", WIDTH - 70, HEIGHT + 100);
			}
			if (sceneFrames > 270 && sceneFrames < 390)
			{
				g.setColor(Color.white);
				g.setFont(new Font("consolas", Font.LAYOUT_LEFT_TO_RIGHT, 26));
				g.drawString("Fire sprite by Chromaeleon (ColorOptimist)", 55, HEIGHT + 60);
				g.drawString("Cyberpunk Moonlight Sonata", 180, HEIGHT + 100);
				g.drawString("(RedNinja theme) by Joth", 180, HEIGHT + 130);
			}
			if (sceneFrames > 410)
			{
				g.setColor(Color.white);
				g.setFont(new Font("consolas", Font.LAYOUT_LEFT_TO_RIGHT, 26));
				g.drawString("Thank you for playing.", WIDTH - 50, HEIGHT + 100);
			}
			if (sceneFrames > 500)
			{
				System.exit(1);
			}
		}
		if(gameState.equals("LEVEL_SELECT_CHANGED"))
		{
			blackoutFrames++;
			
			final double fadeOutDuration = .7;
			final double fadeInDuration = .5;
			
			if(blackoutFrames < UI.secondsToFrames(fadeOutDuration))
			{
				boolean done = ui.fadeBlack(g, blackoutFrames, fadeOutDuration, true);
				
				if(done)
				{ 
					World.restartGame(curLevel);
					Camera.place(curLevel);
				}				
			}
			else
			{
				boolean done = ui.fadeBlack(g, blackoutFrames, fadeInDuration, false);
				
				if(done)
				{
					blackoutFrames = 0;
					gameState = "LEVEL_SELECT"; 
				}
			}
		}
		bs.show();
	}

	public void run()
	{
		long lastTime = System.nanoTime();
		double amountofTicks = 60.0;
		double ns = 1000000000 / amountofTicks;
		double delta = 0;

		double timer = System.currentTimeMillis();
		requestFocus();
		while (isRunning)
		{
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;

			if (delta >= 1)
			{
				tick();
				render();
				frames++;
				delta--;
			}
			if (System.currentTimeMillis() - timer >= 1000)
			{
				// System.out.println("FPS: "+frames);

				frames = 0;
				timer += 1000;
			}
		}
		stop();
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
	}

	@Override
	public void keyPressed(KeyEvent e)
	{		
		if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D)
		{
			if (gameState == "TUT" && tutRight == false && tutCdown == false)
			{
				Sound.get().keys.play();
				tutRight = true;
				tutCdown = true;
			}
			
			if (gameState == "PAUSE")
			{ gameState = "NORMAL"; }

			if(gameState.equals("LEVEL_SELECT") &&
				curLevel <= SaveGame.latestCompletedLevel() &&
				curLevel != MAX_LEVEL)
			{
				Sound.get().keys.play();
				
				Game.curLevel++;
				gameState = "LEVEL_SELECT_CHANGED";
			}
			
			player.lastDirection = player.currentDirection;
			player.right = true;
			player.left = false;
			player.up = false;
			player.down = false;
		}
		else if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A)
		{
			if (gameState == "TUT" && tutLeft == false && tutCdown == false)
			{
				Sound.get().keys.play();
				tutCdown = true;
				tutLeft = true;
			}

			if (gameState == "PAUSE")
			{ gameState = "NORMAL"; }

			if(gameState.equals("LEVEL_SELECT") && curLevel > 1)
			{
				Sound.get().keys.play();

				Game.curLevel--;
				gameState = "LEVEL_SELECT_CHANGED";
			}
			
			player.lastDirection = player.currentDirection;
			player.left = true;
			player.right = false;
			player.down = false;
			player.up = false;
		}
		else if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W)
		{
			if (gameState == "TUT" && tutUp == false && tutCdown == false)
			{
				Sound.get().keys.play();
				tutCdown = true;
				tutUp = true;
			}
			if (gameState == "PAUSE")
			{ gameState = "NORMAL"; }

			player.lastDirection = player.currentDirection;
			player.up = true;
			player.left = false;
			player.right = false;
			player.down = false;
		}
		else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S)
		{
			if (gameState == "TUT" && tutDown == false && tutCdown == false)
			{
				Sound.get().keys.play();
				tutCdown = true;
				tutDown = true;
			}

			if (gameState == "PAUSE")
			{ gameState = "NORMAL"; }

			player.lastDirection = player.currentDirection;
			player.down = true;
			player.up = false;
			player.left = false;
			player.right = false;
		}
		
		if (e.getKeyCode() == KeyEvent.VK_SPACE)
		{
			if (gameState == "TUT" && tutBar == false && tutCdown == false)
			{
				Sound.get().keys.play();
				tutCdown = true;
				tutBar = true;
			}
			
			else if (gameState == "NORMAL" && orbsPicked == 20)
			{ Player.crushOrb = true; }
			
			else if(gameState.equals("LEVEL_SELECT"))
			{ gameState = "SCENE1"; }
			
			if(orbsPicked < 20)
			{ orbsPicked = 20; }
		}

		if (e.getKeyCode() == KeyEvent.VK_SHIFT)
		{
			if (gameState == "TUT" && !tutShift && !tutCdown)
			{
				Sound.get().keys.play();
				tutCdown = true;
				tutShift = true;
			}
			
			player.sneak = true;
		}

		if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
		{
			if(gameState == "PAUSE")
			{ gameState = "NORMAL"; }
			
			else if(gameState == "NORMAL")
			{ gameState = "PAUSE"; } 
		}
		
		else if(e.getKeyCode() != 0)
		{
			if (gameState == "PAUSE")
			{ gameState = "NORMAL"; }
			
			if (gameState == "MENU")
			{
				blackoutFrames = 0;
				
				gameState = "SCENE1";
				
				if(Game.curLevel > 1 && Game.curLevel != Game.MAX_LEVEL || Game.finished)
				{ gameState = "LEVEL_SELECT"; }
			}

			if (gameState == "GAME_OVER" && !fadeToBlack)
			{ Game.restartGame = true; }
		}
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_SHIFT)
		{ player.sneak = false; }
	}

	@Override
	public void mouseClicked(MouseEvent e) { }

	@Override
	public void mousePressed(MouseEvent e)
	{
		if (gameState == "PAUSE")
		{ gameState = "NORMAL"; }

		if (gameState == "MENU")
		{ 
			blackoutFrames = 0;
			
			gameState = "SCENE1";
			
			if(Game.curLevel > 1 && Game.curLevel != Game.MAX_LEVEL || Game.finished)
			{ gameState = "LEVEL_SELECT"; }
		}

		if (gameState == "GAME_OVER" && !fadeToBlack)
		{ Game.restartGame = true; }
	}

	@Override
	public void mouseReleased(MouseEvent e) { }

	@Override
	public void mouseEntered(MouseEvent e) { }

	@Override
	public void mouseExited(MouseEvent e) { }

	@Override
	public void mouseDragged(MouseEvent e) { }

	@Override
	public void mouseMoved(MouseEvent e) { }
}
