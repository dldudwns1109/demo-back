package com.kh.demo.util;

import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public class RandomGenerator {
	
	private Random r = new Random();
	
	public String randomNumber(int size) {
		StringBuffer buffer = new StringBuffer();
		for(int i=0; i < size; i++) {
			buffer.append(r.nextInt(10));
		}
		return buffer.toString();
	}
}