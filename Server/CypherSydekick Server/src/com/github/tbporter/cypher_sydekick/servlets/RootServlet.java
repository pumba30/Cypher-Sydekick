package com.github.tbporter.cypher_sydekick.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.tbporter.cypher_sydekick.database.DatabaseManager;
import com.github.tbporter.cypher_sydekick.database.DatabaseManagerException;

/**
 * Serves a diagnostic page for viewing and adding users.
 * 
 * @author ayelix
 * 
 */
public class RootServlet extends HttpServlet {
	/** Header for HTML pages. */
	private static final String HTML_HEAD = "<!DOCTYPE html>\n<html>\n<body>\n";
	/** Footer for HTML pages. */
	private static final String HTML_FOOT = "\n</body>\n</html>";

	/** HTML string for "Add User" form. */
	private static final String HTML_ADD_USER_FORM = "<form action=\"users\" method=\"post\">\nUsername: <input type=\"text\" name=\"username\">\n<input type=\"submit\" value=\"Add User\">\n</form>\n";

	/**
	 * Writes the "add user" form and list of users.
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/html");
		PrintWriter out = resp.getWriter();

		out.write(HTML_HEAD);

		// Place the add user form at the top
		out.write(HTML_ADD_USER_FORM);

		// Print all current users below the form
		out.write("<b>Current users:</b><br>\n");
		try {
			final List<String> users = DatabaseManager.getAllUsers();
			for (final String user : users) {
				out.write(user + "<br>\n");
			}

		} catch (DatabaseManagerException e) {
			out.write("<i>Unable to read user list.</i><br>\n");
		}

		out.write(HTML_FOOT);
	}

	/**
	 * Forwards request to doGet()
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		doGet(req, resp);
	}

}