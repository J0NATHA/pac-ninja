package com.bngames.main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public abstract class SaveGame
{
	private static final String SAVE_FILE = "passedLevels.txt";
	
	public static boolean save(String data)
	{
		try
		{
			FileWriter writer = new FileWriter(SAVE_FILE, true);
			
			writer.write(data + '\n');
			writer.close();
			
			return true;
		}
		
		catch(IOException e)
		{ e.printStackTrace(); }
		
		return false;
	}
	
	public static int latestCompletedLevel()
	{
		try
		{
			FileReader fileReader = new FileReader(SAVE_FILE);
			BufferedReader reader = new BufferedReader(fileReader);
			String line, retVal = "";
			
			while((line = reader.readLine()) != null)
			{
				retVal = line.replace("\n", "");
			}
			
			reader.close();
			
			return Integer.parseInt(retVal);
		} 
		
		catch (FileNotFoundException e)
		{ return 0; } 
		
		catch (IOException e)
		{ e.printStackTrace(); }
		
		return 0;
	}
}
