/*
 * Dacara V2
 * Copyright 2015 by Marcus Warm
 */
package de.mwvb.dacara;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import de.mwvb.dacara.config.XmlFileBasedConfiguration;
import de.mwvb.dacara.db.DacaraSQLExecutor;
import de.mwvb.dacara.gui.Application;

/**
 * Starts Dacara 2
 * 
 * @author Marcus Warm
 */
public class Start {
	public static final String VERSION = "2.1.0";
	private static Injector injector = Guice.createInjector(new Dependencies());
	
	/**
	 * You need Java 8 (u60) for running this application.
	 * @param args none
	 */
	public static void main(String[] args) {
		Application.launch(Application.class, new String[] {});
	}
	
	public static Injector getInjector() {
		return injector;
	}
	
	private static class Dependencies extends AbstractModule {
		@Override
		protected void configure() {
			bind(SQLExecutor.class).to(DacaraSQLExecutor.class);
			bind(Configuration.class).to(XmlFileBasedConfiguration.class);
		}
	}
	
	/* ROADMAP: NEXT VERSION OF DACARA
	 * - Context menu for rows: show+edit row (generates UPDATE), copy field, copy row, export as CSV
	 * - Ask for password if it is not configured, remeber it, but not save it to HD
	 * - Help for SQL command syntax: links to websites, search function?
	 */
}
