package com.bngames.graficos;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import com.bngames.entities.Player;
import com.bngames.entities.Red;
import com.bngames.main.Game;

public class UI
{
	public int opacity = Game.bossTimer * 10;
	public final BufferedImage orb = (Game.spritesheet.getSprite(67, 3, 8, 8));
	private int frames = 0;

	public void render(Graphics g)
	{
		if (Game.curLevel != Game.MAX_LEVEL)
		{
			g.setColor(Color.white);
			g.setFont(new Font("consolas", Font.BOLD, 16));
			g.drawString(Game.orbAtual + " / " + Game.orbContagem, 360, 28);
		}
		
		g.setColor(Color.green);
		g.fillRoundRect(158, 685, Game.orbsPicked * 20, 24, 3, 3);
		g.setColor(Color.orange);
		g.drawRoundRect(157, 684, (10 * 40) + 1, 25, 5, 4);
	}

	public void renderOrb(Graphics g)
	{
		g.drawImage(orb, 107, 3, null);
	}

	public void renderBoss(Graphics g)
	{
		if (Game.curLevel == Game.MAX_LEVEL)
		{
			g.setColor(Color.black);
			g.drawRoundRect(44, 9, Red.redLife * 30 + 1, 10, 2, 2);
			g.setColor(Color.red);
			
			if (Player.growIt)
			{ g.setColor(Color.green); }
			
			g.fillRoundRect(45, 10, Red.curLife * 30, 9, 1, 1);
			g.setColor(Color.black);

			g.setColor(new Color(250, 0, 0, Game.bossTimer * 10));
			g.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
		}
	}

	public void drawBossAtk(Graphics g)
	{
		if (Game.curLevel == Game.MAX_LEVEL && Game.gameState == "NORMAL")
		{
			g.setColor(new Color(200, 10, 40));
			g.fillRoundRect(158, 645, Game.bossTimer * 20, 24, 3, 3);
			g.setColor(Color.orange);
			g.drawRoundRect(157, 644, (10 * 40) + 1, 25, 5, 4);
		}
	}

	public void drawLevelSelectMenu(Graphics g)
	{
		g.drawString("Level selector", Game.WIDTH / 2, 10);
		int level = 1;
		for (int row = 1; row < 4; ++row)
		{
			for (int col = 1; col < 4; ++col)
			{
				g.drawString("Level " + level, 50 * col, 60 * row);
				level++;
			}
		}
	}
	
	public boolean drawSplashScreen(Graphics g)
	{
		++frames;
		
		g.setColor(new Color(200, 200, 200));
		g.fillRect(0, 0, Game.WIDTH * Game.SCALE, Game.HEIGHT * Game.SCALE);
		drawLogo(g);
	
		if(frames <= secondsToFrames(1.8))
		{ fadeFromBlack(g, frames, 1.8); }
		
		if(frames >= secondsToFrames(3))
		{

			boolean done = fadeToBlack(g, frames, 3);
			
			if(done)
			{
				frames = 0;
				return true;
			}
		}

		return false;
	}
	
	public boolean fadeFromBlack(Graphics g, int frames, double duration)
	{
		final double alphaPerFrame = 255.0 / secondsToFrames(duration);	
		
		int currentFrame = (frames % secondsToFrames(duration)) + 1;
		
		int alpha = (int)(255 - (alphaPerFrame * frames));
		
		if(currentFrame <= secondsToFrames(duration))
		{ g.setColor(new Color(0, 0, 0, alpha)); }
		
		g.fillRect(0, 0, Game.WIDTH * Game.SCALE, Game.HEIGHT * Game.SCALE);
		
		if(currentFrame == secondsToFrames(duration))
		{ return true; }
		
		return false;
	}
	
	public boolean fadeToBlack(Graphics g, int frames, double duration)
	{
		final double alphaPerFrame = 255.0 / secondsToFrames(duration);
		
		int currentFrame = (frames % secondsToFrames(duration)) + 1;
		
		int alpha = (int)(alphaPerFrame * currentFrame);

		
		if(currentFrame <= secondsToFrames(duration))
		{ g.setColor(new Color(0, 0, 0, alpha)); }
		
		g.fillRect(0, 0, Game.WIDTH * Game.SCALE, Game.HEIGHT * Game.SCALE);
		
		if(currentFrame == secondsToFrames(duration))
		{ return true; }
		
		return false;
	}
	
	private void drawLogo(Graphics g)
	{
		try
		{
			String path = "/LogoBN.png";
			
			if(Game.curLevel == Game.MAX_LEVEL)
			{ path = "/LogoRN.png"; }
			
			final BufferedImage logo = ImageIO.read(getClass().getResource(path));
			
			final int logoSize = 480;
			
			g.drawImage(
					logo, 
					(int)(Game.WIDTH * .5),
					(int)(Game.HEIGHT * .5),
					logoSize,
					logoSize,
					null
			);
		}
		
		catch(Exception e)
		{ e.printStackTrace(); }
	}
	
	private int secondsToFrames(double seconds)
	{
		// 60 frames == 1 sec
		return (int)(seconds * 60);
	}
}