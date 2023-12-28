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
import com.bngames.main.SaveGame;
import com.bngames.main.Sound;
import com.bngames.world.Camera;
import com.bngames.world.World;

public class Player extends Entity
{
	public UI ui;

	public int  maskX = -3, maskY, maskH = 16, maskW = 10, blackoutFrames, frames, 
				index, maxIndex = 4, maxFrames = 10, currentDirection, lastDirection, 
				orbFrames, orbIndex, orbMax = 3, invFrames, crushTime, maxCrushTime = 35,
				life = 2, soundFrames;
	
	public static boolean superHealth;
	
	public boolean crushOrb, right, up, left, down, changedDir, isDamaged, hitWall;
	
	public BufferedImage[] upDir, downDir, leftDir, rightDir, orbCrush, wallHold;
	public BufferedImage deadSprite;

	public Player(int x, int y, int width, int height, int speed, BufferedImage sprite)
	{
		super(x, y, width, height, speed, sprite);
		
		wallHold = new BufferedImage[] 
		{
			Game.spritesheet.getSprite(48, 96, 16, 16),
			Game.spritesheet.getSprite(48, 80, 16, 16),
			Game.spritesheet.getSprite(32, 96, 16, 16),
			Game.spritesheet.getSprite(32, 80, 16, 16)				
		};

		upDir = new BufferedImage[]
		{
			Game.spritesheet.getSprite(65, 48, 14, 16),
			Game.spritesheet.getSprite(81, 48, 14, 16),
			Game.spritesheet.getSprite(97, 48, 14, 16),
			Game.spritesheet.getSprite(113, 48, 14, 16)				
		};

		downDir = new BufferedImage[]
		{
			Game.spritesheet.getSprite(65, 32, 14, 16),	
			Game.spritesheet.getSprite(81, 32, 14, 16),
			Game.spritesheet.getSprite(97, 32, 14, 16),
			Game.spritesheet.getSprite(113, 32, 14, 16)
		}; 
		
		leftDir = new BufferedImage[]
		{
			Game.spritesheet.getSprite(65, 80, 14, 16),
			Game.spritesheet.getSprite(80, 80, 14, 16),
			Game.spritesheet.getSprite(96, 80, 14, 16),
			Game.spritesheet.getSprite(112, 80, 14, 16)
		};

		rightDir = new BufferedImage[]
		{
			Game.spritesheet.getSprite(64, 96, 14, 16),
			Game.spritesheet.getSprite(80, 96, 14, 16),
			Game.spritesheet.getSprite(96, 96, 14, 16),
			Game.spritesheet.getSprite(112, 96, 14, 16)
		};

		orbCrush = new BufferedImage[]
		{
			Game.spritesheet.getSprite(55, 15, 16, 16),
			Game.spritesheet.getSprite(70, 15, 16, 16),
			Game.spritesheet.getSprite(85, 15, 16, 16)
		};
		
		deadSprite = Game.spritesheet.getSprite(22, 16, 8, 16);
		
		ui = new UI();
		
		depth = 1;
	}
	
	public int getXTile()
	{
		return getX() / 16 * 16;
	}
	
	public int getYTile()
	{
		return getY() / 16 * 16;
	}
	
	public boolean hasSuperHealth()
	{ return superHealth || life == 3; }

	public void tick()
	{
		updateCamera();

		if (hasSuperHealth())
		{ superHealth(); }

		if (!isDamaged)
		{ speed = 1; }
		
		if (crushOrb && Game.orbsPicked > 0)
		{ Game.orbsPicked--; }
		
		if(!crushOrb)
		{ 
			if(right)
			{
				currentDirection = 1;
				
				if(World.isFree((int)(x + speed), getY()))
				{
					x += speed;
					changedDir = true;
				}
				
				else
				{ 
					fitIn(lastDirection); 

					if(World.isFree((int)(x + 1), getY()))
					{ x += 1; }
				}	
			}

			else if(left)
			{
				currentDirection = -1;
				
				if(World.isFree((int) (x - speed), getY()))
				{
					x -= speed;
					changedDir = true;
				}
				
				else
				{ 
					fitIn(lastDirection);

					if(World.isFree((int)(x - 1), getY()))
					{ x -= 1; }
				}
			}

			if(up)
			{
				currentDirection = 2;
				
				if (World.isFree(this.getX(), (int)(y - speed)))
				{
					y -= speed;
					changedDir = true;
				}
				
				else
				{ 
					fitIn(lastDirection); 

					if(World.isFree(getX(), (int)(y - 1)))
					{ y -= 1; }
				}
			}

			else if(down)
			{
				currentDirection = -2;
				
				if (World.isFree(this.getX(), (int)(y + speed)))
				{
					y += speed;
					changedDir = true;
				}
				
				else
				{ 
					fitIn(lastDirection);
					
					if(World.isFree(getX(), (int)(y + 1)))
					{ y += 1; }
				}
			}
		}

		if(isDamaged)
		{
			if (hasSuperHealth())
			{
				superHealth = false;
				life = 2;
			}

			invFrames++;

			if(invFrames < 30)
			{ speed = 4; }

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
			Game.fadeToBlack = true;
			Game.gameState = "GAME_OVER";
			SaveGame.saveDeath();
		}
	}
	
