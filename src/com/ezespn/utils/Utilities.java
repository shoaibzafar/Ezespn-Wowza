package com.ezespn.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.wowza.wms.application.IApplication;
import com.wowza.wms.application.IApplicationInstance;
import com.wowza.wms.logging.WMSLogger;
import com.wowza.wms.logging.WMSLoggerFactory;
import com.wowza.wms.server.IServer;
import com.wowza.wms.server.Server;
import com.wowza.wms.stream.MediaStreamMap;
import com.wowza.wms.vhost.IVHost;
import com.wowza.wms.vhost.VHostItem;
import com.wowza.wms.vhost.VHostSingleton;

public class Utilities {

	public static String APPLICATION_NAME = Settings.getStringProperty(
			"app.name", "live2");
	public static String APPLICATION_INSTANCE = Settings.getStringProperty(
			"app.instance", "_definst_");
	public static String APPLICATION_MODULE_INSTANCE = Settings
			.getStringProperty("app.module", "WMain");
	public static String VOD_APPLICATION_NAME = Settings.getStringProperty(
			"app.vod", "billiardnet.tv.Vod");

	private static WMSLogger log = WMSLoggerFactory.getLogger(Utilities.class);

	public static IVHost getVhost() {
		IServer server = Server.getInstance();
		IVHost vhost = null;
		for (VHostItem vhostItem : server.getVHostList().getVHostItems()) {
			vhost = VHostSingleton.getInstance(vhostItem.getName());
			log.info("Vhost: " + vhost.getName());
			for (Object apps : vhost.getApplicationNames()) {
				log.info("App: " + apps);
			}
		}
		return vhost;
	}

	public static IApplicationInstance getApplicationInstance(IVHost vhost) {
		IApplication app = vhost.getApplication(APPLICATION_NAME);
		if (app != null) {
			IApplicationInstance appInstance = app
					.getAppInstance(APPLICATION_INSTANCE);
			return appInstance;
		}

		return null;
	}

	public static String getApplicationPath(IApplicationInstance appInstance) {

		String strPath = appInstance.getApplication().getApplicationPath();
		return strPath;
	}

	public static void log(String tag, String message) {
		String prefix = "";
		if (tag != null)
			prefix = "[" + tag + "]";
		WMSLoggerFactory.getLogger(Utilities.class).info(prefix + message);
	}

	public static String getServerIP() {
		return Settings.getStringProperty("app.host");
	}

	public static String getServerPort() {
		return Settings.getStringProperty("app.port");
	}

	@SuppressWarnings("unchecked")
	public static JSONObject getActiveStreamList(){

		JSONObject response = new JSONObject();
		JSONArray  activeStreamjArray = new JSONArray();
		IApplicationInstance appInstance = Utilities.getApplicationInstance(Utilities.getVhost());
		List<String> streamList = appInstance.getPublishStreamNames();

		MediaStreamMap publishedStreams =    appInstance.getStreams();

		for(int index = 0; index < streamList.size()-1;index++){
			JSONObject obj = new JSONObject();
			obj.put("stream_name", publishedStreams.getStream(streamList.get(index)).getName());
			obj.put("ip_address", publishedStreams.getStream(streamList.get(index)).getClient().getIp());
			activeStreamjArray.add(obj);
		}

		response.put("livestreams", activeStreamjArray);

		return response;
	}

	public static String getPassword(){

		String uuid = UUID.randomUUID().toString();
		Utilities.log("uuid = " , uuid);
		return uuid;
	}   

	@SuppressWarnings("unchecked")
	public static JSONObject addPublisher(String uname , String pwd ,JSONObject response){

		boolean result = false;
		try {
			String existingPublisher = readFile();
			String isExistCheck = uname;
			if(!existingPublisher.contains(isExistCheck)){
				Utilities.log("billiardtv.net",existingPublisher);
				existingPublisher = existingPublisher + uname + " " + pwd ;	
				response.put("status", 1);
				response.put("publishing_url",MediaUtils.getPublishingUrl(uname));
				response.put("username", uname);
				response.put("password", pwd);
				response.put("playing_url", MediaUtils.getHLS(uname, true));
				IApplicationInstance appInstance = getApplicationInstance(getVhost());
				String fileName = appInstance.getStreamStorageDir();
				generateLiveStreamSmilFileSD(uname, uname);
				generateLiveStreamSmilFileHD(uname, uname);
				response.put("SD_url", MediaUtils.getLivePlayBackSmilLink(uname+"_SD"));
				response.put("HD_url", MediaUtils.getLivePlayBackSmilLink(uname+"_HD"));
				
			}
			else{
				Utilities.log("billiardtv.net","Publisher exists already");
				response.put("status", 0);
				response.put("message","Publisher exists already");
			}
			Utilities.log("Publisher List", existingPublisher);
			result = savePublisherToFile(existingPublisher);
			String publisher = readFile();
			result = savePublisherToFile(publisher);

		} catch (IOException e) {

			e.printStackTrace();
		}

		return response;
	}

