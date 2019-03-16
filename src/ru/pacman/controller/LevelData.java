package ru.pacman.controller;

public class LevelData {
	public byte[] level;
	public int width;
	public int height;
	
	public LevelData(byte[] level, int width, int height) {
		this.level = level;
		this.width = width;
		this.height = height;
	}
} 