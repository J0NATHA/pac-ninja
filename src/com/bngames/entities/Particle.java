package com.bngames.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Random;

import com.bngames.main.Game;
import com.bngames.world.Camera;

public class Particle extends Entity
{
	public int lifeTime = 120;
	public int curLife = 0;

	public int spd;
	public double dx = 0;
	public double dy = 0;

	public Particle(int x, int y, int width, int height, int speed, BufferedImage sprite)
	{
		super(x, y, width, height, speed, sprite);

		spd = speed;
		
		dx = new Random().nextGaussian();
		dy = new Random().nextGaussian();
		
		depth = 2;
	}

	public void tick()
	{
		speed = spd * (1 - (double)curLife / lifeTime);
		
		x += dx * speed;
		y += dy * speed;
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
		
		g.setColor(Color.black);
		g.fillRect(this.getX() - Camera.x, this.getY() - Camera.y, width, height);

	}
}