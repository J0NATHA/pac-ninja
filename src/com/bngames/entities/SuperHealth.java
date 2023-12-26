package com.bngames.entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Random;

import com.bngames.main.Game;
import com.bngames.world.Camera;

public class SuperHealth extends Entity
{
	private int frame = 0, index = 0;
	private boolean isAnimating, increasing = true;
	BufferedImage[] sprites;
	
	public SuperHealth(int x, int y, int width, int height, BufferedImage sprite)
	{
		super(x, y, width, height, 0, sprite);
		
		sprites = new BufferedImage[]
		{ 
			sprite, // Game.spritesheet.getSprite(148, 102, 10, 11);
			Game.spritesheet.getSprite(148, 113, 10, 11),
			Game.spritesheet.getSprite(148, 124, 10, 11),
			Game.spritesheet.getSprite(148, 135, 10, 11),
			Game.spritesheet.getSprite(148, 146, 10, 11)
		};
		
		sprite = sprites[4];
	}
	
	private int animate(Graphics g)
	{	
		frame++;
		
		if(!isAnimating)
		{
			if(frame % 10 != 0 && new Random().nextInt(100) < 2)
			{
				isAnimating = true;
				frame = 0;
			}
			return index; 
		}
		
		switch(frame)
		{
			case 10:
			case 15:
			case 20:
			case 25:
			{
				if(increasing)
				{ ++index; }
				
				else
				{ --index; }
				
				break;
			}
			
			default: break;
		}
		
		if(frame == 40)
		{
			frame = 0;
			increasing = !increasing;
			isAnimating = false;
		}
		return index;
	}
	
	public void render(Graphics g)
	{
		if(Game.gameState.equals("NORMAL"))
		{ index = animate(g); }
		
		g.drawImage(sprites[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
	}
}
