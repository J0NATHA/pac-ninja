package com.bngames.graficos;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.bngames.entities.Player;
import com.bngames.entities.Red;
import com.bngames.main.Game;

public class UI
{
	public int opacity = Game.bossTimer * 10;
	public BufferedImage orb = (Game.spritesheet.getSprite(67, 3, 8, 8));

	public void render(Graphics g)
	{
		if (Game.curLevel != 6)
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
		if (Game.curLevel == 6)
		{
			g.setColor(Color.black);
			g.drawRoundRect(44, 9, Red.redLife * 30 + 1, 10, 2, 2);
			g.setColor(Color.red);
			if (Player.growIt)
				g.setColor(Color.green);
			g.fillRoundRect(45, 10, Red.curLife * 30, 9, 1, 1);
			g.setColor(Color.black);

			g.setColor(new Color(250, 0, 0, Game.bossTimer * 10));
			g.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
		}
	}

	public void drawBossAtk(Graphics g)
	{
		if (Game.curLevel == 6 && Game.gameState == "NORMAL")
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
		for (int row = 1; row < 4; ++row)
		{
			int level = 1;
			for (int col = 1; col < 4; ++col)
			{
				g.drawString("Level " + level, 30 * col, 50 * row);
				level++;
			}
		}

	}
}