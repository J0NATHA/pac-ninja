package com.bngames.entities;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Random;

import com.bngames.main.Game;
import com.bngames.main.Sound;
import com.bngames.world.AStar;
import com.bngames.world.Camera;
import com.bngames.world.Vector2i;

public class EnemySpectre extends Entity
{
	private int stunTime;
	public boolean stunned;
	private int frames, orbFrames = 0, maxFrames = 11, index = 0, maxIndex = 4, stunFrames = 0;
	private double followRate = 1.0;
	private BufferedImage[] sprites;

	public EnemySpectre(int x, int y, int width, int height, double speed, BufferedImage sprite)
	{
		super(x, y, width, height, 1, null);

		sprites = new BufferedImage[] 
		{
			Game.spritesheet.getSprite(128, 4, 16, 17),
			Game.spritesheet.getSprite(144, 7, 16, 17),
			Game.spritesheet.getSprite(128, 23, 16, 17),
			Game.spritesheet.getSprite(144, 31, 16, 17)	
		};
	}

	private void chaseStart()
	{
		if (path == null || path.size() == 0)
		{
			Vector2i start = new Vector2i((int) (x / 16), (int) (y / 16));
			Vector2i end = new Vector2i((int) (Game.player.x / 16), (int) (Game.player.y / 16));
			path = AStar.findPath(Game.world, start, end);
		}

		if (new Random().nextInt(100) < (int) (65 * followRate))
		{
			followPath(path);
		}

		if (new Random().nextInt(100) < 5)
		{
			Vector2i start = new Vector2i((int) (x / 16), (int) (y / 16));
			Vector2i end = new Vector2i((int) (Game.player.x / 16), (int) (Game.player.y / 16));
			path = AStar.findPath(Game.world, start, end);
		}
	}

	public void tick()
	{
		depth = 0;
		if (Game.curLevel == Game.MAX_LEVEL && Game.gameState == "NORMAL")
		{ bossBattleMode(); }
		
		if (Game.restartGame)
		{
			Game.enemies.clear();
			Game.entities.clear();
		}

		if(stunned == false)
		{ chaseStart(); }

		if(Game.player.crushOrb)
		{
			stunned = true;
			
			if (Game.orbsPicked > 0)
			{
				orbFrames++;
				
				if (orbFrames == 4)
				{ Game.orbsPicked--; }
				
				if (orbFrames == 5)
				{ orbFrames = 0; }
			}
		}

		if(stunned)
		{
			stunFrames++;
			
			if(stunFrames <= 60 * 4)
			{ stunned = true; }
			
			else
			{
				stunFrames = 0;
				stunned = false;
			}
		}

		if (isCollidingWithPlayer() && !stunned && !Game.player.isDamaged)
		{
			Sound.get().hit.play();
			Game.player.life--;
			Game.player.isDamaged = true;
		}
	}

	private void bossBattleMode()
	{
		if (Game.bossTimer != 0)
		{ followRate = 1.0 - (Game.bossTimer * 0.025); }
	}

	private void animate()
	{
		if(!Game.gameState.equals("PAUSE"))
		{ frames++; }
		
		if(frames == maxFrames)
		{
			frames = 0;
			index++;
			
			if (index >= maxIndex)
			{ index = 0; }
		}
	}

	public boolean isCollidingWithPlayer()
	{
		Rectangle enemyCurrent = new Rectangle(getX() + 2, getY(), 12, 17);
		
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
		if (!stunned)
		{
			stunTime = 0;
			g.drawImage(sprites[index], getX() - Camera.x, getY() - Camera.y, null);
			animate();
		}
		
		else
		{
			stunTime++;
			if (stunTime < 15)
			{ g.drawImage(sprites[index], getX() - Camera.x, getY() - Camera.y, null); }
			
			else if (stunTime >= 15)
			{ g.drawImage(Game.spritesheet.getSprite(108, 4, 16, 17), getX() - Camera.x, getY() - Camera.y, null); }
		}
	}
}
