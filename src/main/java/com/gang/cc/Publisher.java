package com.gang.cc;

import java.util.Scanner;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.qpid.jms.JmsConnectionFactory;

class Publisher {

	public static void main(String[] args) throws Exception {
		Publisher publisher = new Publisher();

		publisher.init(args);
		publisher.inputNumber();
		publisher.sendMessages();
		publisher.close();

		System.exit(0);
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

//		Destination destinationTopic = null;
//		if (destinationName.startsWith(TOPIC_PREFIX)) {
//			// destinationTopic = session.createTopic(destinationName.substring(TOPIC_PREFIX.length()));
//			destinationTopic = session.createQueue(destinationName);
//		} else {
//			destinationTopic = session.createQueue(destinationName);
//		}

		 Destination destinationTopic = null;
		 Destination destinationQueue = null;
		
		 destinationTopic =
		 session.createTopic(destinationName.substring(TOPIC_PREFIX.length()));
		
		 destinationQueue = session.createQueue(destinationName);
		
		 producerTopic = session.createProducer(destinationTopic);
		 producerTopic.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
		
		 producerQueue = session.createProducer(destinationQueue);
		 producerQueue.setDeliveryMode(DeliveryMode.PERSISTENT);
		
		// producerTopic = session.createProducer(destinationTopic);
	}

	private void inputNumber() {
		System.out.println("Enter number of jobs: ");
		Scanner scanner = new Scanner(System.in);
		numberOfMessages = scanner.nextInt();
		scanner.close();
	}

	private void sendMessages() throws Exception {
		// String body = "abcxyz";
		for (int i = 1; i <= numberOfMessages; i++) {
			TextMessage msg = session.createTextMessage(MESSAGE);
			msg.setIntProperty("id", i);
			//producerTopic.send(msg);
			producerQueue.send(msg);
			System.out.println(String.format("Sent %d messages", i));
		}

		producerTopic.send(session.createTextMessage("SHUTDOWN"));
		// Thread.sleep(1000 * 3);
	}

	private void close() throws Exception {
		connection.close();
	}

	private final String MESSAGE = "new jobs available";
	private MessageProducer producerTopic;
	private MessageProducer producerQueue;
	private Session session;
	private Connection connection;
	private int numberOfMessages;

}
