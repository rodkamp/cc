package com.gang.cc;

import org.apache.qpid.jms.*;
import javax.jms.*;

class Publisher {

	public static void main(String[] args) throws Exception {

		// final String TOPIC_PREFIX = "topic://";
		//
		// String user = env("ACTIVEMQ_USER", "admin");
		// String password = env("ACTIVEMQ_PASSWORD", "password");
		// String host = env("ACTIVEMQ_HOST", "localhost");
		// int port = Integer.parseInt(env("ACTIVEMQ_PORT", "5672"));
		//
		// String connectionURI = "amqp://" + host + ":" + port;
		// String destinationName = arg(args, 0, "topic://event");
		//
		// int messages = 8;
		// String body = "abcxyz";
		//
		// JmsConnectionFactory factory = new
		// JmsConnectionFactory(connectionURI);
		//
		// Connection connection = factory.createConnection(user, password);
		// connection.start();
		//
		// Session session = connection.createSession(false,
		// Session.AUTO_ACKNOWLEDGE);
		//
		// Destination destination = null;
		// if (destinationName.startsWith(TOPIC_PREFIX)) {
		// destination =
		// session.createTopic(destinationName.substring(TOPIC_PREFIX.length()));
		// } else {
		// destination = session.createQueue(destinationName);
		// }
		//
		// MessageProducer producer = session.createProducer(destination);
		// producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

		init(args);

		int messages = 8;
		String body = "abcxyz";
		for (int i = 1; i <= messages; i++) {
			TextMessage msg = session.createTextMessage(MESSAGE);
			msg.setIntProperty("id", i);
			producer.send(msg);
			System.out.println(String.format("Sent %d messages", i));
		}

		producer.send(session.createTextMessage("SHUTDOWN"));
		//Thread.sleep(1000 * 3);
		connection.close();
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

	private static void init(String[] args) throws Exception {
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

		Destination destination = null;
		if (destinationName.startsWith(TOPIC_PREFIX)) {
			destination = session.createTopic(destinationName.substring(TOPIC_PREFIX.length()));
		} else {
			destination = session.createQueue(destinationName);
		}

		producer = session.createProducer(destination);
		producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
	}

	private static final String MESSAGE = "new jobs available";
	private static MessageProducer producer;
	private static Session session;
	private static Connection connection;

}
