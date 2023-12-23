package com.bngames.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.bngames.main.Game;
import com.bngames.world.Camera;

public class ParticlePickup extends Particle 
{
	public ParticlePickup(int x, int y, int width, int height, int speed, BufferedImage sprite) 
	{
		super(x, y, width, height, speed, sprite);
		depth = 2;
		lifeTime = 60;
	}

	public void tick()
	{
		x += dx / 3;
		y += dy / 3;
		curLife++;
		
		if (lifeTime == curLife)
		{
			Game.entities.remove(this);
		}
	}

	public void render(Graphics g)
	{
		g.setColor(Color.green);
		g.fillRect(this.getX() - Camera.x - 1, this.getY() - Camera.y - 1, width + 2, height + 2);
		
		g.setColor(new Color(131, 59, 15, 255));
		g.fillRect(this.getX() - Camera.x, this.getY() - Camera.y, width, height);

	}
}
