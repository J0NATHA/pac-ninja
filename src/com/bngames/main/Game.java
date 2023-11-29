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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.bngames.entities.Enemy;
import com.bngames.entities.Entity;
import com.bngames.entities.Player;
import com.bngames.entities.Red;
import com.bngames.graficos.Spritesheet;
import com.bngames.graficos.UI;
import com.bngames.world.Camera;
import com.bngames.world.World;

public class Game extends Canvas implements Runnable, KeyListener, MouseListener, MouseMotionListener
{
	private static final long serialVersionUID = 1L;
	
	public final String GAME_VERSION = "v1.3.0";
	public static JFrame frame;

	private Thread thread;
	private Color keysColor;
	
	private boolean isRunning = true, tutUp, tutDown, tutLeft, tutRight, tutBar, tutShift, tutCdown, color;
	public static boolean randomize, hideSprite, spawnEnemies, restartGame, fadeOut;
	public boolean saveGame, spawnBlue, npcSpawn, showMessageGameOver, fadeIn, fadeMenu;
	
	private int framesGameOver, bossFrames, randFrames, blackoutFrames, space, blackinFrames, nextlvlFrames,
	pauseFrames, musicFrames, tut, initFrames;
	
	public int frames = 0, rectX = 115, rectY = 10, rectH = 1, rectaY = 47, xx, yy, mx, my;
	public static int orbContagem = 0, orbAtual = 0, orbsPicked = 0, redFrames = 0, 
					  bossTimer = 0, bossTimerFrames = 0, sceneFrames = 0, curLevel;
	
	public static final int MAX_LEVEL = 10, WIDTH = 240, HEIGHT = 240, SCALE = 3;
	
	public BufferedImage[] spacebar;
	private BufferedImage image;
	public BufferedImage redmap;
	
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

		curLevel = SaveGame.latestCompletedLevel() + 1;
		
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
		{
			randomize();
		}

		if (gameState == "NORMAL" && curLevel == MAX_LEVEL && Red.curLife == 0)
		{
			SaveGame.save(String.valueOf(curLevel));
			gameState = "TRANSITION2";
		}

		if (curLevel != MAX_LEVEL && !gameState.equals("SPLASH_SCREEN"))
		{ Sound.bgm.loop(); } 
		
		if(curLevel == MAX_LEVEL && gameState != "END")
		{
			musicFrames++;
			if (musicFrames < 2399)
			{
				Sound.boss_opening.loop();
			} 
			else
			{
				Sound.boss_opening.terminate();
				Sound.boss_loop.loop();
			}

			Sound.bgm.terminate();
		}

		if (gameState == "GAME_OVER")
		{
			this.framesGameOver++;
			if (this.framesGameOver == 30)
			{
				this.framesGameOver = 0;
				if (this.showMessageGameOver)
				{
					this.showMessageGameOver = false;
				}

				else
				{
					this.showMessageGameOver = true;
				}
			}
		} else if (gameState == "NORMAL")
		{
			for (int i = 0; i < entities.size(); i++)
			{
				Entity e = entities.get(i);
				e.tick();
			}
			if (Game.orbAtual == Game.orbContagem && curLevel != MAX_LEVEL)
			{
				if (Game.orbsPicked == 20)
				{
					Player.superHealth = true;
				}
				gameState = "TRANSITION";
			}
		}

		if (gameState == "TRANSITION")
		{
			nextlvlFrames++;
			
			if (nextlvlFrames == 1)
			{ fadeOut = true; }
			
			if (nextlvlFrames == 60)
			{
				nextlvlFrames = 0;
				
				SaveGame.save(String.valueOf(curLevel));
				
				curLevel++;
				
				if (curLevel > MAX_LEVEL)
				{ curLevel = 1; }
				
				World.restartGame(curLevel);

				gameState = "SCENE1";
			}
		}

		if (restartGame)
		{
			restartGame = false;

			Game.enemies.removeAll(enemies);
			Game.gameState = "NORMAL";
			this.blackoutFrames = 0;
			World.restartGame(curLevel);
			Game.enemies = new ArrayList<Enemy>();
			
			if (curLevel == MAX_LEVEL)
			{
				Red.curLife = 0;
				Game.gameState = "SCENE2";
				this.bossFrames = 0;
				bossTimer = 0;
			}
		}

