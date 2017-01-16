package com.ezespn.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import com.wowza.wms.application.IApplicationInstance;

public class Settings {

private static final String CONF_FILENAME = "conf.properties";
private static Properties props = new Properties();
		
	public static void LoadSettings(IApplicationInstance appInstance) {

		try
		{
			String strPath = appInstance.getApplication().getApplicationPath();
			props.load(new FileInputStream(strPath+File.separatorChar+CONF_FILENAME));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
		
	public static int getIntegerProperty(String str)
	{
		return getIntegerProperty(str, 0);
	}
	
	public static int getIntegerProperty(String str, int defaultVal)
	{

		String totalStr = props.getProperty(str);
		int result = defaultVal;
		try
		{
			totalStr.trim();
			totalStr.replaceAll(" ", "");
			result = Integer.parseInt(totalStr);
		} catch (Exception e)
		{
		}
		return result;
	}
	
	public static String getStringProperty(String str)
	{
		return getStringProperty(str, null);
	}
	
	public static String getStringProperty(String str, String defaultVal)
	{

		String tmp = defaultVal;
		try
		{
			if (props.getProperty(str) != null)
				tmp = props.getProperty(str);
		} catch (Exception e)
		{
			Utilities.log("Settings.getStringProperty ", e.toString() + " " + e.getMessage());
		}
		return tmp;
	}
	
	public static long getLongProperty(String str)
	{
		return getLongProperty(str, 0);
	}
	
	public static long getLongProperty(String key, long defaultVal)
	{
		String tmp = props.getProperty(key);
		long result = defaultVal;
		try
		{
			result = Long.parseLong(tmp);
		} catch (Exception e)
		{
		}
		return result;
	}
	
	public static boolean getBooleanProperty(String key)
	{
		return getBooleanProperty(key, false);
	}
	
	public static boolean getBooleanProperty(String key, boolean defaultVal)
	{
		String tmp = props.getProperty(key);
		boolean result = defaultVal;
		try
		{
			result = Boolean.parseBoolean(tmp);
		} catch (Exception e)
		{
		}
		return result;
	}
	
}
