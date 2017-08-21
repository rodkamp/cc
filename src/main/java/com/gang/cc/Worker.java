package com.gang.cc;

import java.util.Random;

public class Worker implements Runnable {

	public Worker(String job) {
		this.job = job;
	}

	@Override
	public void run() {
		Random rn = new Random();
		int time = rn.nextInt(5) + 1;
		try {
			Thread.sleep(time * 1000);
		} catch (Exception e) {
		}
		System.out.println("working on job: " + job);
	}

	private String job;
}
