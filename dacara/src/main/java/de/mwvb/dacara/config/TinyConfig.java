package de.mwvb.dacara.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class TinyConfig {

	public static void save(final String name, final String data) throws Exception {
		final FileWriter w = new FileWriter(new File(dateiname(name)));
		w.write(data);
		w.close();
	}
	
	public static String load(final String name) throws Exception {
		final File file = new File(dateiname(name));
		final BufferedReader r = new BufferedReader(new FileReader(file));
		try {
			return r.readLine();
		} finally {
			r.close();
		}
	}
	
	private static String dateiname(final String name) {
		return System.getProperty("user.home") + File.separator + "DACARA_" + name + ".cfg";
	}
}
