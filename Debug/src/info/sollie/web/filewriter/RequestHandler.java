package info.sollie.web.filewriter;

import info.sollie.web.core.JavaCoreUtils;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Main method for debugging web applications. It only display plaintext.
 * 
 * @author Andre Sollie
 *
 */
public class RequestHandler extends HttpServlet implements Servlet {
	
	private static final String NEW_LINE = "\n";

	private static final String defaultFilters = "com\\.ibm|com\\.sun|java|javax|sun|sunw";

	/**
	 */
	private static final long serialVersionUID = -7649424677730859313L;

	/**
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		setHeaders(response);
		
		String filter = request.getParameter("filter");
		boolean deadlock = "true".equalsIgnoreCase(request.getParameter("deadlock"));
		String filters = "";
		if (filter != null && filter.equalsIgnoreCase("internal")) {
			String classNameServlet = request.getSession(true).getServletContext().getClass().getName();
			String getFilterClass = classNameServlet.replaceAll("^(.*?\\..*?)\\..*", "$1");
			filters = "^(" + defaultFilters + "|" + getFilterClass + ").*";
		}
		
		String clazz = request.getParameter("class");

		out.print("Usage: filter=true only internal threads, deadlock=true check for deadlocks, class=java.lang.List check jar location.");
		out.print(NEW_LINE + NEW_LINE);
		out.print(JavaCoreUtils.getJvmCoreInfo());
		out.print(NEW_LINE);
		out.print(JavaCoreUtils.getInitInfo(NEW_LINE));
		out.print(NEW_LINE);
		out.print(JavaCoreUtils.getContextClassLoaderInfo(NEW_LINE));
		out.print(NEW_LINE);
		if (clazz == null) {
			out.print(JavaCoreUtils.getTreadInfo(NEW_LINE, filters, deadlock));
		}
		out.flush();
		
		if (clazz != null) {
			out.print(JavaCoreUtils.getClassLoaderInfoForClass(clazz));
		}
		out.close();
	}

	private void setHeaders(final HttpServletResponse resp) throws IOException {
		resp.setContentType("text/plain; charset=utf-8");
		resp.setHeader("Cache-Control", "no-cache");
	}	

	/**
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.doGet(req, resp);
	}
}
