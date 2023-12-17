package com.bngames.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.bngames.graficos.UI;
import com.bngames.main.Game;
import com.bngames.main.Sound;
import com.bngames.world.Camera;
import com.bngames.world.World;

public class Player extends Entity
{
	public UI ui;

	public int Pmaskx = -3, Pmasky, Pmaskh = 16, Pmaskw = 10, blackoutFrames, frames, 
				index, maxIndex = 4, maxFrames = 10, lastDir, orbFrames, orbIndex,
				orbMax = 3, invFrames, rectTime, rectMax = 25, life = 2, soundFrames ;
	
	public static boolean growIt, superHealth;
	
	public boolean right, up, left, down, changedDir, isDamaged, hitOnce, sneak;
	
	public BufferedImage[] upDir, downDir, leftDir, rightDir, orbX, wallHold;

	public Player(int x, int y, int width, int height, int speed, BufferedImage sprite)
	{
		super(x, y, width, height, speed, sprite);
		
		wallHold = new BufferedImage[4];
		wallHold[0] = Game.spritesheet.getSprite(48, 96, 16, 16);
		wallHold[1] = Game.spritesheet.getSprite(48, 80, 16, 16);
		wallHold[2] = Game.spritesheet.getSprite(32, 96, 16, 16);
		wallHold[3] = Game.spritesheet.getSprite(32, 80, 16, 16);

		upDir = new BufferedImage[4];
		upDir[0] = Game.spritesheet.getSprite(65, 48, 14, 16);
		upDir[1] = Game.spritesheet.getSprite(81, 48, 14, 16);
		upDir[2] = Game.spritesheet.getSprite(97, 48, 14, 16);
		upDir[3] = Game.spritesheet.getSprite(113, 48, 14, 16);

		downDir = new BufferedImage[4];
		downDir[0] = Game.spritesheet.getSprite(65, 32, 14, 16);
		downDir[1] = Game.spritesheet.getSprite(81, 32, 14, 16);
		downDir[2] = Game.spritesheet.getSprite(97, 32, 14, 16);
		downDir[3] = Game.spritesheet.getSprite(113, 32, 14, 16);

		leftDir = new BufferedImage[4];
		leftDir[0] = Game.spritesheet.getSprite(65, 80, 14, 16);
		leftDir[1] = Game.spritesheet.getSprite(80, 80, 14, 16);
		leftDir[2] = Game.spritesheet.getSprite(96, 80, 14, 16);
		leftDir[3] = Game.spritesheet.getSprite(112, 80, 14, 16);

		rightDir = new BufferedImage[4];
		rightDir[0] = Game.spritesheet.getSprite(64, 96, 14, 16);
		rightDir[1] = Game.spritesheet.getSprite(80, 96, 14, 16);
		rightDir[2] = Game.spritesheet.getSprite(96, 96, 14, 16);
		rightDir[3] = Game.spritesheet.getSprite(112, 96, 14, 16);

		orbX = new BufferedImage[3];
		orbX[0] = Game.spritesheet.getSprite(55, 15, 16, 16);
		orbX[1] = Game.spritesheet.getSprite(70, 15, 16, 16);
		orbX[2] = Game.spritesheet.getSprite(85, 15, 16, 16);
		
		ui = new UI();
	}

	public boolean hasSuperHealth()
	{ return superHealth; }

	public void tick()
	{
		depth = 1;
		updateCamera();

		if (hasSuperHealth())
		{ superHealth(); }

		if (!isDamaged)
		{
			if (sneak)
			{ speed = 0.25; } 
			
			else
			{ speed = 1; }
		}
		
		if (!growIt)
		{
			if (right)
			{
				lastDir = 1;
				if (World.isFree((int) (x + speed), this.getY()))
				{
					x += speed;
					changedDir = true;
				}
			}

			else if (left)
			{
				lastDir = -1;
				if (World.isFree((int) (x - speed), this.getY()))
				{
					x -= speed;
					changedDir = true;
				}
			}

			if (up)
			{
				lastDir = 2;
				if (World.isFree(this.getX(), (int) (y - speed)))
				{
					y -= speed;
					changedDir = true;
				}
			}

			else if (down)
			{
				lastDir = -2;
				if (World.isFree(this.getX(), (int) (y + speed)))
				{
					y += speed;
					changedDir = true;
				}
			}
		}

		if (isDamaged)
		{
			if (hasSuperHealth())
			{
				superHealth = false;
				life = 2;
			}

			invFrames++;

			if (invFrames < 30)
			{ speed = 3; }

			else
			{
				speed = 1;
				isDamaged = false;
				invFrames = 0;
			}
		}

		getOrb();

		if (life <= 0)
		{
			life = 0;
			Game.fadeOut = true;
			Game.gameState = "GAME_OVER";
		}
	}

