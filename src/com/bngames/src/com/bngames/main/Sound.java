package com.bngames.main;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Sound {

	public static class Clips{
		public Clip[] clips;
		private int p;
		private int count;
		
		public Clips(byte[] buffer, int count) throws LineUnavailableException, IOException, UnsupportedAudioFileException{
			if(buffer == null)
				return;
			
			clips = new Clip[count];
			this.count= count;
			
			for(int i =0; i<count; i++) {
				clips[i] = AudioSystem.getClip();
				clips[i].open(AudioSystem.getAudioInputStream(new ByteArrayInputStream(buffer)));
			}
		}
		
		public void play() {
			if (clips==null) return;
			clips[p].stop();
			clips[p].setFramePosition(0);
			clips[p].start();
			p++;
			if(p>=count) p=0;
		}
		
		public void loop() {
			if(clips==null) return;
			clips[p].loop(300);
		}
		
		public void terminate() {
			clips[p].stop();
		}
	}

	

	public static Clips hit = load("/hit.wav", 1);
	public static Clips hitwall = load("/hitwall.wav", 1);
	public static Clips growIt = load("/growIt.wav", 1);
	public static Clips entrance = load("/entrance.wav", 1);
	public static Clips pickup = load("/pickup.wav", 1);
	public static Clips keys = load("/keys.wav", 1);
	public static Clips start = load("/start.wav", 1);
	public static Clips sad1 = load("/sad1.mp3", 1);
	public static Clips bgm = load("/music1.wav", 1);
	public static Clips boss_opening = load("/boss_opening.wav", 1);
	public static Clips boss_loop = load("/boss_loop.wav", 1);
	public static Clips boss1 = load("/boss1.wav", 1);
	public static Clips bossound2 = load("/bossound2.wav", 1);
	public static Clips bossound3 = load("/bossound3.wav", 1);
	public static Clips scream = load("/scream.wav", 1);
	public static Clips portal = load("/portal.wav", 1);
	
private static Clips load(String name, int count) {
	try {
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataInputStream dis = new DataInputStream(Sound.class.getResourceAsStream(name));
		
		byte[] buffer = new byte [1024];
		int read = 0;
		while((read=dis.read(buffer)) >= 0) {
			baos.write(buffer,0,read);
		}
		dis.close();
		byte[] data = baos.toByteArray();
		return new Clips(data,count);
	}catch(Exception e) {
		try {
			return new Clips(null,0);
		} catch(Exception ee ) {
			return null;
		}
	}
}
}

