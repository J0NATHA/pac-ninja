package com.bngames.entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Random;

import com.bngames.main.Game;
import com.bngames.world.Camera;

public class Orb extends Entity
{

	private BufferedImage[] sprites;
	private int frames = 0, index = 0, maxIndex = 5;
	private boolean shine = false;

	public Orb(double x, double y, int width, int height, double speed, BufferedImage sprite)
	{
		super(x, y, width, height, speed, sprite);

		sprites = new BufferedImage[]
		{
			Game.spritesheet.getSprite(67, 3, 8, 8),
			Game.spritesheet.getSprite(75, 3, 8, 8),
			Game.spritesheet.getSprite(83, 3, 8, 8),
			Game.spritesheet.getSprite(91, 3, 8, 8),
			Game.spritesheet.getSprite(99, 3, 8, 8),
			Game.spritesheet.getSprite(6, 20, 8, 8)
		};
		
		depth = 0;
	}

	public void tick()
	{
		if(new Random().nextInt(100) < 2)
		{
			if(new Random().nextInt(100) < 50)
			{
				shine = true;
				index = 0;
			}
		}

		if(shine)
		{
			frames++;
			
			if(frames == 5)
			{
				frames = 0;
				
				if(index < maxIndex)
				{ index++; }
			}
		}
	}

	public void render(Graphics g)
	{
		g.drawImage(sprites[index], getX() - Camera.x, getY() - Camera.y, null);
	}
}
