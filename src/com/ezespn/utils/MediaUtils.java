package com.ezespn.utils;

import java.io.File;
import java.util.List;

import com.wowza.wms.application.IApplicationInstance;
import com.wowza.wms.mediacaster.MediaCasterItem;
import com.wowza.wms.mediacaster.MediaCasterList;
import com.wowza.wms.mediacaster.MediaCasterStreamItem;
import com.wowza.wms.mediacaster.MediaCasterStreamManager;
import com.wowza.wms.mediacaster.MediaCasterStreamMap;
import com.wowza.wms.stream.MediaStreamMap;
import com.wowza.wms.vhost.IVHost;

public class MediaUtils
{
	public enum ResourceType {
		RECORDING, STREAM
	}

	public static String getRTMP(String recID, boolean isLive)
	{
		String recordingName = recID;
		String link = "rtmp://[WOWZA_SERVER_IP]:[WOWZA_PORT]/[APPLICATION_NAME]/"+(isLive?"":"")+"[STREAM_NAME]";
		return formatVideoUrl(recordingName, isLive, link);
	}
	public static String getRTSP(String recID, boolean isLive)
	{
		String recordingName = recID;
		String link = "rtsp://[WOWZA_SERVER_IP]:[WOWZA_PORT]/[APPLICATION_NAME]/"+(isLive?"":"")+"[STREAM_NAME]";
		return formatVideoUrl(recordingName, isLive, link);
	}
	protected static  String formatVideoUrl(String recordingName, boolean isLive, String link)
	{
		String recName = recordingName;
		link = link.replace("[WOWZA_SERVER_IP]", Utilities.getServerIP());
		link = link.replace("[WOWZA_PORT]", Utilities.getServerPort());

		if(isLive) 
		{
			link = link.replace("[STREAM_NAME]",recordingName);
			link = link.replace("[APPLICATION_NAME]", Utilities.APPLICATION_NAME);
		}
		else 
		{
			link = link.replace("[APPLICATION_NAME]", Utilities.VOD_APPLICATION_NAME);
			link = link.replace("[STREAM_NAME]",createVODStreamName(recordingName));
		}
		return link;
	}
	private static String createVODStreamName(String recordingName)
	{
		return "mp4:" +recordingName+".mp4";
	}

	@SuppressWarnings("unused")
	public static String getPlayBackLink(String recordingName)
	{
		return "http://"+ Utilities.getServerIP()  + ":" + Utilities.getServerPort() +"/" + Utilities.VOD_APPLICATION_NAME+"/_definst_"+"/mp4:"+recordingName+".mp4/playlist.m3u8";
	}

	@SuppressWarnings("unused")
	public static String getPlayBackSmilLink(String recordingName)
	{
		return "http://"+ Utilities.getServerIP()  + ":" + Utilities.getServerPort() +"/" + Utilities.VOD_APPLICATION_NAME+"/_definst_"+"/smil:"+recordingName+".smil/playlist.m3u8";
	}
	
	
	@SuppressWarnings("unused")
	public static String getLivePlayBackSmilLink(String recordingName)
	{
		return "http://"+ Utilities.getServerIP()  + ":" + Utilities.getServerPort() +"/" + Utilities.APPLICATION_NAME+"/_definst_"+"/smil:"+recordingName+".smil/playlist.m3u8";
	}
	
	
	public static String getDownloadLink(String recordingName)
	{
		return "http://"+ Utilities.getServerIP() +"/rec_content/" + recordingName+".mp4";	
	}

	public static String getHLS(String streamName,boolean isLive)
	{
		String link = "http://[WOWZA_SERVER_IP]:[WOWZA_PORT]/[APPLICATION_NAME]/_definst_/"+(isLive?"":"")+"[STREAM_NAME]/playlist.m3u8";
		return formatVideoUrl(streamName, isLive, link);
	}

	public static String getMediaStorageDir(IVHost vhost)
	{
		return Utilities.getApplicationInstance(vhost).getStreamStorageDir();
	}

