package com.spark.example;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.mapred.JobConf;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;

import scala.Tuple2;

public class TwitterData {
	public static void run(String filename, String output) {
		SparkConf conf = new SparkConf().setMaster("yarn-cluster").setAppName("Work Count App");
		JavaSparkContext sc = new JavaSparkContext(conf);
		JobConf jobConf = new JobConf(sc.hadoopConfiguration());

		jobConf.set("dynamodb.servicename", "dynamodb");
		jobConf.set("dynamodb.input.tableName", "TwitterUserRecords");
		jobConf.set("dynamodb.endpoint", "dynamodb.us-east-1.amazonaws.com");
		jobConf.set("dynamodb.regionid", "us-east-1");
		jobConf.set("dynamodb.throughput.read", "1");
		jobConf.set("dynamodb.throughput.read.percent", "1");
		jobConf.set("dynamodb.version", "2011-12-05");

		jobConf.set("mapred.output.format.class", "org.apache.hadoop.dynamodb.write.DynamoDBOutputFormat");
		jobConf.set("mapred.input.format.class", "org.apache.hadoop.dynamodb.read.DynamoDBInputFormat");
		
		
		JavaRDD<String> input = sc.textFile("s3n://sandysparkoutput/input/twitterData.txt");

		JavaRDD<String[]> words = input.flatMap(new FlatMapFunction<String, String[]>() {
			private static final long serialVersionUID = 0;

			public Iterator<String[]> call(String s) {
				List<String[]> l = new ArrayList<>();
				String temp[] = s.split("\t");
				l.add(temp);
				return l.iterator();
			}
		});

		JavaPairRDD<String, String> counts = words.mapToPair(new PairFunction<String[], String, String>() {
			private static final long serialVersionUID = 0;

			@Override
			public Tuple2<String, String> call(String[] s) throws Exception {
				// TODO Auto-generated method stub
				System.out.println(s[1] + " " + Integer.parseInt(s[4]));
				return new Tuple2<String, String>(s[1], s[4] + " " + s[5]);
			}
		});

		JavaPairRDD<String, String> reducedCounts = counts.reduceByKey(new Function2<String, String, String>() {
			private static final long serialVersionUID = 0;

			@Override
			public String call(String v1, String v2) throws Exception {
				int i1 = Integer.parseInt(v1.split(" ")[0]);
				int i2 = Integer.parseInt(v2.split(" ")[0]);
				if (i1 > i2)
					return v1;
				else
					return v2;

			}
		});

		reducedCounts.saveAsTextFile("s3n://sandysparkoutput" + output);
		sc.close();
	}

	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("Usage: Twitter Data");
			System.exit(0);
		}

		run(args[0], args[1]);
	}
}