		if (gameState == "TRANSITION2")
		{
			nextlvlFrames++;
			
			if(nextlvlFrames == 1)
			{ fadeOut = true; }
			
			if (nextlvlFrames == 60)
			{ 
				fadeIn = true;
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
			if (randFrames == 30)
			{
				Sound.bossound2.play();
			}
			if (randFrames > 30 && randFrames < 60)
			{
				color = true;
				World.restartGame(curLevel);
				bossTimer = 0;
			} else if (randFrames > 60)
			{
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
				{
					bossTimer++;
				}
				bossTimerFrames = 0;
			}
			if (bossTimer == 20)
			{
				randomize = true;
				bossTimer = 0;

				if (player.life == 2)
				{
					Sound.hit.play();
				} else if (player.life == 1)
				{
					player.life--;
					Sound.hit.play();
					this.bossFrames = 0;
				}
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
			if (gameState != "SCENE3")
			{ g.drawImage(Game.spritesheet.getSprite(43, 136, 20, 20), 158 - Camera.x, 0 - Camera.y, null); }
			if (gameState == "SCENE3")
			{
				if (sceneFrames < 240)
				{
					g.drawImage(Game.spritesheet.getSprite(43, 136, 20, 20), 158 - Camera.x, 0 - Camera.y, null);
				} else if (sceneFrames >= 240)
				{
					g.drawImage(Game.spritesheet.getSprite(120, 136, 20, 20), 158 - Camera.x, 0 - Camera.y, null);
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
		
		// TODO Level selection?
		if (gameState == "LEVEL_SELECT")
		{
			g.setFont(new Font("consolas", Font.CENTER_BASELINE, 12));
			ui.drawLevelSelectMenu(g);
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
			g.setColor(Color.white);
			pauseFrames++;
			if (pauseFrames > 30)
			{
				g.drawString("PAUSED", 92, 110);
			}
			if (pauseFrames == 60)
			{
				pauseFrames = 0;
			}
		}
		
		// Level intro
		if (gameState == "SCENE1")
		{
			boolean canChangeState = false;
			
			if (curLevel != MAX_LEVEL)
			{ player.updateCamera(); }
			
			if(!canChangeState)
			{ ++initFrames; }
			
			if (initFrames == 1)
			{ fadeIn = true; }
			
			if (initFrames == 60)
			{ 
				canChangeState = true;
				initFrames = 0;
			}
			
			if(canChangeState)
			{
				if (curLevel == MAX_LEVEL)
				{
					Camera.y = 0;
					Camera.x = 47;
					g.setColor(new Color(0, 250, 0, 200));
					
					if (rectY == 10)
					{ Sound.start.play(); }
					
					if (rectY != 215)
					{
						g.fillOval(rectX + 4, rectY, 16, 23);
						if (rectY < 215)
							rectY += 5;
					} 
					else if (rectY == 215)
					{
						Game.gameState = "SCENE2";
						rectY = 10;
					}
				}
				
				else
				{ Game.gameState = "NORMAL"; }
			}	
		}

		// Boss intro
		if (gameState == "SCENE2")
		{

			if(bossFrames == 0)
			{
				Camera.y = 0;
				Camera.x = 47;
			}
			
			bossFrames++;

			ui.renderBoss(g);

			if (bossFrames == 1)
			{
				Red.curLife = 0;
			}

			if (Red.curLife < 5)
			{
				if (bossFrames == 10)
				{
					Red.curLife++;
					Sound.boss1.play();
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
			if (bossFrames > 50)
			{
				Camera.x = 40;
				Camera.y += 1; 
			}
			
			if (bossFrames == 135)
			{
				randomize = true;
				
				if (Red.curLife == Red.redLife)
				{ gameState = "NORMAL"; }
			}
		}

		if (gameState == "SCENE3")
		{
			sceneFrames++;

			Camera.y = 0;
			Camera.x = 47;
			if (sceneFrames == 1)
			{
				player.lastDir = 1;
				Sound.scream.play();
			}
			if (sceneFrames > 10)
			{
				if (rectaY > 32)
					rectaY--;
				if (rectH < 16)
					rectH++;
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
			{
				player.lastDir = 2;
			}
			if (sceneFrames > 65)
			{
				if (player.getY() > 16)
				{
					player.y--;
					player.animate();
				}

			}
			if (sceneFrames == 100)
			{
				player.lastDir = 1;
			}
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
				player.lastDir = 2;
			}
			
			if (sceneFrames == 170)
			{
				Sound.portal.play();
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
				fadeOut = true;
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
		
		if (gameState == "NORMAL" && curLevel != MAX_LEVEL)
		{ ui.renderOrb(g); }

		if (fadeOut == true)
		{
			Graphics2D g2 = (Graphics2D) g;
			blackoutFrames++;

			boolean done = ui.fadeToBlack(g2, blackoutFrames, 1);
			
			if(done)
			{
				blackoutFrames = 0;
				fadeOut = false;
			}
		}

		if (fadeIn == true)
		{
			Graphics2D g2 = (Graphics2D) g;
			blackinFrames++;
			boolean done = ui.fadeFromBlack(g2, blackinFrames, 1);
			
			if(done)
			{
				blackinFrames = 0;
				fadeIn = false;
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

			if (tutCdown == true)
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
					Sound.keys.terminate();
				
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
				
				if(curLevel == 1)
				{ gameState = "TUT";}
			}
		}
		
		if (gameState.equals("NORMAL"))
		{ ui.render(g); }

		if (gameState.equals("GAME_OVER"))
		{
			if(!fadeOut)
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
		
		if (gameState == "NORMAL")
		{
			if (Game.orbsPicked == 20)
			{
				space++;
				if (space <= 10)
				{
					g = bs.getDrawGraphics();
					g.drawImage(spacebar[0], 101 * SCALE, (230 * SCALE) - 4, 33 * SCALE, 7 * SCALE, null);
				} 
				
				else if (space < 22)
				{ g.drawImage(spacebar[1], 101 * SCALE, (230 * SCALE) - 4, 33 * SCALE, 7 * SCALE, null); }
				
				else
				{ space = 0; }
			}
		}

		if (gameState == "MENU")
		{
			if(!fadeMenu)	
			{
				++blackoutFrames;
				
				fadeMenu = ui.fadeFromBlack(g, blackoutFrames, 3);
				
				if(fadeMenu)
				{ blackoutFrames = 0; }
			}
			
			g.setColor(new Color(120, 200, 0));
			g.setFont(new Font("consolas", Font.BOLD, 20));
			g.drawString("Version: " + GAME_VERSION, 10, Game.HEIGHT * SCALE - 18);
			g.drawString("Made by J0NATHA", 500, Game.HEIGHT * SCALE - 18);
			pauseFrames++;
			
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
		ui.drawBossAtk(g);

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
				Sound.keys.play();
				tutRight = true;
				tutCdown = true;
			}
			if (gameState == "PAUSE")
				gameState = "NORMAL";

			player.right = true;
			player.left = false;
			player.up = false;
			player.down = false;

		} else if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A)
		{
			if (gameState == "TUT" && tutLeft == false && tutCdown == false)
			{
				Sound.keys.play();
				tutCdown = true;
				tutLeft = true;
			}

			if (gameState == "PAUSE")
				gameState = "NORMAL";

			player.left = true;
			player.right = false;
			player.down = false;
			player.up = false;

		} else if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W)
		{
			if (gameState == "TUT" && tutUp == false && tutCdown == false)
			{
				Sound.keys.play();
				tutCdown = true;
				tutUp = true;
			}
			if (gameState == "PAUSE")
				gameState = "NORMAL";

			player.up = true;
			player.left = false;
			player.right = false;
			player.down = false;

		} else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S)
		{
			if (gameState == "TUT" && tutDown == false && tutCdown == false)
			{
				Sound.keys.play();
				tutCdown = true;
				tutDown = true;
			}

			if (gameState == "PAUSE")
				gameState = "NORMAL";

			player.down = true;
			player.up = false;
			player.left = false;
			player.right = false;

		}
		
		// TODO dev skip, remove
		if(e.getKeyCode() == KeyEvent.VK_R)
		{
			orbAtual = orbContagem;
		}

		if (e.getKeyCode() != 0)
		{
			if (gameState == "PAUSE")
			{ gameState = "NORMAL"; }
			
			if (gameState == "MENU")
			{
				blackoutFrames = 0;
				gameState = "SCENE1"; 
			}

			if (gameState == "GAME_OVER" && !fadeOut)
			{ Game.restartGame = true; }
		}

		if (e.getKeyCode() == KeyEvent.VK_SPACE)
		{
			if (gameState == "TUT" && tutBar == false && tutCdown == false)
			{
				Sound.keys.play();
				tutCdown = true;
				tutBar = true;
			}

			if (gameState == "NORMAL")
			{
				if (orbsPicked == 20)
				{ Player.growIt = true; }
			}
		}

		if (e.getKeyCode() == KeyEvent.VK_SHIFT)
		{
			if (gameState == "TUT" && !tutShift && !tutCdown)
			{
				Sound.keys.play();
				tutCdown = true;
				tutShift = true;
			}
			player.sneak = true;
		}

		if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
		{

			if (gameState == "PAUSE")
			{ gameState = "NORMAL"; }
			
			else if (gameState == "NORMAL")
			{ gameState = "PAUSE"; } 
		}
	}

	@Override
	public void keyReleased(KeyEvent e)
	{

		if (e.getKeyCode() == KeyEvent.VK_SHIFT)
		{
			player.sneak = false;
		}
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{

	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		if (gameState == "PAUSE")
		{ gameState = "NORMAL"; }

		if (gameState == "MENU")
		{ 
			blackoutFrames = 0;
			gameState = "SCENE1"; 
		}

		if (gameState == "GAME_OVER" && !fadeOut)
		{ Game.restartGame = true; }
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{

	}

	@Override
	public void mouseEntered(MouseEvent e)
	{

	}

	@Override
	public void mouseExited(MouseEvent e)
	{
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{

	}

	@Override
	public void mouseMoved(MouseEvent e)
	{

	}

}
