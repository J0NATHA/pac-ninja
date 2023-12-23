package com.bngames.graficos;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import com.bngames.entities.Entity;
import com.bngames.entities.Player;
import com.bngames.entities.Red;
import com.bngames.main.Game;
import com.bngames.main.SaveGame;
import com.bngames.world.Camera;

public class UI
{
	private int framesSplashScreen = 0;

	public void render(Graphics g)
	{
		int y = 44;
		
		if (Game.curLevel != Game.MAX_LEVEL)
		{
			double distance = Math.sqrt(
					       	Math.pow(16 - Game.player.getX(), 2) +
					       	Math.pow(16 - Game.player.getY(), 2) );
			
			
			int alpha = distance < 30 ? 100 : 255;
			
			double completionPercentage = (double) Game.orbAtual / Game.orbContagem;
			int arcAngle = - (int)(completionPercentage * 360);
			
			g.setColor(new Color(0, 0, 0, 150));
			g.fillArc(10, 10, 100, 100, 90, 360);
			
			g.setColor(new Color(0, 255, 0, alpha));
			g.fillArc(10, 10, 100, 100, 90, arcAngle);
			
			g.setColor(Color.orange);
			g.drawArc(10, 10, 100, 100, 0, 360);
		}
		
		else
		{ y = 30; }
				
		g.drawImage(Entity.ORB_HUD, 44, y, 32, 32, null);

		double imagePercentage = (double)Game.orbsPicked / 20;
		int height = (int)(imagePercentage * 8);
		
		if(height > 0)
		{
			final BufferedImage ORB_SPRITE = Game.spritesheet.getSprite(67, 3, 8, height);
			g.drawImage(ORB_SPRITE, 44, y, 32, 4 * height, null);
		}
	}
	
	public boolean animateLevelIntro(Graphics g, int frame)
	{		
		int y = frame * 4;
		y = clamp(y, 0, Game.player.getY());
		
		g.setColor(new Color(0, 250, 0, 150));
		g.fillOval(Game.player.getX() - Camera.x, y - Camera.y - 4, 16, 23);
		
		if(Game.player.hasSuperHealth())
		{ 
			g.setColor(new Color(255, 255, 0, 150));
			g.fillOval(Game.player.getX() - Camera.x - 2, y - Camera.y - 6, 20, 28);
		}
		
		return y == Game.player.getYTile();
	}
	
	public boolean waitLevelIntro(Graphics g, int frame)
	{
		g.setColor(new Color(0, 250, 0, 150));
		g.fillOval(Game.player.getX() - Camera.x, Game.player.getY() - Camera.y - 4, 16, 23);
		
		if(Game.player.hasSuperHealth())
		{ 
			g.setColor(new Color(255, 255, 0, 150));
			g.fillOval(Game.player.getX() - Camera.x - 2, Game.player.getY() - Camera.y - 6, 20, 28);
		}
		
		return frame == secondsToFrames(.7);
	}
	
	private int clamp(int current, int min, int max)
	{
		return current > max ? max : current < min ? min : current;
	}
	
	public int animateSpaceBar(Graphics g, int space, BufferedImage[] spacebar)
	{
		int SCALE = Game.SCALE;
		int index = 0;

		space++;
		
		if(space > 25)
		{ index = 1; }
		
		if(space >= 40)
		{ space = 0; }
		
		final int x = (Game.player.getX() - Camera.x - 9) * SCALE;
		final int y = (Game.player.getY() - Camera.y + 18) * SCALE;
		final int w = 33 * SCALE;
		final int h = 7 * SCALE;
		
		g.drawImage(spacebar[index], x, y, w, h, null);
		
		return space;
	}
	
	public int animateSpaceBar(Graphics g, int space, BufferedImage[] spacebar, int x, int y, double scale)
	{
		final double SCALE = Game.SCALE + scale;
		int index = 0;
		
		space++;
		
		if(space >= 15)
		{ index = 1; }
		
		if(space >= 60)
		{ space = 0; }
		
		final int w = (int)(33 * SCALE);
		final int h = (int)(7 * SCALE);
		
		g.drawImage(spacebar[index], x, y, w, h, null);
		
		return space;
	}

	public void renderBoss(Graphics g)
	{
		if (Game.curLevel == Game.MAX_LEVEL)
		{
			g.setColor(Color.black);
			g.drawRoundRect(44, 9, Red.maxLife * 30 + 1, 10, 2, 2);
			g.setColor(Color.red);
			
			if (Player.crushOrb)
			{ g.setColor(Color.green); }
			
			g.fillRoundRect(45, 10, Red.curLife * 30, 9, 1, 1);
			g.setColor(Color.black);
		}
	}

	public void drawLevelSelectMenu(Graphics g, int level)
	{
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
		++framesSplashScreen;
		
		g.setColor(new Color(200, 200, 200));
		g.fillRect(0, 0, Game.WIDTH * Game.SCALE, Game.HEIGHT * Game.SCALE);
		drawLogo(g);
	
		if(framesSplashScreen < secondsToFrames(1.8))
		{ fadeBlack(g, framesSplashScreen, 1.8, false); }
		
		if(framesSplashScreen >= secondsToFrames(3))
		{

			boolean done = fadeBlack(g, framesSplashScreen, 3, true);
			
			if(done)
			{
				framesSplashScreen = 0;
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
	
	public void fadeToRed(Graphics g, int frame)
	{
		final double alphaPerFrame = 255.0 / 60;
		
		g.setColor(new Color(255, 0, 0, (int)(alphaPerFrame * frame)));
		g.fillRect(0, 0, Game.WIDTH * Game.SCALE, Game.HEIGHT * Game.SCALE);
	}
	
	private void drawLogo(Graphics g)
	{
		try
		{
			String path = "/LogoBN.png";
			
			if(Game.curLevel == Game.MAX_LEVEL)
			{ path = "/LogoRN.png"; }
			
			if(Game.finished)
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