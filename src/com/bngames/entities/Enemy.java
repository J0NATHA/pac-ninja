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

public class Enemy extends Entity{
	private int greenWait=0;
	public  boolean ghostMode=false, startGhost=false;
	private int frames=0, orbFrames=0, maxFrames=11, index=0,maxIndex=4, ghostFrames=0, nextTime = Entity.rand.nextInt(60*5 - 60*3) + 60*3;
	private BufferedImage[] sprites;
	private double followRate = 1.0;
	
	public Enemy(int x, int y, int width, int height,double speed, BufferedImage sprite) {
		super(x, y, width, height, 1, null);
	
		sprites=new BufferedImage[4];
		sprites[0]=Game.spritesheet.getSprite(128, 4, 16, 17);
		sprites[1]=Game.spritesheet.getSprite(144, 7, 16, 17);
		sprites[2]=Game.spritesheet.getSprite(128, 23, 16, 17);
		sprites[3]=Game.spritesheet.getSprite(144, 31, 16, 17);
	}
	
	private void chaseStart() {
		if(path==null || path.size()==0 ) {
			Vector2i start = new Vector2i((int)(x/16), (int)(y/16));
			Vector2i end = new Vector2i((int) (Game.player.x/16), (int)(Game.player.y/16));
			path = AStar.findPath(Game.world, start, end);
		}

		if(new Random().nextInt(100)< (int)(65 * followRate))
		{
			followPath(path);
		}
		
		if(new Random().nextInt(100)<5) {
			Vector2i start = new Vector2i((int) (x/16), (int)(y/16));
			Vector2i end = new Vector2i((int) (Game.player.x/16), (int)(Game.player.y/16));
			path = AStar.findPath(Game.world, start, end);
		}
	}

	public void tick() {
		depth=0;
		if (Game.CUR_LEVEL == 6 && Game.gameState == "NORMAL")
		{
			bossBattleMode();
		}
		if(Game.restartGame) {
		Game.enemies.clear();
		Game.entities.clear();
		}
		
		int xPlayer = Game.player.getX();
		int yPlayer = Game.player.getY();
		
		int xEnemy = (int)x;
		int yEnemy = (int)y;
		
		if(ghostMode==false)
			chaseStart();
		
		if(Player.growIt) {
			startGhost=true;
			if(Game.orbsPicked>0) {
				orbFrames++;
			if(orbFrames==4) {
				Game.orbsPicked--;
		}if(orbFrames==5)
			orbFrames=0;
			}
		}
		
		if(startGhost) {
			ghostFrames++;
			if( ghostFrames<=60*4) {
				ghostMode=true;
			}
			else {
				ghostFrames=0;	
				ghostMode=false;
				startGhost=false;
			}
		}
		
			
		if(isCollidingWithPlayer() && ghostMode==false) {
			if(Game.player.isDamaged==false) {
				Game.player.life--;
				Sound.hit.play();
				Game.player.isDamaged=true;
			}
		}
	}
	
	private void bossBattleMode()
	{
		if (Game.bossTimer != 0)
			{
				followRate = 1.0 - (Game.bossTimer * 0.025);
			}
	}
			
	private void animate(){
		if(Game.gameState=="NORMAL")
			frames++;
		if(frames==maxFrames) {
			frames=0;
			index++;
			if(index>=maxIndex)
				index=0;
		}
	}
	
	
	
	
	
	public boolean isCollidingWithPlayer() {
		Rectangle enemyCurrent = new Rectangle(this.getX()+2, this.getY(), 12,17);	
		Rectangle player= new Rectangle (Game.player.getX()-Game.player.Pmaskx, Game.player.getY()-Game.player.Pmasky, Game.player.Pmaskw, Game.player.Pmaskh);
		
		return enemyCurrent.intersects(player);
	}
	
		
	
	
	public void render(Graphics g) {
//			g.fillRect(this.getX()+2, this.getY(), 12,17);
		
		if(ghostMode==false) {
			greenWait=0; 
			g.drawImage(sprites[index], this.getX()-Camera.x, this.getY()-Camera.y, null);
			animate();
		}
		else {
			greenWait++;
			if(greenWait<15) {
				g.drawImage(sprites[index], this.getX()-Camera.x, this.getY()-Camera.y, null);
			}
			else if(greenWait>=15)
				g.drawImage(Game.spritesheet.getSprite(108, 4, 16, 17), this.getX()-Camera.x, this.getY()-Camera.y, null);
		}
		
	}
		
		
	}
		


