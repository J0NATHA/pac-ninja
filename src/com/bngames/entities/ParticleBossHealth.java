package com.bngames.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.bngames.main.Game;
import com.bngames.world.Camera;

public class ParticleBossHealth extends Particle
{
	public ParticleBossHealth(int x, int y, int width, int height, int speed, BufferedImage sprite)
	{
		super(x, y, width, height, speed, sprite);

		lifeTime = 90;
		spd = 4;
		depth = 4;
	}

	public void tick()
	{
		x += dx * spd * 2;
		y += dy * spd * 2;
		curLife++;
		
		if (lifeTime == curLife)
		{ Game.entities.remove(this); }
	}

	public void render(Graphics g)
	{
		if(Game.gameState.equals("TRANSITION2"))
		{ return; }
		
		g.setColor(Color.red);
		g.fillRect(getX() - Camera.x - 1, getY() - Camera.y - 1, width + 2, height + 2);
		
		g.setColor(Color.black);
		g.fillRect(getX() - Camera.x, getY() - Camera.y, width, height);

	}
}