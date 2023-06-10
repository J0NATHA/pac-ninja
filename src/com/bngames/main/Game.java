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

public class Game extends Canvas implements Runnable, KeyListener, MouseListener,MouseMotionListener
{
	private static final long serialVersionUID = 1L;
	public static JFrame frame;
	private boolean isRunning=true, tutUp, tutDown, tutLeft,tutRight,
			tutBar, tutShift, tutCdown, color;
	public static boolean randomize, hideSprite, spawnEnemies, restartGame;
	private Thread thread;
	public static final int WIDTH = 240;
	public static final int HEIGHT = 240;
	public static final int SCALE = 3;
	public boolean saveGame, spawnBlue, npcSpawn, showMessageGameOver, fadeOut, fadeIn;
	public int mx,my;
	public static int curLevel = 1;
	private static int maxLevel = 6;
	public static int redFrames=0, bossTimer=0, bossTimerFrames=0, sceneFrames=0;
	private int framesGameOver=0, bossFrames=0, randFrames=0;
	private int blackoutFrames=0;
	private int space=0;
	private int blackinFrames=0;
	private int nextlvlFrames=0;
	private int pauseFrames=0, musicFrames=0;
	private int tut=0;
	
	public BufferedImage[] spacebar;
	private BufferedImage image;
	public BufferedImage redmap;
	public int frames=0, rectX=115, rectY=10, rectH=1, rectaY=47;
	public int xx,yy;
	public static int orbContagem=0, orbAtual=0, orbsPicked=0;

	public static List<Entity> entities;
	public static List<Enemy> enemies;
	public static Spritesheet spritesheet;
	public static World world;
	public static Player player;
	public static Enemy enemy;
	public static Red red;

	public UI ui;
	
	public static String gameState="TUT";
	
