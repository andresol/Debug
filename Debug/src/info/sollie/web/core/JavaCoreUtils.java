/**
 * 
 */
package info.sollie.web.core;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * @author Andre Sollie
 * 
 */
public class JavaCoreUtils {

	private static final DefaultClassLoaderPathFinders defaultClassLoaderPathFinders = new DefaultClassLoaderPathFinders();
	private static final Comparator<ThreadInfo> threadCPUInfoComparator = new ThreadInfoCPURunnableComperator();
	
	public static MemoryPoolMXBean findTenuredGenPool() {
		for (MemoryPoolMXBean pool : ManagementFactory.getMemoryPoolMXBeans()) {
			if (pool.getType() == MemoryType.HEAP
					&& pool.isUsageThresholdSupported()) {
				return pool;
			}
		}
		throw new AssertionError("Error");
	}

	public static final String getJvmCoreInfo() {
		StringBuffer result = new StringBuffer();
		String sep = System.getProperty("line.separator");

		final RuntimeMXBean runTimeMX = ManagementFactory.getRuntimeMXBean();
		final OperatingSystemMXBean myOsBean=  ManagementFactory.getOperatingSystemMXBean();

		long megsTotal = Runtime.getRuntime().totalMemory() / (1024 * 1024);
		long megsFree = Runtime.getRuntime().freeMemory() / (1024 * 1024);
		final MemoryPoolMXBean tenuredGenPool = findTenuredGenPool();
		long maxTenuredMemory = tenuredGenPool.getUsage().getMax()
				/ (1024 * 1024);
		long usedTenuredMemory = tenuredGenPool.getUsage().getUsed()
				/ (1024 * 1024);
		long[] jvmUpTime = milliSekundsToTime(runTimeMX.getUptime());
		double load = myOsBean.getSystemLoadAverage();
		List<String> inputArguments = runTimeMX.getInputArguments();
		Map<String, String> systemProperties = runTimeMX.getSystemProperties();
		String arguments = "";
		for (String input : inputArguments) {
			arguments += input + " ";
		}
		int percentFree = (int) (100d * ((double) Runtime.getRuntime()
				.freeMemory() / (double) Runtime.getRuntime().totalMemory()));
		result.append("OS: ").append(systemProperties.get("os.name")).append(sep);
		result.append("Java version: ").append(systemProperties.get("java.version")).append(sep);
		result.append("Input Arguments: ").append(arguments).append(sep);
		result.append("Uptime: Days: ").append(jvmUpTime[0]).append(" Hours: ").append(jvmUpTime[1])
		.append(" Minutes: ").append(jvmUpTime[2]).append(" Seconds: ").append(jvmUpTime[3]).append(sep);
		result.append("Total memory: ").append(megsTotal).append(" MB.").append(sep);
		result.append("Total free memory: ").append(megsFree).append(" MB (").append(percentFree).append("%)").append(sep);
		result.append("Max old generation memory: ").append(maxTenuredMemory).append(" MB.").append(sep);
		result.append("Used old generation memory: ").append(usedTenuredMemory).append(" MB.").append(sep);
		result.append("Load: ").append((load >= 0 ? load :"NA")).append(" last min.").append(sep);
		result.append(sep);
		return result.toString();
	}

	private static final long[] milliSekundsToTime(long millisecond) {
		long[] date = new long[4];
		long oneDay = 24 * 60 * 60 * 1000;
		long oneHour = 60 * 60 * 1000;
		long oneMinute = 60 * 1000;
		long oneSecond = 1000;
		date[0] = Math.abs(millisecond / oneDay);
		millisecond -= (date[0] * oneDay);
		date[1] = Math.abs(millisecond / oneHour);
		millisecond -= (date[1] * oneHour);
		date[2] = Math.abs(millisecond / oneMinute);
		millisecond -= (date[2] * oneMinute);
		date[3] = Math.abs(millisecond / oneSecond);
		return date;
	}

