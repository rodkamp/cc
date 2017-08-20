package com.gang.cc;

public class Worker implements Runnable {
	
	public Worker(String job) {
		this.job = job;
	}
	@Override
	public void run() {
		System.out.println("working on job: " + job);
	}

	private String job;
}
