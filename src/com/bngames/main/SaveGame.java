package com.bngames.main;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.Gson;

public abstract class SaveGame
{
	private static final String FILE_NAME = "save_data.json";
	private static final File SAVE_DIRECTORY = 
			new File(
				System.getProperty("user.home") 
				+ File.separator + "AppData" + File.separator + "Local" + File.separator  
				+ "pacninja" + File.separator + Game.GAME_VERSION + File.separator
			);
		
	public static SaveFile loadFile()
	{
		try 
		{
			Gson gson = new Gson();
			Reader reader = new FileReader(SAVE_DIRECTORY.getAbsolutePath() + File.separator + FILE_NAME);
			return gson.fromJson(reader, SaveFile.class);
		}
		
		catch (Exception e) 
		{ return new SaveFile(); }	
	}
	
	private static void gsonWrite(SaveFile file) throws IOException
	{
		FileWriter writer = 
				new FileWriter(SAVE_DIRECTORY.getAbsolutePath() + File.separator + FILE_NAME);
			
		Gson gson = new Gson();
		gson.toJson(file, writer);
		
		writer.close();
	}
	
	public static boolean saveLevel(String data)
	{
		try
		{
			if(!SAVE_DIRECTORY.exists())
			{ SAVE_DIRECTORY.mkdirs();}
			
			Integer level = Integer.parseInt(data);
			
			if(latestCompletedLevel() >= level)
			{ return false; }
			
			SaveFile file = loadFile();
			
			file.getCompletedLevels().add(level);
			
			gsonWrite(file);
			
			return true;
		}
		catch(IOException e)
		{ e.printStackTrace(); }
		
		return false;
	}
	
	public static void saveDeath()
	{
		try
		{
			SaveFile file = loadFile();
			
			file.setDeathCount(file.getDeathCount() + 1);
			
			gsonWrite(file);
		}
		
		catch(IOException e)
		{ e.printStackTrace(); }
	}
	
	public static void saveBossDefeated()
	{
		try
		{
			SaveFile file = loadFile();
			
			file.setBossDefeatedCount(file.getBossDefeatedCount() + 1);
			
			gsonWrite(file);
		}
		
		catch(IOException e)
		{ e.printStackTrace(); }
	}
	
	public static void saveLastPlayedLevel(int level)
	{
		try
		{
			SaveFile file = loadFile();
			
			file.setLastPlayedLevel(level);
			
			gsonWrite(file);
		}
		
		catch(IOException e)
		{ e.printStackTrace(); }
	}
	
	public static int latestCompletedLevel()
	{
		SaveFile file = loadFile();
		
		if(file == null || file.getCompletedLevels().isEmpty())
		{ return 0; }
		
		ArrayList<Integer> completedLevels = file.getCompletedLevels();
		
		return completedLevels.get(completedLevels.size() - 1);
	}
}