	private void fitIn(int dir)
	{
		final double MOVE_RATE = .125;
		
		// Prioritize closest diagonal
		dir = checkDiagonals(currentDirection);
		
		switch(dir)
		{
			case 1:
			{
				// right
				if (World.isFree((int) (x + speed), this.getY()))
				{ x += MOVE_RATE; }
				
				return;
			}
			case -1:
			{
				// left
				if (World.isFree((int) (x - speed), this.getY()))
				{ x -= MOVE_RATE; }
				
				return;
			}
			case 2:
			{
				// up
				if (World.isFree(this.getX(), (int) (y - speed)))
				{ y -= MOVE_RATE; }
				
				return;
			}
			case -2:
			{
				// down
				if (World.isFree(this.getX(), (int) (y + speed)))
				{ y += MOVE_RATE; }
				
				return;
			}
			default: return;
		}
	}
	
	private int checkDiagonals(int dir)
	{
		switch(dir)
		{
			case 1:
			{ 
				// Down-Right
				if(World.isFree(getXTile() + 16, getYTile() + 16))
				{ return -2; }
				
				// Up-Right
				if(World.isFree(getXTile() + 16, getYTile()))
				{ return 2; } 
				
				return dir;
			}
			case -1:
			{
				// Down-Left
				if(World.isFree(getXTile() - 16, getYTile() + 16))
				{ return -2; }
				
				// Up-Left
				if(World.isFree(getXTile() - 16, getYTile()))
				{ return 2; } 
				
				return dir;	
			}
			case 2:
			{
				// Up-Right
				if(World.isFree(getXTile() + 16, getYTile() - 16))
				{ return 1; }
				
				// Up-Left
				if(World.isFree(getXTile(), getYTile() - 16))
				{ return -1; } 
				
				return dir;
			}
			case -2:
			{
				// Down-Right
				if(World.isFree(getXTile() + 16, getYTile() + 16))
				{ return 1; }
				
				// Down-Left
				if(World.isFree(getXTile(), getYTile() + 16))
				{ return -1; } 
				
				return dir;
			}
			default: return dir;
		}		
	}

	public void superHealth()
	{
		this.life = 3;
	}

