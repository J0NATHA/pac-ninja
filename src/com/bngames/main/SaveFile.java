package com.bngames.main;

import java.util.ArrayList;

public class SaveFile 
{
	private ArrayList<Integer> completedLevels;
	private int lastPlayedLevel;
	private int deathCount;
	private int bossDefeatedCount;
	
	public SaveFile()
	{
		setCompletedLevels(new ArrayList<Integer>());
		setLastPlayedLevel(1);
		setDeathCount(0);
		setBossDefeatedCount(0);
	}
	
	public SaveFile(ArrayList<Integer> completedLevels, int lastPlayedLevel, int deathCount, int bossDefeatedCount)
	{
		setCompletedLevels(completedLevels);
		setLastPlayedLevel(lastPlayedLevel);
		setDeathCount(deathCount);
		setBossDefeatedCount(bossDefeatedCount);
	}
	
	public ArrayList<Integer> getCompletedLevels() 
	{
		return completedLevels;
	}

	public void setCompletedLevels(ArrayList<Integer> completedLevels) 
	{
		this.completedLevels = completedLevels;
	}

	public int getLastPlayedLevel() 
	{
		return lastPlayedLevel;
	}

	public void setLastPlayedLevel(int lastPlayedLevel) 
	{
		this.lastPlayedLevel = lastPlayedLevel;
	}

	public int getDeathCount() 
	{
		return deathCount;
	}

	public void setDeathCount(int deathCount) 
	{
		this.deathCount = deathCount;
	}

	public int getBossDefeatedCount() 
	{
		return bossDefeatedCount;
	}

	public void setBossDefeatedCount(int bossDefeatedCount) 
	{
		this.bossDefeatedCount = bossDefeatedCount;
	}
}
