package com.bngames.graficos;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import com.bngames.entities.Player;
import com.bngames.entities.Red;
import com.bngames.main.Game;
import com.bngames.main.SaveGame;

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
	
	public int animateSpaceBar(Graphics g, int space, BufferedImage[] spacebar)
	{
		int SCALE = Game.SCALE;

		space++;
		
		if (space <= 10)
		{ g.drawImage(spacebar[0], 101 * SCALE, (230 * SCALE) - 4, 33 * SCALE, 7 * SCALE, null); } 
		
		else if (space < 22)
		{ g.drawImage(spacebar[1], 101 * SCALE, (230 * SCALE) - 4, 33 * SCALE, 7 * SCALE, null); }
		
		else
		{ space = 0; }
		
		return space;
	}
	
	public int animateSpaceBar(Graphics g, int space, BufferedImage[] spacebar, int x, int y, int scale)
	{
		int SCALE = Game.SCALE + scale;

		space++;
		
		if (space < 15)
		{ g.drawImage(spacebar[0], x, y, 33 * SCALE, 7 * SCALE, null); } 
		
		else if (space < 60)
		{ g.drawImage(spacebar[1], x, y, 33 * SCALE, 7 * SCALE, null); }
		
		else
		{ 
			g.drawImage(spacebar[1], x, y, 33 * SCALE, 7 * SCALE, null);
			space = 0;
		}
		
		return space;
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

	public void drawLevelSelectMenu(Graphics g, int level)
	{
		g.setFont(new Font("consolas", Font.CENTER_BASELINE, 52));
		g.setColor(Color.WHITE);
		g.drawString("Level selection", 144, 159);
		g.drawString("Level selection", 146, 161);
		
		g.setColor(Color.BLACK);
		g.setFont(new Font("consolas", Font.CENTER_BASELINE, 52));
		g.drawString("Level selection", 145, 160);

		g.setColor(Color.GRAY);
		g.fillRect(210, 210, 300, 300);
		
		g.setColor(Color.BLACK);
		g.fillRect(220, 220, 280, 280);
		
		{
			int[] x = { 140, 200, 200 };
			int[] y = { 360, 320, 400 };
			
			g.setColor(Color.WHITE);
			
			if(level == 1)
			{ g.setColor(new Color(0, 0, 0, 100)); }
			
			g.fillPolygon(x, y, 3);
			
			x[0] += 5; x[1] -= 3; x[2] -= 3;
			y[1] += 5; y[2] -= 5;
			
			g.setColor(Color.GRAY);
			
			if(level == 1)
			{ g.setColor(new Color(0, 0, 0, 100)); }
			
			g.fillPolygon(x, y, 3);
		}		
		{
						
			int[] x = { 580, 520, 520 };
			int[] y = { 360, 320, 400 };

			g.setColor(Color.WHITE);
			
			if(Game.curLevel == Game.MAX_LEVEL || Game.curLevel == SaveGame.latestCompletedLevel() + 1)
			{ g.setColor(new Color(0, 0, 0, 100)); }
			
			g.fillPolygon(x, y, 3);
			
			x[0] -= 5; x[1] += 3; x[2] += 3;
			y[1] += 5; y[2] -= 5;
			
			g.setColor(Color.GRAY);
			
			if(Game.curLevel == Game.MAX_LEVEL || Game.curLevel == SaveGame.latestCompletedLevel() + 1)
			{ g.setColor(new Color(0, 0, 0, 100)); }
			
			g.fillPolygon(x, y, 3);
		}
		
		double progressPercentage = 1 - ((double)level / Game.MAX_LEVEL);		
		int blueGreen = (int)(255 * progressPercentage);
		
		g.setColor(new Color(255, blueGreen, blueGreen, 255));
		g.setFont(new Font("consolas", Font.CENTER_BASELINE, 200));
		g.drawString(String.valueOf(level), level != Game.MAX_LEVEL ? 310 : 250, 420);
	}
	
	public boolean drawSplashScreen(Graphics g)
	{
		++frames;
		
		g.setColor(new Color(200, 200, 200));
		g.fillRect(0, 0, Game.WIDTH * Game.SCALE, Game.HEIGHT * Game.SCALE);
		drawLogo(g);
	
		if(frames < secondsToFrames(1.8))
		{ fadeBlack(g, frames, 1.8, false); }
		
		if(frames >= secondsToFrames(3))
		{

			boolean done = fadeBlack(g, frames, 3, true);
			
			if(done)
			{
				frames = 0;
				return true;
			}
		}

		return false;
	}
	
	public boolean fadeBlack(Graphics g, int frames, double duration, boolean toBlack)
	{
		final double alphaPerFrame = 255.0 / secondsToFrames(duration);
		
		int currentFrame = (frames % secondsToFrames(duration)) + 1;
		
		int alpha;
		
		if(toBlack)
		{ alpha = (int)(alphaPerFrame * currentFrame); }		
		else
		{ alpha = (int)(255 - (alphaPerFrame * currentFrame)); }
		
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
			
			if(Game.completed)
			{ path = "/LogoCompleted.png"; }
			
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
	
	public static int secondsToFrames(double seconds)
	{
		// 60 frames == 1 sec
		return (int)(seconds * 60);
	}
}