package com.bngames.entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.bngames.world.Camera;

public class SuperHealth extends Entity
{
	final BufferedImage sprite;
	public SuperHealth(int x, int y, int width, int height, BufferedImage sprite)
	{
		super(x, y, width, height, 0, sprite);
		this.sprite = sprite;
	}
	
	public void render(Graphics g)
	{
		g.drawImage(sprite, this.getX() - Camera.x, this.getY() - Camera.y, null);
	}
}