	public Game() {
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		this.setPreferredSize(new Dimension(WIDTH*SCALE,HEIGHT*SCALE));
		initFrame();	
		image = new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_RGB);
		image = new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_RGB);
		try {
			redmap=ImageIO.read(getClass().getResource("/red.png"));
		} 
		catch (IOException e1) {
			e1.printStackTrace();
		}
		
		spritesheet = new Spritesheet("/spritesheet.png");
		player = new Player(0,0,16,16,1,spritesheet.getSprite(32, 0, 16, 16));
		entities = new ArrayList<Entity>();
		enemies = new ArrayList<Enemy>();
		world = new World("/level1.png");
		ui = new UI();
		spacebar = new BufferedImage[2];
		spacebar[0] = spritesheet.getSprite(101, 116, 40, 11);
		spacebar[1] = spritesheet.getSprite(59, 116, 40, 11);
		red = new Red(0, 0, 14, 16, 1, null);

		entities.add(player);	
	}
	
	public void initFrame() {
	frame = new JFrame("Pac-Ninja");
	frame.add(this);
	frame.setResizable(false);
	frame.pack();
	Image imagem = null;
	
	try {
		imagem = ImageIO.read(getClass().getResource("/icon.png"));	
	}
	catch(IOException e ) {
		e.printStackTrace();
	}
	
	frame.setIconImage(imagem);
	frame.setAlwaysOnTop(true);
	frame.setLocationRelativeTo(null);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.setVisible(true);
	}
	
	public synchronized void start() {
		thread = new Thread(this);
		isRunning=true;
		thread.start();
	}
	
	public synchronized void stop() {
		isRunning=false;
		try {
			thread.join();
		} 
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[]args) {
		Game game = new Game();
		game.start();
	}

	
	
	public void tick() {
		if(randomize) {
			randomize();
		}
		
		if(gameState=="NORMAL" && curLevel==6 && Red.curLife==0) {
			gameState="TRANSITION2";
		}
			
			if(curLevel != 6) {
				Sound.bgm.loop();
			}
			else{
				musicFrames++;
				if(musicFrames<2399) {
					Sound.boss_opening.loop();
				}
				else {
					Sound.boss_opening.terminate();
					Sound.boss_loop.loop();
				}
				Sound.bgm.terminate();
			}
			
			if(gameState=="GAME_OVER") {
				this.framesGameOver++;
				if(this.framesGameOver==30) {
					this.framesGameOver=0;
					if(this.showMessageGameOver)
						this.showMessageGameOver=false;
					else {
						this.showMessageGameOver=true;
					}
				}
			}
		else if(gameState=="NORMAL") {
			 for (int i=0; i<entities.size(); i++) {
				 Entity e=entities.get(i);
				 e.tick();
			 }
			 if(Game.orbAtual == Game.orbContagem && curLevel!=6) {
				 if (Game.orbsPicked == 20)
				 {
					 Player.superHealth = true;
				 }
				 gameState="TRANSITION";
			 }
		}
		 if(gameState=="TRANSITION") {
			fadeOut=true;
			nextlvlFrames++;
			if(nextlvlFrames>40){
				fadeIn=true;
			}
			if(blackinFrames>24) {
				blackoutFrames=0;
				nextlvlFrames=0;
				curLevel++;
				if(curLevel>maxLevel) {
					curLevel=1;
				}
				String newWorld = "level"+curLevel+".png";
				World.restartGame(newWorld);
		
				gameState="SCENE1";
			}	
		}
		
	
		 if(restartGame) {
			restartGame = false;
	
			Game.enemies.removeAll(enemies);
			Game.gameState="NORMAL";
			this.blackoutFrames=0;
			String newWorld="level"+curLevel+".png";
			World.restartGame(newWorld);
			Game.enemies= new ArrayList<Enemy>();
			if(curLevel==6) {
				 Red.curLife=0;
				 Game.gameState="SCENE2";
				 this.bossFrames=0;
				 bossTimer=0;
			}		
		 }
		 
		 if(gameState=="TRANSITION2") {
			fadeOut=true;
			nextlvlFrames++;
			if(nextlvlFrames>40){
				fadeIn=true;
			}
			if(blackinFrames>24) {
				blackoutFrames=0;
				nextlvlFrames=0;
				gameState="SCENE3";
			}
	 	}	 
	}
			 
	public void randomize() {
		if(randomize) {
			randFrames++;
			if(randFrames==30) {
				Sound.bossound2.play();
			}
			if(randFrames>30 && randFrames<60) {
				color=true;
				String newWorld="level"+curLevel+".png";
				World.restartGame(newWorld);
				bossTimer=0;
			}
			else if(randFrames>60){
				randomize = false;
				color = false;
				randFrames=0;
			}
		}	
	}
	
	
	public void countdown() {
		if(gameState=="NORMAL") {
			bossTimerFrames++;
			if(bossTimerFrames==60) {
				if(bossTimer<20) {
					bossTimer++;
				}
			bossTimerFrames=0;
			}
			if(bossTimer==20) {
				randomize=true;
				bossTimer=0;
		
				if(player.life==2) {
					Sound.hit.play();
				}
				else if (player.life==1) {
					player.life--;
					Sound.hit.play();
					this.bossFrames=0;
				}
			}
		}
	}

	public void render() {	
		BufferStrategy bs=this.getBufferStrategy();
		if(bs==null) {
			this.createBufferStrategy(3);
			return;
		}	
		Graphics g = image.getGraphics();
		g.setColor(new Color(0,0,0));
		g.fillRect(0,0,WIDTH,HEIGHT);
		
		world.render(g);
		Collections.sort(entities, Entity.nodeSorter);
		if(Game.curLevel==6) {
			if(gameState!="SCENE3")
				g.drawImage(Game.spritesheet.getSprite(43, 136, 20, 20), 158-Camera.x, 0-Camera.y, null);
			if(gameState=="SCENE3" ) {
				if(sceneFrames<240) {
					g.drawImage(Game.spritesheet.getSprite(43, 136, 20, 20), 158-Camera.x, 0-Camera.y, null);
				}
				else if(sceneFrames>=240) {
					g.drawImage(Game.spritesheet.getSprite(120, 136, 20, 20), 158-Camera.x, 0-Camera.y, null);
				}
			}
		}
		
		for (int i=0; i<entities.size();i++) {
			Entity e=entities.get(i);
			e.render(g);
		}
		
		
		if(color) {
			g.setColor(new Color(250,0,0,200));
			g.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
		}
		
		if(gameState=="MENU") {
			g.setColor(new Color(0,0,0,100));
			g.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
			g.setColor(Color.green);
			g.fillRect(89, 80, 62, 16);
			g.setColor(new Color(210,140,0));
			g.fillRect(90, 81, 60, 14);
			g.setFont(new Font("arial", Font.CENTER_BASELINE, 12));
			g.setColor(Color.green);
			g.drawString("Pac-Ninja", (Game.WIDTH)/2-26, (Game.HEIGHT)/2-28);
		}
		
		if(curLevel==6) {
			if(gameState=="NORMAL") {
				spawnEnemies=true;
				g.setColor(Color.white);
				countdown();
				ui.renderBoss(g);
			}
		}
		
		if(gameState=="PAUSE") {
			g.setColor(Color.white);
			pauseFrames++;
			if(pauseFrames>30) {
				g.drawString("PAUSADO", 92, 110);
			}
			if(pauseFrames==60) {
				pauseFrames=0;
			}
		}
		if(gameState=="SCENE1") {
			if(curLevel!=6)
				player.updateCamera();
			
				if(curLevel==1) {
				g.setColor(new Color(0,250,0,200));
				if(rectY==10)
				Sound.start.play();
				if(rectY!=110) {
				g.fillOval(rectX-4, rectY, 16, 23);
				if(rectY<106)
					rectY+=5;
				}else if(rectY==110) {
					Game.gameState="NORMAL";
					rectY=10;
				}
				}else if(curLevel==2) {
		
					g.setColor(new Color(0,250,0,200));
					if(rectY==10)
					Sound.start.play();
					if(rectY!=90) {
					g.fillOval(rectX+4, rectY, 16, 23);
					if(rectY<90)
						rectY+=5;
					}else if(rectY==90) {
						Game.gameState="NORMAL";
						rectY=10;
					}
				}
				else if(curLevel==3) {
					
					g.setColor(new Color(0,250,0,200));
					if(rectY==10)
					Sound.start.play();
					if(rectY!=120) {
					g.fillOval(rectX+4, rectY, 16, 23);
					if(rectY<120)
						rectY+=5;
					}else if(rectY==120) {
						Game.gameState="NORMAL";
						rectY=10;
					}
				}else if(curLevel==4) {
		
					g.setColor(new Color(0,250,0,200));
					if(rectY==10)
					Sound.start.play();
					if(rectY!=65) {
					g.fillOval(rectX+4, rectY, 16, 23);
					if(rectY<65)
						rectY+=5;
					}else if(rectY==65) {
						Game.gameState="NORMAL";
						rectY=10;
					}
				}
				if(curLevel==5) {
					g.setColor(new Color(0,250,0,200));
					if(rectY==10)
					Sound.start.play();
					if(rectY!=20) {
					g.fillOval(rectX+6, rectY, 16, 23);
					if(rectY<20)
						rectY+=5;
					}else if(rectY==20) {
						Game.gameState="NORMAL";
						rectY=10;
					}
					}
				else if(curLevel==6) {
					Camera.y=0;
					Camera.x=47;
					g.setColor(new Color(0,250,0,200));
					if(rectY==10)
					Sound.start.play();
					if(rectY!=215) {
					g.fillOval(rectX+4, rectY, 16, 23);
					if(rectY<215)
						rectY+=5;
					}else if(rectY==215) {
						Game.gameState="SCENE2";
						rectY=10;
					}
				}	
		 }
		
		if(gameState=="SCENE2") {
			
			Camera.y=0;
			Camera.x=47;
			bossFrames++;
			
			ui.renderBoss(g);
			
			if(bossFrames==1) {
				Red.curLife=0;
			}
			
			if(Red.curLife<5) {
				if(bossFrames==10) {
				Red.curLife++;
				Sound.boss1.play();
				Camera.x+=5;
				}
				if(bossFrames==20) {
					Red.curLife++;
					Camera.x-=5;
				}
				if(bossFrames==30) {
					Red.curLife++;
					Camera.x+=5;
				}
				if(bossFrames==40) {
					Red.curLife++;
					Camera.x-=5;
				}
				if(bossFrames==50) {
					Red.curLife++;
					Camera.x+=5;
				}
				}
					if(bossFrames>50){
					Camera.y++;
			
			}
			if(bossFrames==120) {
				randomize=true;
			if(Red.curLife==Red.redLife)
				gameState="NORMAL";
			
			}
		}
	
		if(gameState=="SCENE3") {	
			sceneFrames++;
			
			Camera.y=0;
			Camera.x=47;
			if(sceneFrames==1) {
				player.lastDir=1;
			Sound.scream.play();
			}
			if(sceneFrames>10) {
				if(rectaY>32)
					rectaY--;
				if(rectH<16)
					rectH++;
				g.setColor(new Color (0,240,0,100));
				g.fillRect(113, rectaY, 14, rectH);
				

			}
			if(sceneFrames>30) {
				hideSprite=true;
				g.drawImage(spritesheet.getSprite(103, 135, 14, 16), 113, 	32, null);
				if(player.getX()<128){
					player.animate();
					player.x++;
				}
			}if(sceneFrames==63) {
				player.lastDir=2;
			}if(sceneFrames>65) {
				if(player.getY()>16) {
							player.y--;
							player.animate();
				}
					
			
			}if(sceneFrames==100) {
				player.lastDir=1;
			}if(sceneFrames>100) {
				if(player.getX()<161) {
					player.x++;
					player.animate();
				}
			}if(sceneFrames==162) {
				player.index=0;
				player.lastDir=2;
			}if(sceneFrames==170) {
				Sound.portal.play();
				Camera.y+=5;
			}
			if(sceneFrames==180)
				Camera.x+=5;
			if(sceneFrames==190)
				Camera.y-=5;
			if(sceneFrames==200)
				Camera.x-=5;
			if(sceneFrames>240) {
				if(player.getY()>2) {
					player.y--;
				}
			}if(sceneFrames>250) {
				fadeOut=true;
				gameState="END";
				sceneFrames=0;
			}
			
			
		}
		
	
		
		
		
		if(gameState=="TUT") {
			g.setColor(new Color(0,0,0,245));
			g.fillRect(0, 0, WIDTH*SCALE, HEIGHT*SCALE);
			g.setColor(Color.gray);
			g.drawRect(109, 89, 12, 12);
			g.drawRect(109, 104, 12, 12);
			g.drawRect(124, 104, 12, 12);
			g.drawRect(94, 104, 12, 12);
			g.drawRect(94, 119, 42, 12);
			g.drawRect(54, 119, 21, 12);
			
			g.setColor(Color.white);
			g.drawRect(110, 90, 10, 10);
			g.drawRect(110, 105, 10, 10);
			g.drawRect(125, 105, 10, 10);
			g.drawRect(95, 105, 10, 10);
			g.drawRect(95, 120, 40, 10);
			g.drawRect(55, 120, 19, 10);
		
			
			if(tutUp) {
				g.setColor(Color.red);
				g.fillRect(111, 91, 9, 9);
			
			}
			if(tutDown) {
				g.setColor(Color.green);
				g.fillRect(111, 106, 9, 9);
			}
			if(tutLeft ) {
				g.setColor(Color.blue);
				g.fillRect(96, 106, 9, 9);
			}
			if(tutRight ) {
				g.setColor(Color.yellow);
				g.fillRect(126, 106, 9, 9);
			}
			if(tutBar ) {
				g.setColor(Color.pink);
				g.fillRect(96, 121, 39, 9);
			}
			
			if(tutShift ) {
				g.setColor(Color.darkGray);
				g.fillRect(56, 121, 18, 9);
			}
			
			
			
			if(tutCdown==true) {
				tut++;
				if(tut>=60) {
					tutCdown=false;
					Sound.keys.terminate();
					if(tutUp && tutDown && tutLeft && tutRight && tutBar && tutShift) {
						gameState="MENU";
					}
				}
			}
			else
				tut=0;
		}
		
		
		
		if(Game.player.life==1) {
			redFrames++;
			if(redFrames<=15)
			g.drawImage(redmap, 0, 0, null);
		else if(redFrames>=45)
			redFrames=0;
			
		}
		if(gameState=="NORMAL" && curLevel!=6)
		ui.renderOrb(g);
		
		if(gameState=="GAME_OVER"){
			
			fadeOut=true;
		}
		
		if(fadeOut==true) {
			fadeOut=false;
			Graphics2D g2= (Graphics2D) g;
			blackoutFrames++;
			if(blackoutFrames>1 && blackoutFrames<10) {
				g2.setColor(new Color(0,0,0,100));
			g2.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
			}
			else if(blackoutFrames>=10 && blackoutFrames<20) {
				g2.setColor(new Color(0,0,0,150));
			g2.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
			}
			else if(blackoutFrames>=20 && blackoutFrames<30) {
				g2.setColor(new Color(0,0,0,200));
			g2.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
			}
			else if(blackoutFrames>=30) {
				g2.setColor(new Color(0,0,0));
			g2.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
			}
		}
		
		
		if(fadeIn==true) {
			
			Graphics2D g2= (Graphics2D) g;
			blackinFrames++;
			if(blackinFrames>1 && blackinFrames<10) {
				g2.setColor(new Color(0,0,0,200));
			g2.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
			
			}
			else if(blackinFrames>=10 && blackinFrames<20) {
				g2.setColor(new Color(0,0,0,250));
			g2.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
			
			}
			else if(blackinFrames>=20 && blackinFrames<30) {
				g2.setColor(new Color(0,0,0,100));
			g2.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
		
			}
			else if(blackinFrames>=30) {
				g2.setColor(new Color(0,0,0,0));
			g2.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
		
			}
			if (blackinFrames>30){
				fadeIn=false;
				blackinFrames=0;
			}
			
		}

		g.dispose();
		g=bs.getDrawGraphics();
		
		g.drawImage(image,0,0,WIDTH*SCALE,HEIGHT*SCALE,null);
		
		
		if(gameState=="NORMAL")
		ui.render(g);
		if(Game.gameState=="GAME_OVER") {
			g.setColor(Color.gray);
			g.setFont(new Font("arial", Font.BOLD, 33));
			g.drawString("Voce foi derrotado!", 198, Game.HEIGHT*SCALE/2+2);
			g.setColor(Color.red);
			g.setFont(new Font("arial", Font.BOLD, 33));
			g.drawString("Voce foi derrotado!", 200, Game.HEIGHT*SCALE/2);
		
		
		if(showMessageGameOver) {
			
			g.setColor(Color.gray);
			g.setFont(new Font("arial", Font.BOLD, 20));
			g.drawString("Clique para tentar novamente", 212, Game.HEIGHT*SCALE/2+32);
			g.setColor(Color.red);
			g.setFont(new Font("arial", Font.BOLD, 20));
			g.drawString("Clique para tentar novamente", 214, Game.HEIGHT*SCALE/2+30);
		}	
		}
		if(gameState=="NORMAL") {
		 if(Game.orbsPicked==20) {
			 space++;
			 if(space<=10) {
					g=bs.getDrawGraphics();
					g.drawImage(spacebar[0], 101*SCALE, (230*SCALE)-4,33*SCALE,7*SCALE, null);
			 }
			 else if(space>10 && space <22)
				 g.drawImage(spacebar[1], 101*SCALE, (230*SCALE)-4,33*SCALE,7*SCALE, null);
			 else
				 space=0;
		 }
		}
		
		if(gameState=="MENU") {
			g.setColor(new Color(120,200,0));
			g.setFont(new Font("arial", Font.BOLD, 20));
			g.drawString("Criado por BlackNinjaCX", 255, Game.HEIGHT*SCALE-18);
			pauseFrames++;
			if(pauseFrames>30) {
			g.drawString("Clique para comecar!", 258, Game.HEIGHT*SCALE/2);
			}if(pauseFrames==60)
				pauseFrames=0;
		}
		
		if(curLevel==6 && gameState=="NORMAL") {
			g.setColor(Color.black);
		g.setFont(new Font("arial", Font.BOLD, 26 ));
		g.drawString("RedNinja", 295, 52);
		}
		
		if(gameState=="END") {
			sceneFrames++;
			
			
			g.setColor(Color.black);
			g.fillRect(0, 0, WIDTH*SCALE, HEIGHT*SCALE);
			
			if(sceneFrames>30 && sceneFrames<150) {
				g.setColor(Color.white);
				g.setFont(new Font("arial", Font.LAYOUT_LEFT_TO_RIGHT, 26 ));
				g.drawString("Continua...", WIDTH+50 ,  HEIGHT+60   ) ;
			}
			if(sceneFrames>150 && sceneFrames < 270) {
				g.setColor(Color.white);
				g.setFont(new Font("arial", Font.LAYOUT_LEFT_TO_RIGHT, 26 ));
				g.drawString("Criado por Jonatha S.M.", WIDTH-10 ,  HEIGHT+60   ) ;
			}
			if(sceneFrames>270 && sceneFrames <390) {
				g.setColor(Color.white);
				g.setFont(new Font("arial", Font.LAYOUT_LEFT_TO_RIGHT, 26 ));
				g.drawString("Fire sprite by Chromaeleon", WIDTH-20, HEIGHT+60);
				g.drawString("Cyberpunk moonlight sonata by Joth", WIDTH-75, HEIGHT+90);
			}
			if(sceneFrames>410) {
				g.setColor(Color.white);
				g.setFont(new Font("arial", Font.LAYOUT_LEFT_TO_RIGHT, 26 ));
				g.drawString("Obrigado por jogar.", WIDTH-20, HEIGHT+60);
			}if(sceneFrames>500) {
				System.exit(1);
			}
		}
		ui.drawBossAtk(g);
		
		
		bs.show();
		
	}
	
	public void run() {
		long lastTime=System.nanoTime();
		double amountofTicks = 60.0;
		double ns= 1000000000 / amountofTicks;
		double delta = 0;
		
	

		double timer = System.currentTimeMillis();
		requestFocus();
		while(isRunning) {
			long now = System.nanoTime();
			delta+= (now - lastTime) / ns;
			lastTime=now;
			if (delta >= 1) {
				tick();
				render();
				frames++;
				delta--;
			}
			if (System.currentTimeMillis() - timer >=1000) {
				//System.out.println("FPS: "+frames);
			
				frames=0;
				timer+=1000;
			}
		}
		stop();
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void keyPressed(KeyEvent e) {	
		
		
		
		if(e.getKeyCode()==KeyEvent.VK_RIGHT  || 
			e.getKeyCode()==KeyEvent.VK_D) {
			if(gameState=="TUT" && tutRight==false && tutCdown==false) {
				Sound.keys.play();
			tutRight=true;
			tutCdown=true;
			}
			if(gameState =="PAUSE") 
				gameState= "NORMAL";
			
			player.right=true;
			player.left=false;
			player.up=false;
			player.down=false;
			
		}else if(e.getKeyCode()==KeyEvent.VK_LEFT  || 
				e.getKeyCode()==KeyEvent.VK_A) {
			if(gameState=="TUT" && tutLeft==false && tutCdown==false) {
				Sound.keys.play();
				tutCdown=true;
			tutLeft=true;
			}
			
			if(gameState =="PAUSE") 
				gameState= "NORMAL";
			
			player.left=true;
			player.right=false;
			player.down=false;
			player.up=false;
			
		}else if(e.getKeyCode()==KeyEvent.VK_UP  || 
			e.getKeyCode()==KeyEvent.VK_W) {
			if(gameState=="TUT" && tutUp==false && tutCdown==false) {
				Sound.keys.play();
				tutCdown=true;
			tutUp=true;
			}
			if(gameState =="PAUSE") 
				gameState= "NORMAL";
			
			player.up=true;
			player.left=false;
			player.right=false;
			player.down=false;
			
		}else if(e.getKeyCode()==KeyEvent.VK_DOWN  || 
			e.getKeyCode()==KeyEvent.VK_S) {
			if(gameState=="TUT" && tutDown==false && tutCdown==false) {
				Sound.keys.play();
				tutCdown=true;
				tutDown=true;
			}
			
			if(gameState =="PAUSE") 
				gameState= "NORMAL";
			
			player.down=true;
			player.up=false;
			player.left=false;
			player.right=false;
			
			
		}
		
		if(e.getKeyCode() != 0)
		{
			if( gameState =="PAUSE") {
				gameState= "NORMAL";
				
			}
			if(gameState == "MENU" )
				gameState = "SCENE1";
			
			if(gameState == "GAME_OVER") {
				Game.restartGame=true;
			}
		}
		
		if(e.getKeyCode()==KeyEvent.VK_SPACE) {
			if(gameState=="TUT" && tutBar==false && tutCdown==false) 
			{
				Sound.keys.play();
				tutCdown=true;
				tutBar=true;
			}
			
			if(gameState=="NORMAL") 
			{
				if(orbsPicked==20) {
					Player.growIt=true;
				}
			}
		}
		
		if(e.getKeyCode() == KeyEvent.VK_SHIFT) 
		{
			if(gameState=="TUT" && !tutShift && !tutCdown) 
			{
				Sound.keys.play();
				tutCdown=true;
				tutShift=true;
			}
			player.sneak=true;
		}
		
		
		if(e.getKeyCode()==KeyEvent.VK_ESCAPE) {
			
			
			if(gameState=="PAUSE")
				gameState="NORMAL";
			else if(gameState=="NORMAL")
				gameState="PAUSE";
		}	
	}
	@Override
	public void keyReleased(KeyEvent e) {
		
		 if(e.getKeyCode()==KeyEvent.VK_SHIFT) {
			player.sneak=false;
		 }
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if( gameState =="PAUSE") {
			gameState= "NORMAL";
		}
		
		if(gameState == "MENU" )
			gameState="SCENE1";
		
		if(gameState=="GAME_OVER") {
			Game.restartGame=true;
		}
	}

		
	

	@Override
	public void mouseReleased(MouseEvent e) {
		
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {

		
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
				
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		this.mx=e.getX();
		this.my=e.getY();
	}

}
