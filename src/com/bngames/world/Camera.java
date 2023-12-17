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
	
	public static void place(int level)
	{
		switch(level)
		{
			case Game.MAX_LEVEL:
			{
				x = 43;
				y = 0;
				break;
			}
			default:
			{
				x = 0;
				y = 0;
			}
		}
	}
}
