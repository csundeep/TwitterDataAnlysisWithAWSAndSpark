package com.aws.twitter.model;

import java.nio.ByteBuffer;

public class Event {

	public Event(String partitionKey, ByteBuffer data) {
		super();
		this.partitionKey = partitionKey;
		this.data = data;
	}

	public Event(String partitionKey, String data) {
		super();
		this.partitionKey = partitionKey;
		this.data = ByteBuffer.wrap(data.getBytes());
	}

	private String partitionKey;

	private ByteBuffer data;

	public String getPartitionKey() {
		return partitionKey;
	}

	public void setPartitionKey(String partitionKey) {
		this.partitionKey = partitionKey;
	}

	public ByteBuffer getData() {
		return data;
	}

	public void setData(ByteBuffer data) {
		this.data = data;
	}
}