	public void superHealth()
	{
		this.life = 3;
	}

	public void getOrb()
	{
		for (int i = 0; i < Game.entities.size(); i++)
		{
			Entity current = Game.entities.get(i);
			if (current instanceof Tree)
			{
				if (Entity.isColliding(this, current))
				{
					Game.entities.remove(i);
					Game.orbAtual++;
					
					Sound.pickup.play();
					
					if (Game.orbsPicked < 20)
					{
						Game.orbsPicked++;
						if (Game.curLevel == Game.MAX_LEVEL)
						{
							if (new Random().nextInt(100) < 35)
							{
								if (Game.bossTimer > 1)
								{
									Game.bossTimer -= 2;
								}
							}
						}
					}
					return;
				}
			}
			
			if(current instanceof SuperHealth && Entity.isColliding(this, current))
			{
				superHealth = true;
				Game.entities.remove(i);
			}
		}
	}

	public void animate()
	{
		if (Game.gameState == "NORMAL" || Game.gameState == "SCENE3")
		{ frames++; }
		
		if (frames == maxFrames)
		{
			frames = 0;
			index++;
			
			if (index == maxIndex)
			{ index = 0; }
		}
	}

	public void hitWall()
	{
		if (hitOnce)
		{
			Sound.hitwall.play();
			hitOnce = false;
		}
	}

	public void animateOrb()
	{
		orbFrames++;
		if (orbFrames == 5)
		{
			orbFrames = 0;
			
			if (orbIndex < 2)
			{ orbIndex++; }
			else
			{ World.generateParticle(5, (int) x + 10, (int) y + 10); }
		}
	}

	private void drawSuperHealth(Graphics g)
	{
		g.setColor(new Color(255, 255, 0, 150));
		g.fillOval(Game.player.getX() - Camera.x - Game.player.Pmaskx - 4,
				Game.player.getY() - Camera.y - 3 - Game.player.Pmasky, Game.player.Pmaskw + 6, Game.player.Pmaskh + 4);
	}

