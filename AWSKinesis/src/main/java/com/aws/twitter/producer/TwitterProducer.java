package com.aws.twitter.producer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aws.twitter.kinesis.ProducerBuilder;
import com.aws.twitter.kinesis.ProducerClient;
import com.aws.twitter.util.PropertiesUtil;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

public class TwitterProducer {

	private static final String TWIT_SECRET = "twitter.secret";
	private static final String TWIT_TOKEN = "twitter.token";
	private static final String TWIT_CONSUMER_SECRET = "twitter.consumerSecret";
	private static final String TWIT_CONSUMER_KEY = "twitter.consumerKey";
	private static final String HASHTAGS = "twitter.hashtags";
	private static final String STREAM_NAME = "aws.streamName";
	private static final String DEFAULT_PROP_FILE_NAME = "AwsUserData";
	private static final String REGION_NAME = "aws.regionName";

	private static final Log LOG = LogFactory.getLog(TwitterProducer.class);

	public static void main(String[] args) {
		TwitterProducer producer = new TwitterProducer();
		producer.run();
	}

	public void run() {
		PropertiesUtil.loadFileProperties(DEFAULT_PROP_FILE_NAME);

		String consumerKey = System.getProperty(TWIT_CONSUMER_KEY);
		String consumerSecret = System.getProperty(TWIT_CONSUMER_SECRET);
		String token = System.getProperty(TWIT_TOKEN);
		String secret = System.getProperty(TWIT_SECRET);
		String streamName = System.getProperty(STREAM_NAME);
		String regionName = System.getProperty(REGION_NAME);

		while (true) {
			/**
			 * Set up your blocking queues: Be sure to size these properly based
			 * on expected TPS of your stream
			 */
			BlockingQueue<String> msgQueue = new LinkedBlockingQueue<String>(10000);

			/**
			 * Declare the host you want to connect to, the endpoint, and
			 * authentication (basic auth or oauth)
			 */
			StatusesFilterEndpoint endpoint = new StatusesFilterEndpoint();

			// Track anything that is geo-tagged
			endpoint.addQueryParameter("locations", "-180,-90,180,90");

			// These secrets should be read from a config file
			Authentication hosebirdAuth = new OAuth1(consumerKey, consumerSecret, token, secret);

			// create a new basic client - by default gzip is enabled
			Client client = new ClientBuilder().hosts(Constants.STREAM_HOST).endpoint(endpoint)
					.authentication(hosebirdAuth).processor(new StringDelimitedProcessor(msgQueue)).build();

			client.connect();

			LOG.info("Got connection to Twitter");

			// create producer
			ProducerClient producer = new ProducerBuilder().withName("Twitter").withStreamName(streamName)
					.withRegion(regionName).withThreads(10).build();

			producer.connect();

			LOG.info("Got connection to Kinesis");

			try {
				if (process(msgQueue, producer)) {
					break;
				}

			} catch (Exception e) {
				// if we get here, our client has broken, throw away and
				// recreate
				e.printStackTrace();
			}
			// also, if we make it here, we have had a problem, so start again
			client.stop();
		}
	}

	private boolean process(BlockingQueue<String> msgQueue, ProducerClient producer) {

		int exceptionCount = 0;

		while (true) {
			try {
				// get message HBC from queue
				String msg = msgQueue.take();
				System.out.println(msg);
				// use 'random' partition key
				String key = String.valueOf(System.currentTimeMillis());

				// send to Kinesis
				producer.post(key, msg);

			} catch (Exception e) {
				// didn't get record - move on to next\
				e.printStackTrace();

				if (++exceptionCount > 5) {
					// too many exceptions - lets reconnect and try again
					return false;
				}
			}
		}

	}

}
