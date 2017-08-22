package com.gang.cc;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.qpid.jms.JmsConnectionFactory;

public class Manager {

	public static void main(String[] args) throws Exception {
		Manager manager = new Manager();
		manager.init(args);
		manager.receiveMessages();
		manager.close();
	}

	private static String env(String key, String defaultValue) {
		String rc = System.getenv(key);
		if (rc == null)
			return defaultValue;
		return rc;
	}

	private static String arg(String[] args, int index, String defaultValue) {
		if (index < args.length)
			return args[index];
		else
			return defaultValue;
	}

	private void init(String[] args) throws Exception {
		final String TOPIC_PREFIX = "topic://";

		String user = env("ACTIVEMQ_USER", "admin");
		String password = env("ACTIVEMQ_PASSWORD", "password");
		String host = env("ACTIVEMQ_HOST", "localhost");
		int port = Integer.parseInt(env("ACTIVEMQ_PORT", "5672"));

		String connectionURI = "amqp://" + host + ":" + port;
		String destinationName = arg(args, 0, "topic://event");

		JmsConnectionFactory factory = new JmsConnectionFactory(connectionURI);

		connection = factory.createConnection(user, password);
		connection.start();
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		Destination destinationTopic = null;
		Destination destinationQueue = null;

		destinationTopic = session.createTopic(destinationName.substring(TOPIC_PREFIX.length()));
		destinationQueue = session.createQueue(destinationName);

		consumerTopic = session.createConsumer(destinationTopic);
		consumerQueue = session.createConsumer(destinationQueue);

		executorService = Executors.newFixedThreadPool(NUMBEROFWORKERS);
	}

	private void receiveMessages() throws Exception {
		System.out.println("Waiting for messages...");
		while (true) {
			Message msg = consumerTopic.receive();
			System.out.println("msg received for new job available.");
			Thread dispatcher = new Thread(new JobDispatcher());
			dispatcher.start();
		}

	}

	private void close() {
		executorService.shutdown();
	}

	private Connection connection;
	private Session session;
	private MessageConsumer consumerTopic;
	private MessageConsumer consumerQueue;
	private ExecutorService executorService;

	private final int NUMBEROFWORKERS = 10;
	private AtomicInteger numberOfIdealWorkers = new AtomicInteger(NUMBEROFWORKERS);

	// JobDispatcher as another thread to handle jobs in the queue and not block
	// the new jobs available message receiving from topic
	class JobDispatcher implements Runnable {

		@Override
		public void run() {
			System.out.println("in JobDispatcher");
			Message msg = null;
			try {
				while (true) {
					if (numberOfIdealWorkers.get() > 0) {
						msg = consumerQueue.receive();
						if (msg instanceof TextMessage) {
							String job = ((TextMessage) msg).getText();
							System.out.println("in JobDispatcher, msg = " + job);
							executorService.execute(new Worker(job));
						}
					} else {
						System.out.println("no ideal workers for now.");
						try {
							Thread.sleep(1000);
						} catch (Exception e) {
						}
					}
				}

			} catch (JMSException e) {
				e.printStackTrace();
			}

		}

	}

	// Worker as an inner class to access numberOfIdealWorkers from class
	// Manager
	// this is an easy way to measure whether there is ideal workers for a
	// manager
	class Worker implements Runnable {

		public Worker(String job) {
			this.job = job;
		}

		@Override
		public void run() {
			// this worker not ideal
			numberOfIdealWorkers.decrementAndGet();

			System.out.println("working on job: " + job);
			Random rn = new Random();
			int time = rn.nextInt(5) + 1;
			try {
				Thread.sleep(time * 1000);
			} catch (Exception e) {
			}
			System.out.println("complete job: " + job);

			// this worker becomes ideal
			numberOfIdealWorkers.addAndGet(1);
		}

		private String job;
	}
}