	public static boolean isStreamAvailable(IVHost vhost, String StreamName)
	{
		IApplicationInstance appInstance = Utilities.getApplicationInstance(vhost);
		MediaStreamMap streams = appInstance.getStreams();
		List<String> liststreams = streams.getPublishStreamNames();
		for (String name : liststreams)
		{
			if (name.equals(StreamName))
				return true;
		}
		return false;
	}

	public static String getRecordingPath(String recordingDir, String customer_id, String event_id, String _recName)
	{
		String outputPath = recordingDir + File.separator + customer_id + File.separator + event_id + File.separator;
		File fOutput = new File(outputPath);
		if (!fOutput.exists())
		{
			fOutput.mkdirs();
		}
		return outputPath + _recName;
	}

//	public static String generateStreamName()
//	{
//		return String.valueOf(Utilities.getUnixTimeStamp() + ".stream");
//	}

	public static String getRecordingName()
	{
		return System.currentTimeMillis() + ".mp4";
	}

	public static String generatePhotoFileName()
	{
		return System.currentTimeMillis() + ".jpeg";
	}

	public static boolean startMediaCasterStream(IApplicationInstance appInstance,String streamName, String mediaCasterType)
	{
		boolean success = false;
		try
		{
			while(true)
			{
				MediaCasterStreamMap mediaCasterMap = appInstance.getMediaCasterStreams();
				IVHost vhost = appInstance.getVHost();
				MediaCasterStreamManager mediaCasterStreamManager = mediaCasterMap.getStreamManager();

				MediaCasterList mediaCasterList = vhost.getMediaCasterList();
				MediaCasterItem mediaCasterDef = mediaCasterList.getMediaCasterDef(mediaCasterType);

				if (mediaCasterDef == null)
				{
					Utilities.log("ModuleMediaCasterStreamManager", "startMediaCasterStream: MediaCaster type not found: "+mediaCasterType);
					break;
				}

				MediaCasterStreamItem mediaCasterStream = mediaCasterMap.getMediaCaster(streamName);

				if (mediaCasterStream != null)
				{
					Utilities.log("ModuleMediaCasterStreamManager", "startMediaCasterStream: MediaCaster already exists[mediacaster]: "+streamName);
					break;
				}
				success = mediaCasterStreamManager.startStream(streamName, mediaCasterType);
				mediaCasterStream = mediaCasterMap.getMediaCaster(streamName);
				//mediaCasterStream.getMediaCaster().setReconnectWaitTime(3000);
				mediaCasterStream.getMediaCaster().setStreamTimeout(50000);

				if (success)
					Utilities.log("ModuleMediaCasterStreamManager", "startMediaCasterStream: Stream started: "+streamName);
				else
					Utilities.log("ModuleMediaCasterStreamManager", "startMediaCasterStream: Stream start failed: "+streamName);
				break;
			}
		}
		catch (Exception e)
		{
			Utilities.log("ModuleMediaCasterStreamManager", "startMediaCasterStream: "+e.toString());
		}

		return success;
	}

	public static void  stopMediaCasterStream(IApplicationInstance appInstance,String streamName)
	{
		try
		{
			while(true)
			{
				MediaCasterStreamMap mediaCasterMap = appInstance.getMediaCasterStreams();
				MediaCasterStreamManager mediaCasterStreamManager = mediaCasterMap.getStreamManager();

				boolean success = mediaCasterStreamManager.stopStream(streamName);
				if (success)
				{
					Utilities.log("ModuleStreamStarter", "stopMediaCasterStream: Stream stopped: "+streamName);
				}
				else
				{
					Utilities.log("ModuleMediaCasterStreamManager", "stopMediaCasterStream: Stream not found: "+streamName);
				}
				break;
			}
		}
		catch (Exception e)
		{
			Utilities.log("ModuleMediaCasterStreamManager", "startMediaCasterStream: "+e.toString());
		}
	}

	public static String getPublishingUrl(String UserId){
		String publishing_url = "rtmp://"+Utilities.getServerIP() +":" + Utilities.getServerPort() + "/live2/" +UserId ;		
		return publishing_url;
	}
	
}
