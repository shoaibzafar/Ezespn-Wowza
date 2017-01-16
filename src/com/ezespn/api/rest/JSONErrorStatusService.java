package com.ezespn.api.rest;

import org.json.simple.JSONObject;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.service.StatusService;

import com.wowza.wms.logging.WMSLoggerFactory;

public class JSONErrorStatusService extends StatusService{

	public JSONErrorStatusService() {
		super();
		super.setOverwriting(true);
	}

	public JSONErrorStatusService(boolean enabled) {
		super(enabled);
		super.setOverwriting(true);
	}

	@Override
	public Representation getRepresentation(Status status, Request request, Response response)
	{
		WMSLoggerFactory.getLogger(getClass()).info("Error code " + status.getCode());
		if (status.getCode() > 199 && status.getCode() < 300)
			return super.getRepresentation(status, request, response);
		else
			return getErrorRepresentation(status, request, response);
	}

	@SuppressWarnings("unchecked")
	private Representation getErrorRepresentation(Status status, Request request, Response response)
	{
		JSONObject json = new JSONObject();

		json.put("error_code", status.getCode());
		json.put("error_reason", status.getReasonPhrase());
		json.put("error_description", status.getDescription());

		return new JsonRepresentation(json);
	}

}

