package com.ezespn.utils;

import java.util.Map;

import org.json.simple.JSONObject;


public class Validator
{

	public static String PARAMS_START_STREAM[] = {"channel_name"};
	public static String PARAMS_CREATE_EVENT[] = {"event_name"};
	public static String PARAMS_REMOVE_USER  [] = {"username","password"};
	public static String PARAMS_REMOVE_EVENT  [] = {"username","password"};
	
	public static void validate(JSONObject params, String[] matcher) throws ValidationException
	{
		for (int i = 0; i < matcher.length; i++)
		{
			if (params.containsKey(matcher[i]))
			{
				String variable = (String) params.get(matcher[i]);
				Utilities.log("Validator", "Value for " + matcher[i] + " is " + variable);
				if (variable == null || variable.equals(""))
				{
					
					throw ValidationException.INVALID_OR_NULL;
					
				}
			} else
			{
				throw ValidationException.ARGUMENTS_MISMATCH;
			}
		}
	}

	public static void validateMap(Map<String, String> params, String[] matcher) throws ValidationException
	{
		for (int i = 0; i < matcher.length; i++)
		{
			Utilities.log("Validator", "Checking for param " + matcher[i]);
			if (params.containsKey(matcher[i]) && (String) params.get(matcher[i]) != null && (String) params.get(matcher[i]) != "")
			{
				continue;
			} else
				throw ValidationException.ARGUMENTS_MISMATCH;
		}
	}
}

