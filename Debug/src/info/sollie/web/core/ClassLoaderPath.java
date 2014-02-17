package info.sollie.web.core;

public interface ClassLoaderPath {

	public void getClassLoaderPaths(StringBuilder result, ClassLoader classLoader);
	
	public String getClassLoaderPath(String clazz);
}
