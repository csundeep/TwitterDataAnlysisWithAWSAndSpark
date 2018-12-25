package com.aws.twitter.dynamoDB.client;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.aws.twitter.dynamoDB.model.TwitterUserRecord;

public class DynamoDBClient {

	static AmazonDynamoDBClient client = new AmazonDynamoDBClient(
			new ProfileCredentialsProvider("sandy"));

	public void saveRecord(TwitterUserRecord item) {

		DynamoDBMapper mapper = new DynamoDBMapper(client);
		mapper.save(item);

	}
}