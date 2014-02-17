/**
 * Contains all ClassloaderPathFinders. It also has an WebSphere impl. that needs to be compiled against Websphere jvm. Uncomment for use.
 */
package info.sollie.web.core;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;

//import com.ibm.ws.bootstrap.ExtClassLoader;
//import com.ibm.ws.classloader.CompoundClassLoader;

/**
 * @author Andre Sollie
 *
 */
public class DefaultClassLoaderPathFinders implements ClassLoaderPathFinders {
	
	private static final ClassLoaderPath defaultClassLoaderPath = new JavaClassLoaderPathFinder();
	
	@Override
	public ClassLoaderPath getClassLoaderPath() {
		return DefaultClassLoaderPathFinders.defaultClassLoaderPath;
	}
	
	/**
	 * Export this class into a jar and put it inside the lib/ext folder under IBM Websphere for use. 
	 * @author Andre Sollie
	 *
	 */
//	private static class IBMClassLoaderPathFinder extends JavaClassLoaderPathFinder implements ClassLoaderPath {
//		
//		/**
//		 * @see info.sollie.web.util.classloading.interfaces.ClassLoaderPath#getClassLoaderPaths(java.lang.StringBuilder, java.lang.ClassLoader)
//		 */
//		@Override
//		public void getClassLoaderPaths(StringBuilder result, ClassLoader classLoader) {
//			try {
//				if (classLoader instanceof CompoundClassLoader) {  // IBM spesific.
//					CompoundClassLoader compoundClassLoader = (CompoundClassLoader) classLoader;
//					String[] paths = compoundClassLoader.getPaths();
//					for (String path : paths ) {
//						result.append(path).append(";");
//					}
//				} else if (classLoader instanceof ExtClassLoader) { // IMB spesific. 
//					ExtClassLoader extClassLoader = (ExtClassLoader) classLoader;
//					result.append(extClassLoader.getClassPath());
//				} 
//			} catch (NoClassDefFoundError e) {
//				//Skip.
//			}
//			super.getClassLoaderPaths(result, classLoader);
//		}
//
//
//		@Override
//		public String getClassLoaderPath(String clazz) {
//			return HelpClassLoaderPathFinder.getClassLoaderPath(clazz);
//		}
//	}
	
	private static class JavaClassLoaderPathFinder implements ClassLoaderPath {
		
		/**
		 * @see info.sollie.web.util.classloading.interfaces.ClassLoaderPath#getClassLoaderPaths(java.lang.StringBuilder, java.lang.ClassLoader)
		 */
		@Override
		public void getClassLoaderPaths(StringBuilder result, ClassLoader classLoader) {
			if (classLoader instanceof URLClassLoader) {
				URLClassLoader uRLClassLoader = (URLClassLoader) classLoader;
				URL[] urls = uRLClassLoader.getURLs();
				if (urls != null) {
					for (URL url : urls ) {
						result.append(url.toString()).append(";");
					}
				}
			}
			
			ClassLoader parent = classLoader.getParent();
			if (parent != null ) {
				result.append("\n\nParents classpath: ").append(classLoader.getClass().getSimpleName()).append("\n");
				this.getClassLoaderPaths(result, parent);
			}
		}

		@Override
		public String getClassLoaderPath(String clazz) {
			return HelpClassLoaderPathFinder.getClassLoaderPath(clazz);
		}
	}
	
	
	private static class HelpClassLoaderPathFinder {

		public static String getClassLoaderPath(String clazz) {
			String result = "Don't know where it is.";
			try {
				Class<?> clazzz = Class.forName(clazz);
				String path = "";
				URL url = HelpClassLoaderPathFinder.class.getClassLoader().getResource(getClassFileName(clazzz.getName()));
				if (url !=null ){
					result = url.toString();
				} else {
					if (clazzz != null && clazzz.getProtectionDomain().getCodeSource() != null) {
						path = clazzz.getProtectionDomain().getCodeSource().getLocation().getPath();
						result = URLDecoder.decode(path, "UTF-8");
					} 
				}
			} catch (ClassNotFoundException e) {
				result = "Error ClassNotFound";
			} catch (UnsupportedEncodingException e) {
				result = "Error UnsupportedEncodingException";
			}
			return result;
		}
		
		 public static String getClassFileName(String clazzBinName) {
		        return clazzBinName.replace('.', '/') + ".class";            
		 }
	}
}
