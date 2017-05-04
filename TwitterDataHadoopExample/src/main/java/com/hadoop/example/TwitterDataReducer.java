package com.hadoop.example;

import java.io.IOException;

import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class TwitterDataReducer extends Reducer<Text, Text, Text, Text> {
	MapWritable mapWritable = new MapWritable();

	public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
		int maxvalue = 0;
		String name = "";
		System.out.println(key);

		for (Text val : values) {
			System.out.println("@@@@@@@@@@" + val.getBytes());
			String s = new String(val.getBytes());
			String str[] = s.split(" ");
			int v = Integer.parseInt(str[0]);
			String n1 = str[1];
			if (v > maxvalue) {
				maxvalue = v;
				name = n1;
			}
		}
		Text text = new Text();
		text.set(name + " " + maxvalue);
		context.write(key, text);
	}
}