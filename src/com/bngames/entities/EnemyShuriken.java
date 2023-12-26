package com.bngames.entities;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Random;

import com.bngames.main.Game;
import com.bngames.main.Sound;
import com.bngames.world.AStar;
import com.bngames.world.Camera;
import com.bngames.world.Vector2i;

public class EnemyShuriken extends Entity
{
	private int rotation, maskx = 1, masky = 1, maskw = 11, maskh = 11;

	public EnemyShuriken(int x, int y, int width, int height, int speed, BufferedImage sprite)
	{
		super(x, y, width, height, speed, sprite);
	}
	
	private void chaseStart()
	{

		if (path == null || path.size() == 0)
		{
			Vector2i start = new Vector2i((int) (x / 16), (int) (y / 16));
			Vector2i end = new Vector2i((int) (Game.player.x / 16), (int) (Game.player.y / 16));
			path = AStar.findPath(Game.world, start, end);

		}

		if(new Random().nextInt(100) < 25)
		{ followPath(path); }
		
		if(new Random().nextInt(100) < 5)
		{
			Vector2i start = new Vector2i((int) (x / 16), (int) (y / 16));
			Vector2i end = new Vector2i((int) (Game.player.x / 16), (int) (Game.player.y / 16));
			path = AStar.findPath(Game.world, start, end);
		}
	}

	public void tick()
	{
		depth = 1;

		chaseStart();

		if (isCollidingWithPlayer() && !Game.player.isDamaged)
		{
			Game.player.life -= 2;
			Sound.get().hit.play();
			Game.player.isDamaged = true;	
		}
	}

	public void animate()
	{
		if(!Game.gameState.equals("PAUSE"))
		{ rotation += 15; }
		
		if(rotation > 360) 
		{ rotation = 0; }
	}

	public boolean isCollidingWithPlayer()
	{
		Rectangle enemyCurrent = new Rectangle(getX() + maskx, getY() + masky, maskw, maskh);
		
		Rectangle player = new Rectangle(
					Game.player.getX() - Game.player.maskX,
					Game.player.getY() - Game.player.maskY, 
					Game.player.maskW,
					Game.player.maskH
				);

		return enemyCurrent.intersects(player);
	}

	public void render(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;
		
		int centerX = getX() + 6 - Camera.x;
		int centerY = getY() + 6 - Camera.y;
		
		AffineTransform rotateTransform = new AffineTransform();
		AffineTransform zeroTransform = new AffineTransform();
		
		zeroTransform.rotate(Math.toRadians(0), centerX, centerY);
		rotateTransform.rotate(Math.toRadians(rotation), centerX, centerY);

		g2.setTransform(rotateTransform);
		g.drawImage(getSprite(), getX() - Camera.x, getY() - Camera.y, null);
		g2.setTransform(zeroTransform);
		
		animate(); 
	}
}
