package ru.pacman.model;

public class Pair<T, V> {
	private T key;
	private V value;

	public Pair(T t, V v) {
		key = t;
		value = v;
	}

	public T getKey() {
		return key;
	}

	public V getValue() {
		return value;
	}
}