	public void getOrb()
	{
		for(int i = 0; i < Game.entities.size(); ++i)
		{
			Entity entity = Game.entities.get(i);
			
			if(!Entity.isColliding(this, entity))
			{ continue; }
			
			if(entity instanceof Orb)
			{
				
				Game.orbAtual++;
				Sound.get().pickup.play();
				World.generatePickupParticle(4, entity.getX() + 4, entity.getY() + 4);
				Game.entities.remove(entity);
				
				if(Game.orbsPicked == 20)
				{ continue; }
				
				// Game.orbsPicked < 20
				Game.orbsPicked++;
				
				// In the final level, there's a 35% chance of reducing the
				// boss timer by 2 upon picking up an orb
				if (Game.curLevel == Game.MAX_LEVEL && 
					new Random().nextInt(100) < 35 &&
					Game.bossTimer > 1)
				{ Game.bossTimer -= 2; }
			}	
			else if(entity instanceof SuperHealth && Entity.isColliding(this, entity))
			{
				Game.entities.remove(entity);
				superHealth = true;
				Sound.get().pickupSuperHealth.play();
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
		if (hitWall)
		{
			Sound.get().hitwall.play();
			hitWall = false;
		}
	}

	public void animateOrb()
	{
		orbFrames++;
		
		if (orbFrames != 5)
		{ return; }
		
		orbFrames = 0;
		
		if (orbIndex < 2)
		{ orbIndex++; }
		
		else
		{ World.generateParticle(200, getX() + 8, getY() + 8); }
	}

	private void drawSuperHealth(Graphics g)
	{
		g.setColor(new Color(255, 255, 0, 150));
		g.fillOval(Game.player.getX() - Camera.x - Game.player.maskX - 4,
				Game.player.getY() - Camera.y - 3 - Game.player.maskY, Game.player.maskW + 6, Game.player.maskH + 4);
	}

	public void render(Graphics g)
	{
		if (life > 0)
		{
			List<String> states = 
					new ArrayList<String>(
							Arrays.asList("NORMAL", "PAUSE", "GAME_OVER", "SCENE2") );
			
			if (states.contains(Game.gameState))
			{
				if (!crushOrb)
				{
					depth = 1;
					if (currentDirection == 0)
					{ g.drawImage(downDir[0], this.getX() - Camera.x, this.getY() - Camera.y, null); }
					
					if (currentDirection == 1)
					{
						if (World.isFree((int) (x + speed), getY()))
						{
							hitWall = true;
							animate();
							g.drawImage(rightDir[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
						} 
						
						else
						{
							g.drawImage(this.wallHold[0], this.getX() - Camera.x, this.getY() - Camera.y, null);
							hitWall();
						}
					} 
					
					else if (currentDirection == -1)
					{
						if (World.isFree((int) (x - speed), getY()))
						{
							hitWall = true;
							animate();
							g.drawImage(leftDir[index], this.getX() - Camera.x, this.getY() - Camera.y, null);

						}
						
						else
						{
							g.drawImage(this.wallHold[1], this.getX() - Camera.x, this.getY() - Camera.y, null);
							hitWall();
						}
					} 
					
					else if (currentDirection == 2)
					{
						if (World.isFree(getX(), (int) (y - speed)))
						{
							hitWall = true;
							animate();
							g.drawImage(upDir[index], this.getX() - Camera.x, this.getY() - Camera.y, null);

						} 
						
						else
						{
							g.drawImage(this.wallHold[3], this.getX() - Camera.x, this.getY() - Camera.y, null);
							hitWall();
						}
					} 
					
					else if (currentDirection == -2)
					{
						if (World.isFree(getX(), (int) (y + speed)))
						{
							hitWall = true;
							animate();
							g.drawImage(downDir[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
						} 
						
						else
						{
							g.drawImage(this.wallHold[2], this.getX() - Camera.x, this.getY() - Camera.y, null);
							hitWall();
						}
					}
				} 
				else
				{
					g.drawImage(orbCrush[orbIndex], this.getX() - Camera.x, this.getY() - Camera.y, null);
					animateOrb();
				}

				if (hasSuperHealth())
				{ drawSuperHealth(g); }

				if (crushOrb)
				{
					depth = 4;
					crushTime++;
					
					if (crushTime <= maxCrushTime)
					{
						Graphics2D g2 = (Graphics2D) g;
						
						blackoutFrames++;
						
						if(blackoutFrames == 20)
						{ Sound.get().growIt.play(); }
						
						double completionPercentage = blackoutFrames / 20.0;
						
						if(completionPercentage > 1.0) 
						{ completionPercentage = 1.0; }
						
						final int diameter = (int)(Math.pow(Game.WIDTH * 2.5, completionPercentage));
						final int alpha = (int)(180 * completionPercentage);
					
						g2.setColor(new Color(0, 255, 0, alpha));
						
						g2.fillOval(
								getX() - Camera.x - diameter / 2 + 8,
								getY() - Camera.y - diameter / 2 + 8, 
								diameter, diameter);
					} 
					else
					{
						if (Game.curLevel == Game.MAX_LEVEL)
						{
							if (Red.curLife > 1)
							{ Game.randomize = true; }

							if (Red.curLife > 0)
							{
								Sound.get().bossound3.play();
								World.generateParticleBossHealth(800, Camera.x + 10 + (Red.curLife * 30), Camera.y + 15);
								Red.curLife--;
							}
						}

						blackoutFrames = 0;
						crushOrb = false;
						
						if(life == 1)
						{ life++; }
						
						orbIndex = 0;
						crushTime = 0;
						Game.orbsPicked = 0;
					}
				}
				if (isDamaged && life > 0)
				{
					g.setColor(new Color(0, 255, 0, 150));
					g.fillOval(
							Game.player.getX() - Camera.x - Game.player.maskX - 4,
							Game.player.getY() - Camera.y - 3 - Game.player.maskY, 
							Game.player.maskW + 6,
							Game.player.maskH + 4
					);
				}

				if (life == 1 && Game.curLevel != Game.MAX_LEVEL)
				{
					g.setColor(new Color(255, 0, 0, 50));
					g.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
				}
			}
		}

		if (life == 0)
		{ g.drawImage(deadSprite, getX() + 3 - Camera.x, getY() - Camera.y, null); }

		if (Game.gameState == "SCENE3" && Game.sceneFrames < 248)
		{
			if (Game.sceneFrames == 1)
			{
				x = 100;
				y = 30;
			}

			if (currentDirection == 1)
			{ g.drawImage(rightDir[index], this.getX() - Camera.x, this.getY() - Camera.y, null); }

			else if (currentDirection == -1)
			{ g.drawImage(leftDir[index], this.getX() - Camera.x, this.getY() - Camera.y, null); }
			
			else if (currentDirection == 2)
			{ g.drawImage(upDir[index], this.getX() - Camera.x, this.getY() - Camera.y, null); }
			
			else if (currentDirection == -2)
			{ g.drawImage(downDir[index], this.getX() - Camera.x, this.getY() - Camera.y, null); }
		}
	}
}
