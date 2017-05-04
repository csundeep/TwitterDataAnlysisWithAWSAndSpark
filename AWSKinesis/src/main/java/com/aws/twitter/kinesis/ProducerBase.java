package com.aws.twitter.kinesis;

import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.model.PutRecordRequest;
import com.amazonaws.services.kinesis.model.PutRecordResult;
import com.aws.twitter.model.Event;

/**
 * Runnable class responsible for sending items on the queue to Kinesis
 * 
 * @author corbetn
 *
 */
public class ProducerBase implements Runnable {

	/**
	 * Reference to the queue
	 */
	private final BlockingQueue<Event> eventsQueue;

	/**
	 * Reference to the Amazon Kinesis Client
	 */
	private final AmazonKinesis kinesisClient;

	/**
	 * The stream name that we are sending to
	 */
	private final String streamName;

	private final static Logger logger = LoggerFactory.getLogger(ProducerBase.class);

	/**
	 * @param eventsQueue
	 *            The queue that holds the records to send to Kinesis
	 * @param kinesisClient
	 *            Reference to the Kinesis client
	 * @param streamName
	 *            The stream name to send items to
	 */
	public ProducerBase(BlockingQueue<Event> eventsQueue, AmazonKinesis kinesisClient, String streamName) {
		this.eventsQueue = eventsQueue;
		this.kinesisClient = kinesisClient;
		this.streamName = streamName;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {

		while (true) {
			try {

				// get message from queue - blocking so code will wait here for
				// work to do
				Event event = eventsQueue.take();
				PutRecordRequest put = new PutRecordRequest();
				put.setStreamName(this.streamName);

				put.setData(event.getData());
				put.setPartitionKey(event.getPartitionKey());

				PutRecordResult result = kinesisClient.putRecord(put);
				System.out.println(result.getSequenceNumber());
				logger.info(result.getSequenceNumber() + ": {}", this);

			} catch (Exception e) {
				// didn't get record - move on to next\
				e.printStackTrace();
			}
		}

	}
}