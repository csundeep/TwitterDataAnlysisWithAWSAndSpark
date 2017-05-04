package com.hadoop.example;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class TwitterDataMapper extends Mapper<LongWritable, Text, Text, Text> {
	private Text category = new Text();

	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		String line = value.toString();
		String str[] = line.split("\t");
		if (str.length > 5) {
			category.set(str[1]);
		}
		Text text = new Text();
		text.set(str[4] + " " + str[5]);
		context.write(category, text);
	}
}