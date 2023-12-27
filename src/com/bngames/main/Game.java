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
import com.bngames.entities.Particle;
import com.bngames.entities.ParticleBossHealth;
import com.bngames.entities.EnemySpectre;
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
					tutBar, tutShift, tutCdown, redScreen, damagePlayer, saving;
	public static boolean randomize, hideSprite, spawnEnemies, restartGame, fadeToBlack, finished,
							superHealthNextLevel;
	public boolean saveGame, spawnBlue, npcSpawn, showMessageGameOver, fadeFromBlack, fadeMenu,
					eraseSaveHold;
	
	private int framesGameOver, bossFrames, randFrames, blackoutFrames, nextlvlFrames,
				pauseFrames, musicFrames, tut, initFrames, playerHealth;
	
	public int frames;
	public static int orbContagem = 0, orbAtual = 0, orbsPicked = 0, redFrames = 0, 
					  bossTimer = 0, bossTimerFrames = 0, sceneFrames = 0, curLevel, eraseSaveFrames;
	
	public static final int MAX_LEVEL = 10, WIDTH = 240, HEIGHT = 240, SCALE = 3;
	
	private Integer space = 0;
	
	public BufferedImage[] spacebar;
	public BufferedImage hurtImage;
	private BufferedImage image;
	
	public static List<Entity> entities;
	public static List<EnemySpectre> enemies;
	public static Spritesheet spritesheet;
	public static World world;
	public static Player player;
	public static EnemySpectre enemy;
	public static Red red;

	public UI ui;

	public static String gameState = "SPLASH_SCREEN";
	
	public Game()
	{
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);

		setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		initFrame();

		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		
		try
		{ hurtImage = ImageIO.read(getClass().getResource("/hurt.png")); }

		catch (IOException e1)
		{ e1.printStackTrace(); }

		curLevel = SaveGame.loadFile().getLastPlayedLevel();
		
		if(SaveGame.loadFile().getBossDefeatedCount() > 0)
		{ finished = true; }
		
		spritesheet = new Spritesheet("/spritesheet.png");
		
		player = new Player(0, 0, 16, 16, 1, spritesheet.getSprite(32, 0, 16, 16));
		entities = new ArrayList<Entity>();
		enemies = new ArrayList<EnemySpectre>();
		
		world = new World(curLevel);
		ui = new UI();
		
		spacebar = new BufferedImage[]
		{
			spritesheet.getSprite(101, 116, 40, 11),
			spritesheet.getSprite(59, 116, 40, 11)
		};
		
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

		if (curLevel == MAX_LEVEL && Red.curLife == 0 && gameState == "NORMAL")
		{
			sceneFrames++;
			
			if(sceneFrames >= 60)
			{
				saving = true;
				SaveGame.saveLevel(String.valueOf(curLevel));
				SaveGame.saveBossDefeated();
				sceneFrames = 0;
				gameState = "TRANSITION2";
			} 
		}
		
		if(gameState.equals("LEVEL_SELECT"))
		{ 
			if(eraseSaveHold)
			{ 
				eraseSaveFrames = eraseSaveFrames < 120 ? eraseSaveFrames + 1 : eraseSaveFrames;
				
				if(eraseSaveFrames == 120)
				{
					SaveGame.eraseSave();
					System.exit(0);
				}
			}
			
			else
			{ 
				eraseSaveFrames = eraseSaveFrames > 0 ? eraseSaveFrames - 2 : 
				eraseSaveFrames < 0 ? eraseSaveFrames + 1 : eraseSaveFrames; 
			}
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
			
			if(musicFrames == 60 * 40 - 1)
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

		if (restartGame)
		{
			restartGame = false;

			Game.enemies.removeAll(enemies);
			Game.gameState = "SCENE1";
			
			blackoutFrames = 0;
			
			World.restartGame(curLevel);
			Game.enemies = new ArrayList<EnemySpectre>();
			
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
				Game.entities.removeIf(e -> e instanceof ParticleBossHealth || e instanceof Particle);
				fadeFromBlack = true;
				nextlvlFrames = 0;
				gameState = "SCENE3";
			}
		}
	}

	public void randomize()
	{
		if (randomize)
		{
			randFrames++;
			
			if(randFrames == 30)
			{ 
				Sound.get().bossound2.play();
				Sound.get().hit.play();
				
				if(damagePlayer)
				{
					Player.superHealth = false;
					--player.life; 
					damagePlayer = false;
				}
				
				else if(player.life < 3)
				{ 
					if(superHealthNextLevel)
					{ player.life = 3; } 
					
					else
					{ player.life++; } 
				}
				
				
				if(playerHealth == 0)
				{ bossFrames = 0; }
				
				playerHealth = player.life;
			}
			
			if(randFrames > 30 && randFrames < 60)
			{
				redScreen = true;
				bossTimer = 0;
				
				if(playerHealth > 0) 
				{ World.restartGame(curLevel); }
			}
			
			else if(randFrames > 60)
			{
				player.life = playerHealth;
				randomize = false;
				redScreen = false;
				damagePlayer = false;
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
			if (bossTimer == 20 - (Red.maxLife - Red.curLife) / 2)
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
		
		Collections.sort(entities, Entity.nodeSorter);
		
		for (int i = 0; i < entities.size(); i++)
		{
			Entity e = entities.get(i);
			e.render(g);
		}

		if(redScreen)
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

		if (curLevel == MAX_LEVEL && (gameState == "NORMAL" || gameState.equals("PAUSE")))
		{
			spawnEnemies = true;
			g.setColor(Color.white);
			countdown();
			ui.renderBoss(g);
		}

		if (gameState == "PAUSE")
		{
			pauseFrames++;
			if (pauseFrames > 30)
			{
				g.setColor(Color.gray);
				g.fillRect(108, 98, 8, 24);
				g.setColor(Color.white);
				g.fillRect(110, 100, 4, 20);
				
				g.setColor(Color.gray);
				g.fillRect(118, 98, 8, 24);
				g.setColor(Color.white);
				g.fillRect(120, 100, 4, 20);
			}
			
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
				player.life = superHealthNextLevel ? 3 : 2;
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
		// Level outro
		if (gameState == "TRANSITION")
		{
			nextlvlFrames++;
			
			ui.animateLevelOutro(g, nextlvlFrames);
			
			if (nextlvlFrames == 1)
			{ fadeToBlack = true; }
			
			if (nextlvlFrames == 60)
			{
				nextlvlFrames = 0;
				saving = true;
				
				SaveGame.saveLevel(String.valueOf(curLevel));
				
				curLevel++;
				
				if (curLevel > MAX_LEVEL)
				{ curLevel = 1; }
				
				SaveGame.saveLastPlayedLevel(curLevel);
				World.restartGame(curLevel);
				
				gameState = "SCENE1";
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
			sceneFrames = ui.finalCutscene(g, sceneFrames);
			
			if (sceneFrames > 250)
			{
				fadeToBlack = true;
				sceneFrames = 0;
				Game.gameState = "END";
			}
		}

		if (Game.player.life == 1)
		{
			redFrames++;
			
			if (redFrames <= 15)
			{ g.drawImage(hurtImage, 0, 0, null); }
			
			else if (redFrames >= 45)
			{ redFrames = 0; }
		}

		if (fadeToBlack == true || fadeFromBlack == true)
		{
			Graphics2D g2 = (Graphics2D) g;
			blackoutFrames++;

			boolean done = ui.fadeBlack(g2, blackoutFrames, 1, fadeToBlack);
			
			if(done)
			{
				blackoutFrames = 0;
				fadeToBlack = false;
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
			space = ui.animateSpaceBar(g, space, spacebar, 220, 600, 5.5);
		}
				
		if (gameState.equals("NORMAL") || gameState.equals("PAUSE"))
		{ ui.render(g); }

		if(saving)
		{ saving = !ui.drawSaveIcon(g); }
		
		if (gameState.equals("GAME_OVER"))
		{
			if(!fadeToBlack)
			{
				g.setColor(Color.black);
				g.fillRect(0, 0, WIDTH * SCALE, WIDTH * SCALE); 
			}
			space = ui.animateSpaceBar(g, space, spacebar, 290, 353, 1);
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
			
			g.setColor(new Color(120, 200, 0));
			g.setFont(new Font("consolas", Font.BOLD, 20));
			g.drawString(GAME_VERSION, 10, Game.HEIGHT * SCALE - 18);
			g.drawString("Made by J0NATHA", 540, Game.HEIGHT * SCALE - 18);
			 
			space = ui.animateSpaceBar(g, space, spacebar, 273, 500, 2.3);	 
		}
		
		if(curLevel == MAX_LEVEL)
		{
			if(gameState.equals("SCENE2"))
			{ ui.drawBossName(g, bossFrames); }
			
			else if(gameState.equals("NORMAL") || gameState.equals("PAUSE"))
			{ ui.drawBossName(g); }
		}
		

		if (gameState == "END")
		{
			Sound.boss_loop.terminate();
			sceneFrames++;

			g.setColor(Color.black);
			g.fillRect(0, 0, WIDTH * SCALE, HEIGHT * SCALE);
			g.setFont(new Font("consolas", Font.LAYOUT_LEFT_TO_RIGHT, 26));

			if(sceneFrames < 250)
			{
				g.setColor(Color.green);
				g.drawString("To be continued...", WIDTH - 20, HEIGHT + 100);
			}
			else if(sceneFrames < 470)
			{
				g.setColor(Color.white);
				g.drawString("Created by Jonatha Menezes", WIDTH - 70, HEIGHT + 100);
				g.drawString("(https://j0natha.itch.io/)", WIDTH - 70, HEIGHT + 140);
			}
			else if(sceneFrames < 760)
			{
				g.setColor(Color.white);
				g.drawString("SFX", WIDTH + 100, HEIGHT + 60);
				g.drawString("Sound effects made with sfxr", WIDTH - 85, HEIGHT + 100);
				g.drawString("\"Cyberpunk Moonlight Sonata\" by Joth", WIDTH - 135, HEIGHT + 140);
				g.drawString("\"The Projects\" by Alex McCulloch", WIDTH - 115, HEIGHT + 180);				
			}
			else if(sceneFrames < 980)
			{
				g.setColor(Color.white);
				g.drawString("GFX", WIDTH + 100, HEIGHT + 60);
				g.drawString("Jonatha Menezes", WIDTH + 15, HEIGHT + 100);
			}
			else
			{
				g.setColor(Color.white);
				g.drawString("Thank you for playing.", WIDTH - 40, HEIGHT + 100);
			}
			
			if (sceneFrames >= 1200)
			{ System.exit(0); }
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
		double ns = 1_000_000_000 / amountofTicks;
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
				//System.out.println("FPS: " + frames);
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
			
			else if(gameState == "NORMAL" && orbsPicked == 20)
			{ player.crushOrb = true; }
			
			else if(gameState.equals("LEVEL_SELECT"))
			{ gameState = "SCENE1"; }
		}

		if (e.getKeyCode() == KeyEvent.VK_SHIFT)
		{
			if(gameState == "TUT" && !tutShift && !tutCdown)
			{
				Sound.get().keys.play();
				tutCdown = true;
				tutShift = true;
			}
			
			player.sneak = true;
		}
		
		if(gameState.equals("LEVEL_SELECT") && e.getKeyCode() == KeyEvent.VK_Q)
		{ eraseSaveHold = true; }

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
		if(e.getKeyCode() == KeyEvent.VK_SPACE) orbsPicked = 20;
		if(e.getKeyCode() == KeyEvent.VK_SHIFT)
		{ player.sneak = false; }
		
		if(e.getKeyCode() == KeyEvent.VK_E && gameState.equals("LEVEL_SELECT") && finished)
		{
			Sound.get().keys.play();
			superHealthNextLevel = !superHealthNextLevel; 	
		}
		
		if(gameState.equals("LEVEL_SELECT") && e.getKeyCode() == KeyEvent.VK_Q)
		{ eraseSaveHold = false; }
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
