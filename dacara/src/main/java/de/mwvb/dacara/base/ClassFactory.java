package de.mwvb.dacara.base;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Late binding class factory with cache
 * 
 * @author Marcus Warm
 */
public class ClassFactory {
	private final ClassLoader classLoader;
	private final Map<String, Object> cache = new HashMap<>();
	
	public ClassFactory(String libDir) {
		try {
			URL[] jars = getJARs(libDir);
			classLoader = URLClassLoader.newInstance(jars, ClassFactory.class.getClassLoader());
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	private URL[] getJARs(String dir) throws MalformedURLException {
		File f_dir = new File(dir);
		if (!f_dir.isDirectory()) {
			throw new RuntimeException("Folder does not exist: " + f_dir.getAbsolutePath());
		}
		List<URL> ret = new ArrayList<URL>();
		for (File f : f_dir.listFiles()) {
			if (f.getName().endsWith(".jar")) {
				ret.add(new URL("file", "/", f.getAbsolutePath().replace("\\", "/")));
			}
		}
		return ret.toArray(new URL[0]);
	}

	public Object newInstance(String classname) throws Exception {
		Object i = cache.get(classname);
		if (i == null) {
			i = classLoader.loadClass(classname).newInstance();
			created(i);
			cache.put(classname, i);
		}
		return i;
	}
	
	protected void created(Object i) {
	}
}
