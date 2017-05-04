package com.aws.twitter.kinesis;

import com.amazonaws.regions.Region;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClient;

public class ConsumerClient {

	private final AmazonKinesis kinesisClient;

	public ConsumerClient(String streamName, Region region) {

		kinesisClient = new AmazonKinesisClient();
		kinesisClient.setRegion(region);

	}

	public AmazonKinesis getKinesisClient() {
		return kinesisClient;
	}

}
