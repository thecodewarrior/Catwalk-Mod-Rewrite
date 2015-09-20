package com.thecodewarrior.catwalks.util;

public abstract class Predicate<T> {
	
	protected Object[] args;
	
	public Predicate(Object... args) {
		this.args = args;
	}
	
	public abstract boolean test(T object);
}
