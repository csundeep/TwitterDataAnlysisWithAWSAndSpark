package com.aws.twitter.dynamoDB.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "TwitterUserRecords")
public class TwitterUserRecord {
	private long id;
	private String name;
	private double followers_count;
	private double friends_count;
	private double favourites_count;
	private double statuses_count;
	private String country;

	public TwitterUserRecord(long id, String name, double followers_count, double friends_count,
			double favourites_count, double statuses_count, String country) {
		super();
		this.id = id;
		this.name = name;
		this.followers_count = followers_count;
		this.friends_count = friends_count;
		this.favourites_count = favourites_count;
		this.statuses_count = statuses_count;
		this.country = country;
	}

	@DynamoDBHashKey(attributeName = "id")
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@DynamoDBAttribute(attributeName = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@DynamoDBAttribute(attributeName = "followers_count")
	public double getFollowers_count() {
		return followers_count;
	}

	public void setFollowers_count(double followers_count) {
		this.followers_count = followers_count;
	}

	@DynamoDBAttribute(attributeName = "friends_count")
	public double getFriends_count() {
		return friends_count;
	}

	public void setFriends_count(double friends_count) {
		this.friends_count = friends_count;
	}

	@DynamoDBAttribute(attributeName = "favourites_count")
	public double getFavourites_count() {
		return favourites_count;
	}

	public void setFavourites_count(double favourites_count) {
		this.favourites_count = favourites_count;
	}

	@DynamoDBAttribute(attributeName = "statuses_count")
	public double getStatuses_count() {
		return statuses_count;
	}

	public void setStatuses_count(double statuses_count) {
		this.statuses_count = statuses_count;
	}

	@DynamoDBAttribute(attributeName = "country")
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	@Override
	public String toString() {
		return "TwitterUserRecord [id=" + id + ", name=" + name + ", followers_count=" + followers_count
				+ ", friends_count=" + friends_count + ", favourites_count=" + favourites_count + ", statuses_count="
				+ statuses_count + ", country=" + country + "]";
	}
	
	

}
