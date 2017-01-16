package com.ezespn.utils;

public class ValidationException extends Exception{

	private static final long serialVersionUID = 1L;
	
	private String error_desc;
	
	public static final ValidationException INVALID_OR_NULL = new ValidationException("Got null or invalid data");
	public static final ValidationException ARGUMENTS_MISMATCH = new ValidationException("Arguments Mismatch");
	
	public ValidationException(String error_desc) {
		this.error_desc = error_desc;
	}
	
	@Override
	public String toString()
	{
		return "ValidationExpection [error_desc=" + error_desc + "]";
	}
}


