package com.tibco.epbu.sb.adapter.http;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.streambase.sb.Schema;
import com.streambase.sb.StreamBaseException;
import com.streambase.sb.Tuple;
import com.streambase.sb.Schema.Field;
import com.streambase.sb.client.StreamBaseClient;

public class StreamSubmitServlet extends HttpServlet {

	private static final long serialVersionUID = 1431086787480169906L;


	private StreamBaseClient connect() throws StreamBaseException {
		return new StreamBaseClient(getInitParameter("StreambaseUrl"));
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		
		String streamName = req.getPathInfo().substring(1);
		try {
			StreamBaseClient client = connect();
			try {

				res.setContentType("text/plain");
				res.getWriter().write(client.getStreamProperties(streamName).toString());
//				Schema schema = client.getSchemaForStream(streamName);
//				Tuple tuple = schema.createTuple();
//				client.enqueue(streamName, tuple);
				
//				res.getWriter().write(schema.toString());
				//client.enqueue(props, tuples)
				
			} finally {
				client.close();
			}
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		String streamName = req.getPathInfo().substring(1);

		try {

			StringWriter writer = new StringWriter();
			Reader reader = req.getReader();
			char[] buffer = new char[8192];
			for (;;) {
				int r = reader.read(buffer);
				if (r<0) break;
				writer.write(buffer, 0, r);
			}

			JSONObject j = (JSONObject)JSON.parse(writer.toString());
			
			StreamBaseClient client = connect();
			try {

				Schema schema = client.getSchemaForStream(streamName);

				Tuple tuple = schema.createTuple();
				for (Field field : schema.getFields()) {
					Object v = j.get(field.getName());
					if (v!=null) tuple.setField(field, v);
				}

				client.enqueue(streamName, tuple);
				
				res.setStatus(HttpServletResponse.SC_NO_CONTENT);

			} finally {
				client.close();
			}

		} catch (Exception e) {
			throw new ServletException(e);
		}
	}
	
}
