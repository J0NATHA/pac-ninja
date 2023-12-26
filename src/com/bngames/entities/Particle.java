package com.bngames.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Random;

import com.bngames.main.Game;
import com.bngames.world.Camera;

public class Particle extends Entity
{
	protected int lifeTime = 120;
	protected int curLife = 0;
	protected int spd;
	protected double dx = 0;
	protected double dy = 0;

	public Particle(int x, int y, int width, int height, int speed, BufferedImage sprite)
	{
		super(x, y, width, height, speed, sprite);
		
		dx = new Random().nextGaussian();
		dy = new Random().nextGaussian();
		
		spd = speed;
		depth = 2;
	}

	public void tick()
	{
		speed = spd * (1 - (double)curLife / lifeTime);
		
		x += dx * speed;
		y += dy * speed;
		curLife++;
		
		if (lifeTime == curLife)
		{ Game.entities.remove(this); }
	}

	public void render(Graphics g)
	{
		g.setColor(Color.green);
		g.fillRect(getX() - Camera.x - 1, getY() - Camera.y - 1, width + 2, height + 2);
		
		g.setColor(Color.black);
		g.fillRect(getX() - Camera.x, getY() - Camera.y, width, height);
	}
}