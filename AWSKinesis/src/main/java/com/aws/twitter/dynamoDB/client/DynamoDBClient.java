package com.aws.twitter.dynamoDB.client;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.aws.twitter.dynamoDB.model.TwitterUserRecord;

public class DynamoDBClient {

	static AmazonDynamoDBClient client = new AmazonDynamoDBClient(
			new BasicAWSCredentials("AKIAJOEO24AKMZW6VRFA", "xryY41BNd3rwkRUJmWj2Fc2sbpBD34fCM+PpET03"));

	public void saveRecord(TwitterUserRecord item) {

		DynamoDBMapper mapper = new DynamoDBMapper(client);
		mapper.save(item);

	}
}