package com.aws.twitter.consumer;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.kinesis.model.DescribeStreamRequest;
import com.amazonaws.services.kinesis.model.DescribeStreamResult;
import com.amazonaws.services.kinesis.model.GetRecordsRequest;
import com.amazonaws.services.kinesis.model.GetRecordsResult;
import com.amazonaws.services.kinesis.model.GetShardIteratorRequest;
import com.amazonaws.services.kinesis.model.GetShardIteratorResult;
import com.amazonaws.services.kinesis.model.Record;
import com.amazonaws.services.kinesis.model.Shard;
import com.aws.twitter.dynamoDB.client.DynamoDBClient;
import com.aws.twitter.dynamoDB.model.TwitterUserRecord;
import com.aws.twitter.kinesis.ConsumerClient;
import com.aws.twitter.util.PropertiesUtil;

import twitter4j.JSONArray;
import twitter4j.JSONException;
import twitter4j.JSONObject;

public class Twitterconsumer {

	private static final String STREAM_NAME = "aws.streamName";
	private static final String DEFAULT_PROP_FILE_NAME = "AwsUserData";
	private static final String REGION_NAME = "aws.regionName";

	public static void main(String[] args) {

		List<String> twitterRecords = new ArrayList<>();

		PropertiesUtil.loadFileProperties(DEFAULT_PROP_FILE_NAME);

		String streamName = System.getProperty(STREAM_NAME);
		String regionName = System.getProperty(REGION_NAME);

		Region region = Region.getRegion(Regions.fromName(regionName));

		ConsumerClient consumer = new ConsumerClient(streamName, region);

		DescribeStreamRequest describeStreamRequest = new DescribeStreamRequest();
		describeStreamRequest.setStreamName(streamName);
		List<Shard> shards = new ArrayList<>();
		String exclusiveStartShardId = null;
		do {
			describeStreamRequest.setExclusiveStartShardId(exclusiveStartShardId);
			DescribeStreamResult describeStreamResult = consumer.getKinesisClient()
					.describeStream(describeStreamRequest);
			shards.addAll(describeStreamResult.getStreamDescription().getShards());
			if (describeStreamResult.getStreamDescription().getHasMoreShards() && shards.size() > 0) {
				exclusiveStartShardId = shards.get(shards.size() - 1).getShardId();
			} else {
				exclusiveStartShardId = null;
			}
		} while (exclusiveStartShardId != null);

		String shardIterator;
		GetShardIteratorRequest getShardIteratorRequest = new GetShardIteratorRequest();
		getShardIteratorRequest.setStreamName(streamName);
		getShardIteratorRequest.setShardId(shards.get(0).getShardId());
		getShardIteratorRequest.setShardIteratorType("TRIM_HORIZON");

		GetShardIteratorResult getShardIteratorResult = consumer.getKinesisClient()
				.getShardIterator(getShardIteratorRequest);
		shardIterator = getShardIteratorResult.getShardIterator();

		GetRecordsRequest request = new GetRecordsRequest();
		request.setLimit(1000);
		request.setShardIterator(shardIterator);

		GetRecordsResult result = consumer.getKinesisClient().getRecords(request);
		List<Record> records = result.getRecords();
		for (Record record : records) {
			parseData(new String(record.getData().array()));
			twitterRecords.add(new String(record.getData().array()));
		}
	}

	private static void parseData(String jsonString) {
		try {
			JSONObject obj = new JSONObject(jsonString);

			JSONObject user = obj.getJSONObject("user");

			JSONObject place = obj.getJSONObject("place");

			long id = user.getLong("id");
			String name = user.getString("name");
			long followersCount = user.getLong("followers_count");
			long friendsCount = user.getLong("friends_count");
			long favouritesCount = user.getLong("favourites_count");
			long statusesCount = user.getLong("statuses_count");
			String country = place.getString("country");

			TwitterUserRecord record = new TwitterUserRecord(id, name, followersCount, friendsCount, favouritesCount,
					statusesCount, country);

			System.out.println(record);
			DynamoDBClient client = new DynamoDBClient();
			client.saveRecord(record);

		} catch (JSONException e) {

		}
	}

}
