package com.bngames.entities;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.List;

import com.bngames.main.Game;
import com.bngames.world.Camera;
import com.bngames.world.Node;
import com.bngames.world.Vector2i;
import com.bngames.world.World;

public class Entity
{
	public static final BufferedImage ORB_SPRITE = Game.spritesheet.getSprite(67, 3, 8, 8);
	public static final BufferedImage ORB_HUD = Game.spritesheet.getSprite(36, 20, 8, 8);
	public static final BufferedImage ENEMY_EN = Game.spritesheet.getSprite(67, 3, 8, 8);
	public static final BufferedImage ENEMY_ENEMY = Game.spritesheet.getSprite(67, 3, 8, 8);
	public static final BufferedImage SUPER_HEALTH = Game.spritesheet.getSprite(148, 102, 10, 11);

	public double x;
	public double y;
	protected double z;
	protected double speed;
	protected int width;
	protected int height;
	public int depth;
	protected List<Node> path;
	private BufferedImage sprite;

	public Entity(double x, double y, int width, int height, double speed, BufferedImage sprite)
	{
		this.speed = speed;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.sprite = sprite;
	}

	public static Comparator<Entity> nodeSorter = new Comparator<Entity>()
	{
		@Override
		public int compare(Entity n0, Entity n1)
		{
			if (n1.depth < n0.depth)
			{ return +1; }
			if (n1.depth > n0.depth)
			{ return -1; }
			
			return 0;
		}
	};

	public void updateCamera()
	{
		Camera.x = Camera.clamp(getX() - (Game.WIDTH / 2), 0, World.WIDTH * 16 - Game.WIDTH);
		Camera.y = Camera.clamp(getY() - (Game.HEIGHT / 2), 0, World.HEIGHT * 16 - Game.HEIGHT);
	}

	public void setX(int newX)
	{
		this.x = newX;
	}

	public void setY(int newY)
	{
		this.y = newY;
	}

	public int getX()
	{
		return (int)x;
	}

	public int getY()
	{
		return (int)y;
	}

	public int getWidth()
	{
		return this.width;
	}

	public int getHeight()
	{
		return this.height;
	}
	
	public BufferedImage getSprite()
	{
		return this.sprite;
	}

	public void tick()
	{
	}

	public static double calculateDistance(int x1, int y1, int x2, int y2)
	{
		return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
	}

	public void followPath(List<Node> path)
	{
		if(path == null || path.size() <= 0)
		{ return; }
		 
		Vector2i target = path.get(path.size() - 1).tile;

		if (x < target.x * 16)
		{ x += speed; } 
		
		else if (x > target.x * 16)
		{ x -= speed; }
	
		if (y < target.y * 16)
		{ y += speed; } 
		
		else if (y > target.y * 16)
		{ y -= speed; }
		
		if (x == target.x * 16 && y == target.y * 16)
		{ path.remove(path.size() - 1); }
	}

	public static boolean isColliding(Entity e1, Entity e2)
	{
		Rectangle e1Mask = new Rectangle(e1.getX(), e1.getY(), e1.getWidth(), e1.getHeight());
		Rectangle e2Mask = new Rectangle(e2.getX(), e2.getY(), e2.getWidth(), e1.getHeight());
		
		if (e1Mask.intersects(e2Mask))
		{
			return true;
		}
		return false;
	}

	public void render(Graphics g)
	{
		g.drawImage(sprite, this.getX() - Camera.x, this.getY() - Camera.y, null);
	}
}
