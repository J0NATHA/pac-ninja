package com.bngames.world;

import com.bngames.main.Game;

public class Camera
{
	public static int x = 0;
	public static int y = 0;

	public static int clamp(int Atual, int Min, int Max)
	{
		return Atual < Min ? Min : Atual > Max ? Max : Atual; 
	}
	
	public static void print()
	{
		System.out.println("Camera at (" + x + ", " + y + ")");
	}
	
	public static void place(int level)
	{
		switch(level)
		{
			case Game.MAX_LEVEL:
			{
				x = 43;
				y = 0;
				return;
			}
			default:
			{
				Game.player.updateCamera();
				return;
			}
		}	
	}
}
