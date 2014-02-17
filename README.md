Debug
=====

Debug web applications very easy. Just deploy the war [a link](https://github.com/andresol/Debug/bin/Debug.war) on a web server. Restart and go to /Debug. 

It can help you resolve problems like. Hanging threads? Very CPU intense jobs? Trouble finding the class inside a class path jungle? Memory usage? Why is GC blocking my app?  

It will display threads, memory stats, thread cpu usage, classPaths and find the jar containing the class inside the classpath. 

Usage: 
/Debug

Parameters:
filter=true only internal threads. Means only internal threads is displayed.
deadlock=true check for deadlocks. Check for thread deadlocks.
class=java.lang.List. Find the jar that contains. Result is: Class java.io.UnsupportedEncodingException: jar:file:/Library/Java/JavaVirtualMachines/jdk1.7.0_45.jdk/Contents/Home/jre/lib/rt.jar!/java/io/UnsupportedEncodingException.class

OS: Mac OS X
Java version: 1.7.0_45
Input Arguments: -agentlib:jdwp=transport=dt_socket,suspend=y,address=localhost:58293 -Dfile.encoding=US-ASCII 
Uptime: Days: 0 Hours: 0 Minutes: 0 Seconds: 14
Total memory: 245 MB.
Total free memory: 221 MB (90%)
Max old generation memory: 2730 MB.
Used old generation memory: 0 MB.
Load: 1.91259765625 last min.

ClassPath(java.class.path): /Users/user/Documents/Development/apache-tomcat-7.0.47/bin/bootstrap.jar:file:/Users/user/Documents/workspace/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/wtpwebapps/Debug/WEB-INF/classes/;

Parents classpath: WebappClassLoader
file:/Users/user/apache-tomcat-7.0.47/lib/;file:/Users/user/apache-tomcat-7.0.47/lib/annotations-api.jar;

Parents classpath: StandardClassLoader
file:/Users/user/Documents/Development/apache-tomcat-7.0.47/bin/bootstrap.jar;….

Parents classpath: AppClassLoader
file:/Library/Java/JavaVirtualMachines/jdk1.7.0_45.jdk/Contents/Home/jre/lib/ext/dnsns.jar;….

Thread id: 1
Thread state: RUNNABLE
Thread CPU time: 0.755237 Seconds
Thread Name: main
java.net.PlainSocketImpl.socketAccept(Native Method)
java.net.AbstractPlainSocketImpl.accept(AbstractPlainSocketImpl.java:398)
java.net.ServerSocket.implAccept(ServerSocket.java:530)
java.net.ServerSocket.accept(ServerSocket.java:498)
org.apache.catalina.core.StandardServer.await(StandardServer.java:452)
org.apache.catalina.startup.Catalina.await(Catalina.java:779)
org.apache.catalina.startup.Catalina.start(Catalina.java:725)
sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57)
sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
java.lang.reflect.Method.invoke(Method.java:606)
org.apache.catalina.startup.Bootstrap.start(Bootstrap.java:322)
org.apache.catalina.startup.Bootstrap.main(Bootstrap.java:456)