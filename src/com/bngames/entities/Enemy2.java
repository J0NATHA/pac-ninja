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

public class Enemy2 extends Entity
{

	public boolean ghostMode2 = false;
	private int frames = 0, maxFrames = 5, index = 0, maxIndex = 3;
	private int maskx = 4, masky = 2, maskw = 7, maskh = 13;
	private BufferedImage[] sprites;

	public Enemy2(int x, int y, int width, int height, int speed, BufferedImage sprite)
	{
		super(x, y, width, height, speed, null);

		sprites = new BufferedImage[3];
		sprites[0] = Game.spritesheet.getSprite(1, 114, 15, 15);
		sprites[1] = Game.spritesheet.getSprite(20, 114, 15, 15);
		sprites[2] = Game.spritesheet.getSprite(37, 114, 15, 15);

	}

	void chaseStart()
	{

		if (path == null || path.size() == 0)
		{
			Vector2i start = new Vector2i((int) (x / 16), (int) (y / 16));
			Vector2i end = new Vector2i((int) (Game.player.x / 16), (int) (Game.player.y / 16));
			path = AStar.findPath(Game.world, start, end);

		}

		if (new Random().nextInt(100) < 25)
			followPath(path);
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
		if (Game.gameState == "NORMAL" || Game.gameState == "MENU")
			frames++;
		if (frames == maxFrames)
		{
			frames = 0;
			index++;
			if (index >= maxIndex)
				index = 0;
		}

	}

	public boolean isCollidingWithPlayer()
	{
		Rectangle enemyCurrent = new Rectangle(this.getX() + maskx, this.getY() + masky, maskw, maskh);
		Rectangle player = new Rectangle(Game.player.getX() - Game.player.Pmaskx,
				Game.player.getY() - Game.player.Pmasky, Game.player.Pmaskw, Game.player.Pmaskh);

		return enemyCurrent.intersects(player);
	}

	public void render(Graphics g)
	{

//			g.setColor(Color.white);
//			g.fillRect(this.getX()+maskx -Camera.x, this.getY()+masky -Camera.y, maskw, maskh);

		g.drawImage(sprites[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
		if (ghostMode2 == false)
			animate();
	}

}
