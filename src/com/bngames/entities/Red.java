package com.bngames.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.bngames.main.Game;
import com.bngames.main.Sound;
import com.bngames.world.Camera;

public class Red extends Entity {
	
	private int aFrames=0, aIndex=0;
	
	private boolean go=true, back=false;
	public  boolean damage=false;
	
	public static int redLife=5, curLife=5;
	
	private BufferedImage[] sprites;

	public Red(double x, double y, int width, int height, double speed, BufferedImage sprite) 
	{
		super(x, y, width, height, speed, sprite);
		
		sprites = new BufferedImage[4];
		sprites[0] = Game.spritesheet.getSprite(2, 135, 14, 16);
		sprites[1] = Game.spritesheet.getSprite(18, 135, 14, 16);
		sprites[2] = Game.spritesheet.getSprite(67, 135, 14, 16);
		sprites[3] = Game.spritesheet.getSprite(85, 135, 14, 16);	
	}

	public void tick() 
	{		
		if(isCollidingWithPlayer() && Game.player.isDamaged==false) 
		{
			Game.player.isDamaged=true;
			Game.player.life--;
			Sound.hit.play();
		}

		if(Player.growIt) 
		{
			if(Game.orbsPicked>0) 
			{
				Game.orbsPicked--;
			}
		}
	}
	
	public boolean isCollidingWithPlayer() 
	{
		Rectangle enemyCurrent = new Rectangle(this.getX()-curLife*3-1 , this.getY()-curLife*3-1 , (10*curLife),(10*curLife));	
		Rectangle player= new Rectangle (Game.player.getX()-Game.player.Pmaskx, Game.player.getY()-Game.player.Pmasky, Game.player.Pmaskw, Game.player.Pmaskh);
		
		return enemyCurrent.intersects(player);
	}
	
	private void Animate() {
		
		aFrames++;
		if(aFrames==10) {
			aFrames=0;
			if(aIndex<3 && go==true){
				aIndex++;
				if(aIndex==3) {
					go=false;
					back=true;
				}
			}else if(aIndex>0 && back==true) {
				aIndex--;
			if(aIndex==0) {
				go=true;
				back=false;
			}
			}
		}
		
	}
	
	public void render(Graphics g) {
		if(curLife>0) {
			if(Game.gameState=="NORMAL" || Game.gameState=="PAUSE") {
		g.setColor(new Color(250,0,0,100));
		g.fillRoundRect(this.getX()-Camera.x-curLife*3-1  , this.getY()-Camera.y-curLife*3-1, (10*curLife),(10*curLife),10 ,10);	
			}
		}
		if(Game.curLevel==6 && Game.hideSprite==false) {
		g.drawImage(sprites[aIndex], this.getX()-Camera.x, this.getY()-Camera.y, null);
		if(Game.gameState!="SCENE3")
		Animate();
		}
	}

}
