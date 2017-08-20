package com.gang.cc;

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

//		Destination destination = null;
//		if (destinationName.startsWith(TOPIC_PREFIX)) {
//			//destination = session.createTopic(destinationName.substring(TOPIC_PREFIX.length()));
//			destination = session.createQueue(destinationName);
//		} else {
//			destination = session.createQueue(destinationName);
//		}

		 Destination destinationTopic = null;
		 Destination destinationQueue = null;
		
		 destinationTopic =
		 session.createTopic(destinationName.substring(TOPIC_PREFIX.length()));
		 destinationQueue = session.createQueue(destinationName);
		
		 consumerTopic = session.createConsumer(destinationTopic);
		 consumerQueue = session.createConsumer(destinationQueue);

		// consumerTopic = session.createConsumer(destination);
	}

	private void receiveMessages() throws Exception {
		System.out.println("Waiting for messages...");
		while (true) {
			//Message msg = consumerTopic.receive();
			Message msg = consumerQueue.receive();
			if (msg instanceof TextMessage) {
				String body = ((TextMessage) msg).getText();
				if ("SHUTDOWN".equals(body)) {
					connection.close();
					try {
						Thread.sleep(10);
					} catch (Exception e) {
					}
					System.exit(1);
				} else {
					System.out.println(String.format("Received message %d %s.", msg.getIntProperty("id"), body));
				}

			} else {
				System.out.println("Unexpected message type: " + msg.getClass());
			}
		}
	}

	private Connection connection;
	private Session session;
	private MessageConsumer consumerTopic;
	private MessageConsumer consumerQueue;
}
