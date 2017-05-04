package com.aws.twitter.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {
	public static void loadFileProperties(String defaultName) {
		try {

			InputStream propFile = new FileInputStream(defaultName + ".properties");
			Properties p = new Properties(System.getProperties());
			p.load(propFile);
			System.setProperties(p);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// can't read file
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
