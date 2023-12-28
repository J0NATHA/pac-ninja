package com.bngames.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.bngames.main.Game;
import com.bngames.main.Sound;
import com.bngames.world.Camera;

public class Red extends Entity
{
	private boolean cycle = true, cycleBack = false;
	public boolean damage = false;

	private int animationFrames = 0, spriteIndex = 0;
	public static int maxLife = 5, curLife = 0;

	private BufferedImage[] sprites;

	public Red(double x, double y, int width, int height, double speed, BufferedImage sprite)
	{
		super(x, y, width, height, speed, sprite);

		sprites = new BufferedImage[]
		{
			Game.spritesheet.getSprite(2, 135, 14, 16),
			Game.spritesheet.getSprite(18, 135, 14, 16),
			Game.spritesheet.getSprite(67, 135, 14, 16),
			Game.spritesheet.getSprite(85, 135, 14, 16)	
		};
		
		depth = 3;
	}

	public void tick()
	{
		if (isCollidingWithPlayer() && Game.player.isDamaged == false)
		{
			Game.player.isDamaged = true;
			Game.player.life--;
			Sound.get().hit.play();
		}
	}

	public boolean isCollidingWithPlayer()
	{
		int width = 16 * (maxLife - curLife + 1);
		int x = this.getX() - width / 2 + 8;
		int y = this.getY() - width / 2 + 8;
		
		Rectangle enemyCurrent = new Rectangle(x, y, width, width);
		
		Rectangle player = new Rectangle(
				Game.player.getX() - Game.player.maskX,
				Game.player.getY() - Game.player.maskY - 2,
				Game.player.maskW, 
				Game.player.maskH
		);

		return enemyCurrent.intersects(player);
	}

	private void animate()
	{
		animationFrames++;
		if(animationFrames == 10)
		{
			animationFrames = 0;
			if(spriteIndex < 3 && cycle == true)
			{
				spriteIndex++;
				
				if (spriteIndex == 3)
				{
					cycle = false;
					cycleBack = true;
				}
			}
			else if (spriteIndex > 0 && cycleBack == true)
			{
				spriteIndex--;
				
				if (spriteIndex == 0)
				{
					cycle = true;
					cycleBack = false;
				}
			}
		}
	}

	public void render(Graphics g)
	{
		if (Game.curLevel == Game.MAX_LEVEL && Game.hideSprite == false 
				&& Game.gameState != "LEVEL_SELECT_CHANGED")
		{
			g.drawImage(sprites[spriteIndex], this.getX() - Camera.x, this.getY() - Camera.y, null);
			
			if (Game.gameState != "SCENE3" && !Game.gameState.equals("PAUSE"))
			{ animate(); }
		}
		
		if ((curLife > 0 && (Game.gameState == "NORMAL" || Game.gameState == "PAUSE"))
			|| Game.gameState.equals("SCENE2") || Game.gameState.equals("SCENE1"))
		{
			int diameter = 36 * Game.bossTimer;
			int x = this.getX() - Camera.x - diameter / 2 + 8;
			int y = this.getY() - Camera.y - diameter / 2 + 8;
			
			g.setColor(new Color(250, 0, 0, 100));
			g.fillRoundRect(x, y, diameter, diameter, diameter, diameter);
			
			diameter = 20 * (maxLife - curLife + 1);
			x = this.getX() - Camera.x - diameter / 2 + 7;
			y = this.getY() - Camera.y - diameter / 2 + 8;
			
			g.setColor(new Color(255, 0, 0, 190));
			g.fillRoundRect(x, y, diameter, diameter, diameter, diameter);	
		}
	}
}
