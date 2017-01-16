package com.ezespn.api.rest.resources;


public class APIException extends Exception
{

	private static final long serialVersionUID = -5428199404268680082L;
	private int errorCode;
	private String errorDesc;
	private String errorDetail;
	private int httpError;

	public static final APIException API_KEY_NOT_FOUND = new APIException(2, "API_KEY not found in request", 400);
	public static final APIException API_KEY_MALFORMED = new APIException(3, "API_KEY Malformed", 401);
	public static final APIException GENERAL_EXCEPTION = new APIException(999, "General Exception", 500);
	public static final APIException NOT_IMPLEMENTED = new APIException(8, "Not Implemented", 404);
	
	
	
	public APIException(int error_code, String error_desc, int http_Error) {
		this.errorCode = error_code;
		this.errorDesc = error_desc;
		this.httpError = http_Error;	
	}

	public int getHttpError()
	{
		return httpError;
	}

	public void setHttpError(int http_Error)
	{
		this.httpError = http_Error;
	}

	public void setErrorCode(int error_code)
	{
		this.errorCode = error_code;
	}

	public void setErrorDescription(String error_desc)
	{
		this.errorDesc = error_desc;
	}

	public int getErrorCode()
	{
		return errorCode;
	}

	public String getErrorDescription()
	{
		return errorDesc;
	}
	
	public String getErrorDetail()
	{
		return errorDetail;
	}

	public APIException(int error_code, String error_desc) {
		super();
		this.errorCode = error_code;
		this.errorDesc = error_desc;
		this.httpError = error_code;
	}

	@Override
	public String toString()
	{
		return "APIException [error_code=" + errorCode + ", error_desc=" + errorDesc + ", http_Error=" + httpError + "]";
	}
	
	public APIException setDetail(Throwable e)
	{
		this.errorDetail = e.getMessage();
		return this;
	}
	
	public APIException setDescription(String description)
	{
		this.errorDesc = description;
		return this;
	}
}
