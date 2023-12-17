package com.bngames.world;

public class Camera
{

	public static int x = 0;
	public static int y = 0;

	public static int clamp(int Atual, int Min, int Max)
	{
		return Atual < Min ? Min : Atual > Max ? Max : Atual; 
	}
}
