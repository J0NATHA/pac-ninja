package com.bngames.main;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class Sound
{
	private static Sound sound;
	
	public static Sound get()
	{
		if(sound == null)
		{ sound = new Sound(); }
		
		return sound;
	}
	
	public static class Clips
	{
		public Clip[] clips;
		private int p;
		private int count;

		public Clips(byte[] buffer, int count, boolean maxVolume) 
		{
			if (buffer == null)
			{ return; }

			clips = new Clip[count];
			this.count = count;

			try
			{
				for (int i = 0; i < count; i++)
				{
					clips[i] = AudioSystem.getClip();
										
					clips[i].open(AudioSystem.getAudioInputStream(new ByteArrayInputStream(buffer)));

					var gain = (FloatControl) clips[p].getControl(FloatControl.Type.MASTER_GAIN);
					
					float volume = maxVolume ? gain.getMaximum() : 0;
					gain.setValue(volume);
				}
			} 
			catch (Exception e)
			{
				System.err.println(e.getMessage());
				e.printStackTrace();
			}
		}
		
		public synchronized void play()
		{
			if (clips == null)
			{ return; }

			clips[p].stop();
			
			try 
			{ Thread.sleep(0, 1); }
			
			catch (InterruptedException e) 
			{ e.printStackTrace(); }
			
			clips[p].setFramePosition(0);
			clips[p].start();
			
			p++;
			
			if (p >= count)
			{ p = 0; }
		}

		public void loop()
		{
			if (clips == null)
			{ return; }
			
			clips[p].loop(300);
		}
		
		public void terminate()
		{
			clips[p].stop();
		}
	}

	public Clips hit = load("/hit.wav", 1);
	public Clips hitwall = load("/hitwall.wav", 1);
	public Clips growIt = load("/growIt.wav", 1);
	public Clips entrance = load("/entrance.wav", 1);
	public Clips pickup = load("/pickup.wav", 1);
	public Clips pickupSuperHealth = load("/pickupSuperHealth.wav", 1);
	public Clips keys = load("/keys.wav", 1);
	public Clips start = load("/start.wav", 1);
	public Clips boss1 = load("/boss1.wav", 1);
	public Clips bossound2 = load("/bossound2.wav", 1);
	public Clips bossound3 = load("/bossound3.wav", 1);
	public Clips scream = load("/scream.wav", 1);
	public Clips portal = load("/portal.wav", 1);

	// Music is static to avoid multiple instances which created overlapping audio
	public static Clips bgm = load("/the_projects1.wav", 1);
	public static Clips boss_opening = load("/Cyberpunk Moonlight Sonata v2 opening.wav", 1);
	public static Clips boss_loop = load("/Cyberpunk Moonlight Sonata v2 loop.wav", 1);
	
	private static Clips load(String name, int count)
	{
		try
		{
			final String folder = "/sfx";
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataInputStream dis = new DataInputStream(Sound.class.getResourceAsStream(folder + name));

			byte[] buffer = new byte[1024];
			int read = 0;
			
			while ((read = dis.read(buffer)) >= 0)
			{
				baos.write(buffer, 0, read);
			}
			
			dis.close();
			byte[] data = baos.toByteArray();
			
			boolean maxVolume = name == "/pickup.wav";
			
			return new Clips(data, count, maxVolume);
		} 
		catch (Exception e)
		{
			try
			{ return new Clips(null, 0, false); }
			
			catch (Exception ee)
			{ return null; }
		}
	}
}
