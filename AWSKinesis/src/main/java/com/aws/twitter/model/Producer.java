package com.aws.twitter.model;

import java.nio.ByteBuffer;

/**
 * Interface for sending information to Amazon Kinesis.
 *
 */
public interface Producer {
	/**
	 * Posts an event to Kinesis
	 * @param partitionKey The partition key to use
	 * @param data The event data, represented as a string
	 */
	void post(String partitionKey, String data);
	
	/**
	 * Posts an event to Kinesis
	 * @param partitionKey The partition key to use
	 * @param data The event data, represented as a byte buffer
	 */
	void post(String partitionKey, ByteBuffer data);
	
	/**
	 * Posts an event to Kinesis
	 * @param event The event data
	 */
	void post(Event event);
	
	
	/**
	 * Connects to Kinesis and starts listening for events to send
	 */
	void connect();
	
	/**
	 * Stops listening for events
	 */
	void stop();
	
}