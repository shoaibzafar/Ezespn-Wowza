package com.ezespn.api.rest;

import org.restlet.Application;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.engine.header.Header;
import org.restlet.engine.header.HeaderConstants;
import org.restlet.routing.Filter;
import org.restlet.routing.Router;
import org.restlet.util.Series;

public class ApplicationRouter extends Application{

	@SuppressWarnings("unused")
	private Filter createCorsFilter(Restlet next) {
		Filter filter = new Filter(getContext(), next) {
			@SuppressWarnings("unchecked")
			@Override
			protected int beforeHandle(Request request, Response response) {
				// Initialize response headers

				Series<Header> responseHeaders = (Series<Header>) response
						.getAttributes().get(HeaderConstants.ATTRIBUTE_HEADERS);
				if (responseHeaders == null) {
					responseHeaders = new Series<Header>(Header.class);
				}

				// Request headers

				Series<Header> requestHeaders = (Series<Header>) request
						.getAttributes().get(HeaderConstants.ATTRIBUTE_HEADERS);
				String requestOrigin = requestHeaders.getFirstValue("Origin",
						false, "*");
				String rh = requestHeaders.getFirstValue(
						"Access-Control-Request-Headers", false, "*");

				// Set CORS headers in response

				responseHeaders.set(
						"Access-Control-Expose-Headers",
						"Authorization, Link");
				responseHeaders.set("Access-Control-Allow-Credentials", "true");
				responseHeaders.set("Access-Control-Allow-Methods",
						"GET,POST,PUT,DELETE");
				responseHeaders.set("Access-Control-Allow-Origin", requestOrigin);
				responseHeaders.set("Access-Control-Allow-Headers", rh);

				// Set response headers

				response.getAttributes().put(HeaderConstants.ATTRIBUTE_HEADERS,
						responseHeaders);

				// Handle HTTP methods

				if (org.restlet.data.Method.OPTIONS.equals(request.getMethod())) {
					return Filter.STOP;
				}
				return super.beforeHandle(request, response);
			}
		};
		return filter;
	}

	@Override
	public synchronized Restlet createInboundRoot() 
	{
		Router router = new Router(getContext());
//		router.attach("/addChannel",AddChannel.class);
//		router.attach("/removeChannel",RemoveChannel.class);
//		router.attach("/addEvent",AddEvent.class);
//		router.attach("/removeEvent",RemoveEvent.class);
//		router.attach("/channelStatus/{channel_name}",StreamStatus.class);
//		router.attach("/homeChannelStatus/{streamName}",CheckHomeChanelStatus.class);
//		
		
		return  createCorsFilter(router);
	}
	
}
