package com.btsl.process;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.btsl.util.common.Details;
import com.btsl.util.common.PropertiesCache;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
//import com.jcraft.jsch.*;

public class SftpUploadtoServer{
	public final static Logger loger = Logger.getLogger(SftpUploadtoServer.class);
	//public static final String DATE_FORMAT = "yyyyMMddHHmmssss";
	
	public SftpUploadtoServer() throws IOException{
		
	}
	
	
	public boolean startFTP(String userName, String ip, String passwd, String fileModTime, String fileType, String applicationType) throws IOException{	
		loger.info("startFTPUpload() : Enter, userName="+userName+", ip="+ip+", fileModTime="+fileModTime+", fileType="+fileType+", applicationType="+applicationType);//print param
		
		int timeSec = 0;
		long diff = 0l;
		long currentDateMs = 0l;
		long lastModfileTime = 0l;
		boolean flagcheck = true;
		Session     session     = null;
		Channel     channel     = null;
		ChannelSftp channelSftp = null;
		
	    String host = null;
		String user = null;
		String password = null;
		String root = null;
		String downloadDirectory = null;
		String downloadPath = null;
		String appSourceDir = null;
		String lastModified = null;
		String errorDirectory = null;
		String errorPath = null;
		String appBackupDir = null;
		String downloadPattern = null;
		String fileTime = null;
		String time = null;
		String vasChck = null;
		String digChck = null;
		String dateFormat = null;
		String splitParam = null;
		String seperator = null;
		String[] fileTimearr = null;
	   try{ 
	    	
	    	if(Details.isNullString(ip) || Details.isNullString(userName) || Details.isNullString(passwd) || Details.isNullString(fileModTime)){
	    		loger.info("startFTPUpload() : ip, userName, passwd, lastModified should not blank.");//print param
	    		flagcheck = false;
	    		return flagcheck;
	    	}
	    	
		    JSch jsch = new JSch();
		    
		    int    SFTPPORT = 22;
	    	host = ip.trim();
	    	user = userName.trim();
	    	password = passwd.trim();
		    lastModified = fileModTime.trim();
		    
		//    try {
		//		lastModfileTime = Long.parseLong(lastModified);
		//	} 
		//    catch (Exception e1) {
		//		// TODO Auto-generated catch block
		//		e1.printStackTrace();
		//		lastModfileTime = 15*60;//15 Minutes
		//	}
		    
	
			
			if(!Details.isNullString(PropertiesCache.getInstance().getProperty("homeDir"))){
				root = PropertiesCache.getInstance().getProperty("homeDir").trim();
			}
			/*	if((!Details.isNullString(applicationType)) && applicationType.trim().equals("ENT")){
			loger.debug("startFTP() :Fetching the property file values for Enterprise:"+applicationType);
			if(!Details.isNullString(PropertiesCache.getInstance().getProperty("downloadDirectoryENT"))){
				downloadDirectory = PropertiesCache.getInstance().getProperty("downloadDirectoryENT").trim();
				downloadPath = root+"/"+downloadDirectory;
			}
			if(!Details.isNullString(PropertiesCache.getInstance().getProperty("appSourceDirENT"))){
		    	appSourceDir = PropertiesCache.getInstance().getProperty("appSourceDirENT").trim();
            }
		}else{
			if(!Details.isNullString(PropertiesCache.getInstance().getProperty("downloadDirectory"))){
				downloadDirectory = PropertiesCache.getInstance().getProperty("downloadDirectory").trim();
				downloadPath = root+"/"+downloadDirectory;
			}
			if(!Details.isNullString(PropertiesCache.getInstance().getProperty("appSourceDir"))){
		    	appSourceDir = PropertiesCache.getInstance().getProperty("appSourceDir").trim();
            }
		
			if(!Details.isNullString(PropertiesCache.getInstance().getProperty("amlserverholder"))){
		    	appSourceDir = PropertiesCache.getInstance().getProperty("amlserverholder").trim();
            }	
		}
		*/
		if(!Details.isNullString(PropertiesCache.getInstance().getProperty("downloadDirectory"))){
			downloadDirectory = PropertiesCache.getInstance().getProperty("downloadDirectory").trim();
			downloadPath = root+"/"+downloadDirectory;
		}
		//if(!Details.isNullString(PropertiesCache.getInstance().getProperty("amlserverholder"))){
	  //  	appSourceDir = PropertiesCache.getInstance().getProperty("amlserverholder").trim();
     //   }
		appSourceDir = lastModified; 
		
		if(!Details.isNullString(PropertiesCache.getInstance().getProperty("errorDirectory"))){
			errorDirectory = PropertiesCache.getInstance().getProperty("errorDirectory").trim();
			errorPath = root+"/"+errorDirectory;
		}
	    if(!Details.isNullString(PropertiesCache.getInstance().getProperty("appBackupDir"))){
	    	appBackupDir= PropertiesCache.getInstance().getProperty("appBackupDir").trim();
	    	appBackupDir =   downloadPath +"/"+ appBackupDir;
        }
	    
			
			if(!Details.isNullString(PropertiesCache.getInstance().getProperty("errorDirectory"))){
				errorDirectory = PropertiesCache.getInstance().getProperty("errorDirectory").trim();
				errorPath = root+"/"+errorDirectory;
			}

		    if(Details.isNullString(root) || Details.isNullString(downloadDirectory) || Details.isNullString(errorDirectory) 
		    		|| Details.isNullString(appSourceDir) || Details.isNullString(appBackupDir)){
		    	loger.info("startFTPUpload() : root or downloadDirectory or errorDirectory or appSourceDir or appBackupDir should not blank.");//print param
	    		flagcheck = false;
	    		return flagcheck;
		    }
		    if(!Details.isNullString(PropertiesCache.getInstance().getProperty("dateFormat"))){
		    	dateFormat= PropertiesCache.getInstance().getProperty("dateFormat").trim();
            }
		    
			session = jsch.getSession(user, host, SFTPPORT);
			session.setPassword(password);
		    
			java.util.Properties config = new java.util.Properties();
			
			config.put("StrictHostKeyChecking", "no");
			//config.put("kex", "curve25519-sha256@libssh.org");
			//config.put("kex", "diffie-hellman-group1-sha1,diffie-hellman-group14-sha1,diffie-hellman-group-exchange-sha1,diffie-hellman-group-exchange-sha256");
		
			session.setConfig(config);
			
			try{
				session.connect();
				if (session.isConnected()) {
					loger.info("connect to server "+ ip + " successfully");
					}
				else {
					loger.info("connect to server "+ ip + " Failed");
				}
				}
		   catch(Exception e){
			    System.out.println("session.connect() error="+e.getMessage());
		   		}
			//session.connect();
			
			channel = session.openChannel("sftp");
			
			channel.connect();
			
			channelSftp = (ChannelSftp)channel;
			
			try {
			channelSftp.cd(appSourceDir);
			}
			catch(Exception e){
		         System.out.println("channelSftp.cd(appSourceDir) error="+e.getMessage());
			   		}
			
			//System.out.println("channel.connect();004 " + appSourceDir);	
	       // filelist = channelSftp.ls(appSourceDir);
			String increaseFilePlaceholder  = "csv";
			if(!Details.isNullString(PropertiesCache.getInstance().getProperty("vasFilePlaceholder"))){
			vasChck = PropertiesCache.getInstance().getProperty("vasFilePlaceholder").trim();
			}
			if(!Details.isNullString(PropertiesCache.getInstance().getProperty("digFilePlaceholder"))){
				digChck = PropertiesCache.getInstance().getProperty("digFilePlaceholder").trim();
				}
			if(!Details.isNullString(PropertiesCache.getInstance().getProperty("increaseFilePlaceholder"))){
				 increaseFilePlaceholder = PropertiesCache.getInstance().getProperty("increaseFilePlaceholder").trim();
				}
			if(!Details.isNullString(PropertiesCache.getInstance().getProperty("recordDelim"))){
				splitParam = PropertiesCache.getInstance().getProperty("recordDelim").trim();
				}
			else{
				splitParam ="_";
			}
		
			//System.out.println(downloadPattern);
		/*	if(fileType.equals("VAS")){
				
				downloadPattern = "*"+vasChck+"*";
			}
			else if (fileType.equals("DIG")){
				
				downloadPattern = "*"+digChck+"*";
			}
			else {

            	loger.debug("startFTPUpload() :Invalid Service Type is Passed as the argument:"+fileType);
            	flagcheck = false;
	    		return flagcheck;
            
			}
		*/	
	/*		downloadPattern = "*"+vasChck+"*";
		//	downloadPattern = "*"+vasChck+"*" + " *"+digChck+"*" + " *"+increaseFilePlaceholder+"*";
			//xml
			Vector<ChannelSftp.LsEntry> list = channelSftp.ls(downloadPattern);
			//flg
			downloadPattern = "*"+digChck+"*";
			 list.addAll(channelSftp.ls(downloadPattern));
			// list.addAll(arg0)
			//csv
			downloadPattern = "*"+increaseFilePlaceholder+"*";
			list.addAll(channelSftp.ls(downloadPattern));
		*/
			
			File localfiles = new File( downloadPath );
						            
     
            	 InputStream is = null;
            	  if (localfiles.isDirectory()) {
            	        // 获取路径下的所有文件
            	        File[] files = localfiles.listFiles();
            	        
                       	if(files.length == 0 ) {
                        	loger.debug("startFTPUpload() :No files are present in the Local Server Source Directory");
                        	flagcheck = false;
            	    		return flagcheck;
                        }     
            	        
            	        for (int i = 0; i < files.length; i++) {
            	        	loger.debug("startFTPUpload() : loop start, fileName=" + files[i].getName()+", Started at="+new Date());
            	           // System.out.println("文件：" + files[i].getName()); // files[i].getPath());
            	     
            	        	
            	            try {
            	                 //File file_temp = new File(files[i].getName());  
            	                 File file_temp = new File(downloadPath + "//" +files[i].getName());  
            	                 //System.out.println("file_temp.isFile() " + file_temp);
            	                 //System.out.println("file_temp.isFile() " + file_temp.isFile());
            	                 if (!file_temp.isFile()) 
            	                 {           	                 
            	                 continue;
            	                 }
            	                // is = new FileInputStream(downloadPath + "//" + file_temp);  
            	                 is = new FileInputStream(file_temp);  
            	                channelSftp.put(is, files[i].getName(),ChannelSftp.OVERWRITE);  //上传文件
            	                loger.debug("startFTPUpload() : fileName=" + files[i].getName()+" had been uploaded to "+ ip +  " successfully at="+new Date());
            	                flagcheck = true;
            	            }
            	            catch(Exception e){
								loger.error("startFTPUpload() : Exception while SFTP upload of file, Error="+e.getMessage());
								flagcheck = false;
					    		
						      }          	           
            	            finally{								
									if (is != null){
										is.close();
										is = null;
									}
								 }
            	        if(flagcheck){						 
            	        	File file_temp2 = new File(downloadPath + "//" + files[i].getName()); 
            	        	//appBackupDir
            	        	
            	        	//System.out.println("dir:"+appBackupDir + "//" + files[i].getName());
            	        	file_temp2.renameTo(new File(appBackupDir + "//" + files[i].getName()));
		  
					   }
            	            
            	        }
            	        }
            
        	
           // for (ChannelSftp.LsEntry oListItem : list) {}

 	}

		catch (Exception ex){
			loger.error("startFTPUpload() : Exception in main function, Error="+ex.getMessage()+"<<>>classname="+ ex.getClass().getName());
			flagcheck = false;
		}
		finally{
			try {
				if (channel != null) {
					channel.disconnect();
					channel = null;
				}
				if (channelSftp !=null){
					channelSftp.exit();
					channelSftp = null;
				}
				if (session !=null){
					session.disconnect();
					session = null;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				loger.debug("startFTPUpload() :Enter Exception is"+e.getMessage());
				e.printStackTrace();
				flagcheck = false;
			}
        	 
		}	
		return flagcheck;
	}
	
	
    public static void main(String[] args) throws IOException {  
	    System.setProperty("java.classpath","./lib;%JAVA_HOME%/lib;.");
	    String path_progerty = System.getProperty("java.classpath");
		System.out.println("java.classpath :"+path_progerty);
		System.out.println("java.user.dir :"+System.getProperty("user.dir"));
		
		
    	SftpUploadtoServer getMyFiles =null;
		String user = null;
		String ip = null;
		String passwd = null;
		String filelmodTime = null;
		String fileType = null;
		String applicationType = null;
		try {
			getMyFiles = new SftpUploadtoServer();
			user = "sys";
			ip = "10.255.7.134";
			passwd = "wwwsss";
			filelmodTime = "/home/9988";
			fileType = "upload";
			applicationType = "txt";
			getMyFiles.startFTP(user, ip, passwd, filelmodTime, fileType, applicationType);
		/*	if(args.length >= 4){
				user = args[0];
				ip = args[1];
				passwd = args[2];
				filelmodTime = args[3];
				try{
					fileType = args[4]; 
				 }catch (Exception e) {
					 fileType ="DIG";
				}
				try{
					applicationType = args[5]; 
				 }catch (Exception e) {
					 applicationType ="RET";
				}
				getMyFiles.startFTP(user, ip, passwd, filelmodTime, fileType, applicationType);
			}
			else{
				loger.debug("main :Enter no asrguments are passed to the main class");
				return;
			}
		*/
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			loger.error("main() :Enter Exception is"+e.getMessage());
			loger.error("main :Mandatory asrguments are not passed to the main class");
			e.printStackTrace();
		  }	   
		}
	}