	private static boolean savePublisherToFile(String pubisher) {

		boolean result = false;
		//String filePath = "/usr/local/WowzaStreamingEngine-4.5.0/conf/live/publish.password";
		String filePath = "/usr/local/WowzaStreamingEngine-4.4.1/conf/live2/publish.password";

		try {
			File file = new File(filePath);
			if (file.exists())
				file.delete();
			file.createNewFile();
			byte[] contentInBytes = pubisher.getBytes();
			FileOutputStream fop = new FileOutputStream(file);
			fop.write(contentInBytes);
			fop.flush();
			fop.close();
			Utilities.log("File Creation Status :","Created");
			result = true;

		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String readFile() throws IOException {

		String fileName = "/usr/local/WowzaStreamingEngine-4.4.1/conf/live2/publish.password";
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		try {
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {	
				sb.append(line);
				sb.append("\n");
				line = br.readLine();

			}
			return sb.toString();
		} finally {
			br.close();
		}
	}

	public static boolean removePublisher(String uname , String password){

		boolean result = false;
		String publisherToremove = uname + " " + password;
		Utilities.log("Publisher to be removed", publisherToremove);
		try {
			String existingPublisher = readFile();
			Utilities.log("billiardtv.net", "1");
			if(existingPublisher.contains(publisherToremove)){
				Utilities.log("billiardtv.net", "2");
				String[] parts = existingPublisher.split(publisherToremove);
				String s1  = parts[0];
				String s2  = parts[1];
				Utilities.log("billiardtv.net", "3");
				String s3 = "";
				if(parts[1]!=null){
					Utilities.log("billiardtv.net", "4");
					savePublisherToFile(s1.trim());
					s1 = s1+s2.trim();
					result = savePublisherToFile(s1);
				}
				else{
					s3 = parts[0];
					result = savePublisherToFile(s3.trim());
				}
				Utilities.log("billiardtv.net", "Publisher removed");

			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	public static void generateVodSmilFile(String fileName,String streamName1, String StreamName2 ,String StreamName3){

		IApplicationInstance appInstance = getApplicationInstance(getVhost());
		String pathtoSmilFile = appInstance.getStreamStorageDir() + "/" + fileName + ".smil";

		File file = new File(pathtoSmilFile);
		if(file.exists())
		{
			file.delete();
		}

		try {
			String content ="<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"+
					"<smil\n title=\"\">\n"+
					"<body>\n"+
					"<switch>\n"+
					"<video height=\"180\" src=\""+fileName+"/"+StreamName3+"\" width=\"320\">\n"+
					"<param name=\"videoBitrate\" value=\"200000\" valuetype=\"data\"></param>\n"+
					"</video>\n"+
					"<video height=\"360\" src=\""+fileName+"/"+StreamName2+"\" width=\"640\">\n"+
					"<param name=\"videoBitrate\" value=\"520000\" valuetype=\"data\"></param>\n"+
					"</video>\n"+
					"<video height=\"270\" src=\""+fileName+"/"+streamName1+"\" width=\"420\">\n"+
					"<param name=\"videoBitrate\" value=\"570000\" valuetype=\"data\"></param>\n"+
					"</video>\n"+
					"</switch>\n"+
					"</body>\n"+
					"</smil>\n";


			/*if(!file.exists())
		    	    	{
		    	    	   file.createNewFile();
		    	    	}*/
			file.createNewFile();
			FileWriter fw = new FileWriter(file,true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.close();
			Utilities.log("billiardtv.net","Smil file created successfully");

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static boolean getChannelStatus(String Channel_name){

		boolean status = false;
		IApplicationInstance appInstance = getApplicationInstance(getVhost());
		List<String> publishedStreams = appInstance.getPublishStreamNames();
		for(int i = 0; i<publishedStreams.size();i++){
			if(publishedStreams.get(i).equals(Channel_name)){
				status = true;
			}
		}
		
		return status;
	}


	public static List<String> latesRecordedFileNamesList(String filePath){

		List<String> list = new ArrayList<String>();
		File fl = new File(filePath);
		File[] files = fl.listFiles();
		long lastMod = Long.MIN_VALUE;
		File choice = null;
		for(int i = 0 ; i < 4; i++){
		for (File file : files) {
				if (file.lastModified() > lastMod && !file.getName().contains(".tmp")) {
					choice = file;
					lastMod = file.lastModified();
					list.add(i, choice.getName());
					Utilities.log("billiardtv ContentDir ", choice.getName());
				}	
			}
			
		}
		
		return list;
	}
	
	public static String latesThumbnailFileNames(String filePath,String ImgName){

		String thumbnailsName = "";
		File fl = new File(filePath);
		File[] files = fl.listFiles();
		long lastMod = Long.MIN_VALUE;
		File choice = null;
		
		for (File file : files) {
				if (file.lastModified() > lastMod && !file.getName().contains(".tmp") && file.getName().contains("img")&& file.getName().contains(ImgName)) {
					choice = file;
					lastMod = file.lastModified();
					//list.add(i, choice.getName());
					thumbnailsName = file.getName();
					Utilities.log("billiardtv ContentDir Thumbnails name", choice.getName());
				}	
				//Utilities.log("billiardtv ContentDir Thumbnails name Outside If", choice.getName());
				
			
		}
		
		return thumbnailsName;
	}
	
	public static void generateLiveStreamSmilFileSD(String fileName,String streamName1){

		IApplicationInstance appInstance = getApplicationInstance(getVhost());
		String pathtoSmilFile = appInstance.getStreamStorageDir() + "/" + fileName+"_SD" + ".smil";

		File file = new File(pathtoSmilFile);
		if(file.exists())
		{
			file.delete();
		}

		try {
			String content ="<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"+
					"<smil\n title=\"\">\n"+
					"<body>\n"+
					"<switch>\n"+
					"<video height=\"180\" src=\""+streamName1+"_160p"+"\" width=\"320\">\n"+
					"<param name=\"videoBitrate\" value=\"200000\" valuetype=\"data\"></param>\n"+
					"</video>\n"+
					"<video height=\"360\" src=\""+streamName1+ "_240p" +"\" width=\"640\">\n"+
					"<param name=\"videoBitrate\" value=\"520000\" valuetype=\"data\"></param>\n"+
					"</video>\n"+
					"</switch>\n"+
					"</body>\n"+
					"</smil>\n";


			/*if(!file.exists())
		    	    	{
		    	    	   file.createNewFile();
		    	    	}*/
			file.createNewFile();
			FileWriter fw = new FileWriter(file,true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.close();
			Utilities.log("billiardtv.net","Smil file created successfully");

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public static void generateLiveStreamSmilFileHD(String fileName,String streamName1){

		IApplicationInstance appInstance = getApplicationInstance(getVhost());
		String pathtoSmilFile = appInstance.getStreamStorageDir() + "/" + fileName+"_HD" + ".smil";

		File file = new File(pathtoSmilFile);
		if(file.exists())
		{
			file.delete();
		}

		try {
			String content ="<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"+
					"<smil\n title=\"\">\n"+
					"<body>\n"+
					"<switch>\n"+
					"<video height=\"360\" src=\""+streamName1+"_360p"+"\" width=\"640\">\n"+
					"<param name=\"videoBitrate\" value=\"200000\" valuetype=\"data\"></param>\n"+
					"</video>\n"+
					"<video height=\"480\" src=\""+streamName1+ "_480p" +"\" width=\"855\">\n"+
					"<param name=\"videoBitrate\" value=\"520000\" valuetype=\"data\"></param>\n"+
					"</video>\n"+
					"<video height=\"720\" src=\""+streamName1+ "_720p" +"\" width=\"1280\">\n"+
					"<param name=\"videoBitrate\" value=\"570000\" valuetype=\"data\"></param>\n"+
					"</video>\n"+
					"</switch>\n"+
					"</body>\n"+
					"</smil>\n";


			/*if(!file.exists())
		    	    	{
		    	    	   file.createNewFile();
		    	    	}*/
			file.createNewFile();
			FileWriter fw = new FileWriter(file,true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.close();
			Utilities.log("billiardtv.net","Smil file created successfully");

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public static boolean isHomeChannelLive(String streamName){
		
		boolean result = false;
		IApplicationInstance _appInstannce = getApplicationInstance("tvmike", getVhost());
		List<String> publishedStream = _appInstannce.getPublishStreamNames();
		
		for(int i = 0; i<publishedStream.size();i++){
			if(publishedStream.get(i).equals(streamName)){
				result = true;
			}
		}
		return result;
	}
	
	public static IApplicationInstance getApplicationInstance(String AppName , IVHost vhost) {

		IApplication app = vhost.getApplication(AppName);

		if (app != null) {
			IApplicationInstance appInstance = app
					.getAppInstance(APPLICATION_INSTANCE);
			Utilities.log("AppLication Name From Function 2",app.getName());

			return appInstance;
		}

		return null;
	}
	
	
	
}