	public void render(Graphics g)
	{
		if (life > 0)
		{
			List<String> states = 
					new ArrayList<String>(
							Arrays.asList("NORMAL", "PAUSE", "GAME_OVER") );
			
			if (states.contains(Game.gameState))
			{
				if (growIt == false)
				{
					if (lastDir == 0)
					{ g.drawImage(downDir[0], this.getX() - Camera.x, this.getY() - Camera.y, null); }
					
					if (lastDir == 1)
					{
						if (World.isFree((int) (x + speed), getY()))
						{
							hitOnce = true;
							animate();
							g.drawImage(rightDir[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
						} 
						
						else
						{
							g.drawImage(this.wallHold[0], this.getX() - Camera.x, this.getY() - Camera.y, null);
							hitWall();
						}
					} 
					
					else if (lastDir == -1)
					{
						if (World.isFree((int) (x - speed), getY()))
						{
							hitOnce = true;
							animate();
							g.drawImage(leftDir[index], this.getX() - Camera.x, this.getY() - Camera.y, null);

						}
						
						else
						{
							g.drawImage(this.wallHold[1], this.getX() - Camera.x, this.getY() - Camera.y, null);
							hitWall();
						}
					} 
					
					else if (lastDir == 2)
					{
						if (World.isFree(getX(), (int) (y - speed)))
						{
							hitOnce = true;
							animate();
							g.drawImage(upDir[index], this.getX() - Camera.x, this.getY() - Camera.y, null);

						} 
						
						else
						{
							g.drawImage(this.wallHold[3], this.getX() - Camera.x, this.getY() - Camera.y, null);
							hitWall();
						}
					} 
					
					else if (lastDir == -2)
					{
						if (World.isFree(getX(), (int) (y + speed)))
						{
							hitOnce = true;
							animate();
							g.drawImage(downDir[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
							animate();
						} 
						
						else
						{
							g.drawImage(this.wallHold[2], this.getX() - Camera.x, this.getY() - Camera.y, null);
							hitWall();
						}
					}
				} 
				
				// growIt == true
				else
				{
					g.drawImage(orbX[orbIndex], this.getX() - Camera.x, this.getY() - Camera.y, null);
					animateOrb();
				}

				if (hasSuperHealth())
				{ drawSuperHealth(g); }

				if (growIt)
				{
					rectTime++;
					
					if (rectTime > 15)
					{
						if (rectTime <= rectMax)
						{
							Graphics2D g2 = (Graphics2D) g;
							blackoutFrames++;
							
							if (blackoutFrames > 0 && blackoutFrames < 5)
							{
								Sound.growIt.play();
								g2.setColor(new Color(0, 250, 0, 50));
								g2.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
							} 
							
							else if (blackoutFrames >= 5 && blackoutFrames < 10)
							{
								g2.setColor(new Color(0, 250, 0, 100));
								g2.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
							} 
							
							else if (blackoutFrames >= 10 && blackoutFrames < 15)
							{
								g2.setColor(new Color(0, 250, 0, 200));
								g2.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
							} 
							
							else if (blackoutFrames >= 15)
							{
								g2.setColor(new Color(0, 250, 0));
								g2.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
							}
						} 
						
						else
						{
							if (Game.curLevel == Game.MAX_LEVEL)
							{
								if (Red.curLife > 1)
								{
									Game.randomize = true;
								}

								if (Red.curLife == 1)
								{
									superHealth = true;
								}

								if (Red.curLife > 0)
								{
									Sound.bossound3.play();
									World.generateParticle2(200, Camera.x + 10 + (Red.curLife * 30), Camera.y + 15);
									Red.curLife--;
								}
							}

							blackoutFrames = 0;
							growIt = false;
							
							if (life == 1)
							{ life++; }
							
							orbIndex = 0;
							rectTime = 0;
							Game.orbsPicked = 0;
						}
					}
				}
				if (isDamaged)
				{
					if (life == 1)
					{
						g.setColor(new Color(0, 255, 0, 100));
						g.fillOval(Game.player.getX() - Camera.x - Game.player.Pmaskx - 4,
								Game.player.getY() - Camera.y - 3 - Game.player.Pmasky, Game.player.Pmaskw + 6,
								Game.player.Pmaskh + 4);
					}
				}

				if (life == 1 && Game.curLevel != Game.MAX_LEVEL)
				{
					g.setColor(new Color(255, 0, 0, 50));
					g.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
				}

			}
		}

		if (life == 0)
		{
			g.drawImage(Game.spritesheet.getSprite(22, 16, 8, 16), this.getX() - Camera.x, this.getY() - Camera.y,
					null);
		}

		if (Game.gameState == "SCENE3" && Game.sceneFrames < 248)
		{
			if (Game.sceneFrames == 1)
			{
				x = 100;
				y = 30;
			}

			if (lastDir == 1)
			{ g.drawImage(rightDir[index], this.getX() - Camera.x, this.getY() - Camera.y, null); }

			else if (lastDir == -1)
			{ g.drawImage(leftDir[index], this.getX() - Camera.x, this.getY() - Camera.y, null); }
			
			else if (lastDir == 2)
			{ g.drawImage(upDir[index], this.getX() - Camera.x, this.getY() - Camera.y, null); }
			
			else if (lastDir == -2)
			{ g.drawImage(downDir[index], this.getX() - Camera.x, this.getY() - Camera.y, null); }
		}
	}
}
