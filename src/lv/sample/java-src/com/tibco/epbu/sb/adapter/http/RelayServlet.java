package com.tibco.epbu.sb.adapter.http;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RelayServlet extends HttpServlet {

	private static final long serialVersionUID = 1431086787480169906L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

		String server = getInitParameter("TargetUrl");
		String remoteUrl = req.getPathInfo();
		String queryString = req.getQueryString();
		
		String targetUrl = server + remoteUrl;
		String response = this.executeGet(targetUrl, queryString);
		
		res.setStatus(HttpServletResponse.SC_OK);
		res.getWriter().write(response);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		
		String server = getInitParameter("TargetUrl");
		String remoteUrl = req.getPathInfo();
		String postData = this.getPostData(req);

		String targetUrl = server + remoteUrl;
		String response = this.executePost(targetUrl, postData);

		res.setStatus(HttpServletResponse.SC_OK);
		res.getWriter().write(response);
	}

	private String getPostData(HttpServletRequest req) throws ServletException {
		try {

			StringWriter writer = new StringWriter();
			Reader reader = req.getReader();
			char[] buffer = new char[8192];
			for (;;) {
				int r = reader.read(buffer);
				if (r < 0)
					break;
				writer.write(buffer, 0, r);
			}

			return writer.toString();
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

	private String executeGet(String targetUrl, String queryString) {
		URL url;
		HttpURLConnection connection = null;
		try {
			// Create connection
			url = new URL(targetUrl + "?" + queryString);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);

			// Get Response
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuffer response = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();
			return response.toString();

		} catch (Exception e) {

			e.printStackTrace();
			return null;

		} finally {

			if (connection != null) {
				connection.disconnect();
			}
		}
 	}
	
	private String executePost(String targetUrl, String postData) {
		URL url;
		HttpURLConnection connection = null;
		try {
			// Create connection
			url = new URL(targetUrl);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");

			connection.setRequestProperty("Content-Length",
					"" + Integer.toString(postData.getBytes().length));

			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);

			// Send request
			DataOutputStream wr = new DataOutputStream(
					connection.getOutputStream());
			wr.writeBytes(postData);
			wr.flush();
			wr.close();

			// Get Response
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuffer response = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();
			return response.toString();

		} catch (Exception e) {

			e.printStackTrace();
			return null;

		} finally {

			if (connection != null) {
				connection.disconnect();
			}
		}
	}	
}
