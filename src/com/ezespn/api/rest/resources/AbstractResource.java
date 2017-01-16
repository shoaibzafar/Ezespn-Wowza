package com.ezespn.api.rest.resources;

import org.restlet.data.Status;
import org.restlet.representation.BufferingRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

import com.wowza.wms.logging.WMSLogger;
import com.wowza.wms.logging.WMSLoggerFactory;

public abstract class AbstractResource extends ServerResource{

	protected static WMSLogger logger = WMSLoggerFactory.getLogger(AbstractResource.class);

	/*
	 * JSON PUT TEMPLATE METHOD
	 */
	@Put("json")
	public Representation doJsonPutTemplate(StringRepresentation entity)
	{
		Representation r = null;
		BufferingRepresentation buffer = null;
		
		try
		{
			validate();

			r = doJsonPut(entity);
			
			if (r != null)
				buffer = new BufferingRepresentation(r);
			
			getResponse().setStatus(Status.SUCCESS_OK);
		} catch (APIException e)
		{
			logger.error(e.toString());
			e.printStackTrace();
			setStatus(e);
		} 
		return buffer;
	}

	public abstract Representation doJsonPut(StringRepresentation entity) throws APIException;

	/*
	 * DELETE TEMPLATE METHOD
	 */
	@Delete("json:json")
	public void doDeleteTemplate(StringRepresentation entity)
	{
		try
		{
			validate();
			doDelete(entity);

			getResponse().setStatus(Status.SUCCESS_OK);
		}
		catch (APIException e)
		{
			logger.error(e.toString());
			e.printStackTrace();
			setStatus(e);
		}
	}

	public abstract void doDelete(StringRepresentation entity) throws APIException;

	/*
	 * JSON POST TEMPLATE METHOD
	 */
	@Post("json:json")
	public Representation jsonPostTemplate(StringRepresentation entity)
	{
		Representation r = null;
		BufferingRepresentation buffer = null;
		
		// Do security validations
		try
		{
			validate();
			r = doJSONPost(entity);
			
			if (r != null)
				buffer = new BufferingRepresentation(r);

			getResponse().setStatus(Status.SUCCESS_OK);

		} catch (APIException e)
		{
			logger.error(e.toString());
			e.printStackTrace();
			setStatus(e);
		}
		return buffer;
	}

	public abstract Representation doJSONPost(StringRepresentation entity) throws APIException;

	/*
	 * GET TEMPLATE METHOD
	 */
	@Get
	public Representation getTemplate()
	{

		Representation r = null;
		BufferingRepresentation buffer = null;

		// Do security validations
		try
		{
			validate();
			r = doGet();
			
			if (r != null)
				buffer = new BufferingRepresentation(r);

			getResponse().setStatus(Status.SUCCESS_OK);

		} catch (APIException e)
		{
			logger.error(e.toString());
			e.printStackTrace();
			setStatus(e);
		}
		return buffer;
	}
	
	public abstract Representation doGet() throws APIException;

	protected void validate() throws APIException
	{
		
	}

	protected void setStatus(APIException e)
	{
		setStatus(new Status(e.getHttpError(), e, e.getErrorDetail(), e.getErrorDescription(), ""));
	}
	
}