	public static final String getTreadInfo(String sep, String filters, boolean deadlock) {
		StringBuffer result = new StringBuffer(10240);
		final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
		ThreadInfo[] threadInfos;
		if (deadlock) {
			long[] ids = threadMXBean.findDeadlockedThreads();
			if (ids == null || ids.length == 0) {
				return "No deadlocks";
			}
			threadInfos = threadMXBean.getThreadInfo(ids, true, true);
		} else {
			threadInfos = threadMXBean.dumpAllThreads(true, true);
		}
		double totalCpuTime = 0;
		List<ThreadInfo> list = Arrays.asList(threadInfos);
		Collections.sort(list, threadCPUInfoComparator);
		for (int i = 0; i < list.size(); i++) {
			ThreadInfo threadInfo = list.get(i);
			StringBuilder thread = new StringBuilder(1024);
			StringBuffer info = new StringBuffer(120);
			info.append("Thread id: ").append(threadInfo.getThreadId()).append(sep);
			info.append("Thread state: ").append(threadInfo.getThreadState()).append(sep);
			double cpuTime = (double) threadMXBean.getThreadCpuTime(threadInfo.getThreadId()) / 1000000000;
			totalCpuTime += cpuTime;
			info.append("Thread CPU time: ").append(cpuTime).append(" Seconds").append(sep);
			info.append("Thread Name: ").append(threadInfo.getThreadName()).append(sep);
			StackTraceElement[] els = threadInfo.getStackTrace();
			thread.append(info);
			StringBuilder stacktrace = new StringBuilder(512);
			for (int j = 0; j < els.length; j++) {
				StackTraceElement el = els[j];
				stacktrace.append(el.toString()).append(sep);
			}
			thread.append(stacktrace);
			boolean isInternal = checkIfInternal(stacktrace.toString(), filters);
			if ((isInternal && stacktrace.length() > 0 )|| filters.length() == 0) {
				result.append(thread).append(sep);
			}
		}
		result.append("Total cpu time on all threads: " + totalCpuTime + " Seconds");
		return result.toString();
	}

	private static boolean checkIfInternal(String stacktrace, String filters) {
		boolean result = false;
		if (filters.length() > 0) {
			for (String line :  stacktrace.split("\\r?\\n")) {
				if (!line.matches(filters)) {
					return true;
				}
			}
		}
		return result;
	}

	public static final String getInitInfo(final String sep) {
		StringBuilder result = new StringBuilder(1024);
		result.append("ClassPath(java.class.path): ").append(System.getProperty("java.class.path"));
		return result.toString();
	}

	public static final String getContextClassLoaderInfo(final String sep) {
		StringBuilder result = new StringBuilder(1024);
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		JavaCoreUtils.getClassLoaderPaths(result, classLoader);
		result.append("\n");
		return result.toString();
	}

	/**
	 * Get the possible path that the classloader uses to load its classes from.
	 * @param result
	 * @param classLoader
	 */
	private static void getClassLoaderPaths(StringBuilder result, ClassLoader classLoader) {
		defaultClassLoaderPathFinders.getClassLoaderPath().getClassLoaderPaths(result, classLoader);
	}

	public static String getClassLoaderInfoForClass(String clazz) {
		StringBuilder builder = new StringBuilder(256);
		builder.append("\n");
		builder.append("Class ").append(clazz).append(": ");
		builder.append(defaultClassLoaderPathFinders.getClassLoaderPath().getClassLoaderPath(clazz));
		builder.append("\n");
		return builder.toString();
	}
	
	private static final class ThreadInfoCPURunnableComperator implements Comparator<ThreadInfo> {
		private static final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
		@Override
		public int compare(ThreadInfo object1, ThreadInfo object2) {
			if (object1 == null) return 1;
			if (object2 == null) return -1;
			long cpuobject1 =  threadMXBean.getThreadCpuTime(object1.getThreadId());
			long cpuobject2 =  threadMXBean.getThreadCpuTime(object2.getThreadId());
			return (int) (cpuobject2 - cpuobject1);
		}
		
	}
}
