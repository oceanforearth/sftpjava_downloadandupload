package com.btsl.process;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.btsl.util.common.Details;
import com.btsl.util.common.PropertiesCache;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
//import com.btsl.process.SftpUploadtoServer;
//import com.jcraft.jsch.*;

public class SDRloggerSftp{
	public final static Logger loger = Logger.getLogger(SDRloggerSftp.class);
	//public static final String DATE_FORMAT = "yyyyMMddHHmmssss";
	
	public SDRloggerSftp() throws IOException{
		
	}
	
	
	public boolean startFTP(String userName, String ip, String passwd, String fileModTime, String fileType, String applicationType) throws IOException{	
		loger.info("startFTP() : Enter, userName="+userName+", ip="+ip+", fileModTime="+fileModTime+", fileType="+fileType+", applicationType="+applicationType);//print param
		
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
		String increaseFilePlaceholder  = "csv";
	   try{ 
	    	
	    	if(Details.isNullString(ip) || Details.isNullString(userName) || Details.isNullString(passwd) || Details.isNullString(fileModTime)){
	    		loger.info("startFTP() : ip, userName, passwd, lastModified should not blank.");//print param
	    		flagcheck = false;
	    		return flagcheck;
	    	}
	    	
		    JSch jsch = new JSch();
		    
		    int    SFTPPORT = 22;
	    	host = ip.trim();
	    	user = userName.trim();
	    	password = passwd.trim();
		    lastModified = fileModTime.trim();
		    
	//	    try {
	//			lastModfileTime = Long.parseLong(lastModified);
	//		} 
	//	    catch (Exception e1) {
	//			// TODO Auto-generated catch block
	//			e1.printStackTrace();
	//			lastModfileTime = 15*60;//15 Minutes
	//		}
		    
	
			
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
            }
			appBackupDir =  appSourceDir + "/" +   appBackupDir;
            
		    if(Details.isNullString(root) || Details.isNullString(downloadDirectory) || Details.isNullString(errorDirectory) 
		    		|| Details.isNullString(appSourceDir) || Details.isNullString(appBackupDir)){
		    	loger.info("startFTP() : root or downloadDirectory or errorDirectory or appSourceDir or appBackupDir should not blank.");//print param
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
			
		//	if (channel.isConnected()){
		//		loger.info("channel.isConnected() "+ ip + " successfully");
		//	} 
		//	else {
		//		loger.info("channel.isConnected() "+ ip + " Failed");
		//		
		//	}
				
			channelSftp = (ChannelSftp)channel;
			
			
			try {
			channelSftp.cd(appSourceDir);
			}
			catch(Exception e){
		         System.out.println("channelSftp.cd(appSourceDir) error="+e.getMessage());
			   		}
			
			
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

            	loger.debug("startFTP() :Invalid Service Type is Passed as the argument:"+fileType);
            	flagcheck = false;
	    		return flagcheck;
            
			}
		*/	
				
			downloadPattern = "*"+vasChck+"*";
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
			
						            
            if(list.isEmpty()) {
            	loger.debug("startFTP() :No files are present in the SFTP Server Source Directory");
            	flagcheck = false;
	    		return flagcheck;
            }

            for (ChannelSftp.LsEntry oListItem : list) {
            	loger.debug("startFTP() : loop start, fileName=" + oListItem.getFilename()+", Started at="+new Date());
	            try {
					//if(oListItem.getAttrs().getSize() > 0) {
						if(true) {	
						time = Details.convertUtilDateToString(new java.util.Date(), dateFormat).substring(0, 10);
				        seperator = "\\"+splitParam.trim();
				        fileTimearr  = oListItem.getFilename().split(seperator);
				        //if((!Details.isNullString(applicationType)) && applicationType.trim().equals("ENT")){
				        //	fileTime =fileTimearr[4].substring(0, 10);
				       // }else{
				     //   	fileTime =fileTimearr[3].substring(0, 10);
				     //   }
				     //   loger.debug("The time of a file is "+fileTime+" and current time is"+time);
				        
						/*if(fileType.equals("VAS")){
							
							fileTimearr = oListItem.getFilename().split("_");
							fileTime = oListItem.getFilename().substring(20, 30);
							
						}
						else{
							
							fileTime = oListItem.getFilename().substring(17, 27);
							 
						}*/
						
						//if(!(time.equals(fileTime))){
							if(true){
						//currentDateMs = new Date().getTime();
						//timeSec = (int)(currentDateMs / 1000);
						//diff = timeSec - oListItem.getAttrs().getMTime();
						//loger.debug("startFTP() : Time difference between the currentTime"+timeSec+" and the file modifiedTime"+oListItem.getAttrs().getMTime()+" diff="+diff+", lastModfileTime="+lastModfileTime);
						 
//						 if (diff > lastModfileTime){
//							 loger.debug("startFTP() : Modified Time of the file is greater than or equal to the configured time");
//						 }
//						 else {
//							 loger.debug("startFTP() : Modified Time of the file is less than the configured time");
//							 continue;
//						 }
						
					//	 if(oListItem.getFilename().endsWith(".cdr") && oListItem.getFilename().contains("SDRSE00") ) {
							 if(true ) {	 
							  try{
								  
								   OutputStream outStream = null;
								   try{
										outStream = new FileOutputStream(downloadPath + "//" + oListItem.getFilename());
										channelSftp.get(oListItem.getFilename().toString(), downloadPath);
										loger.debug("startFTP() : fileName=" + oListItem.getFilename()+" had been downloaded to local server:"+ downloadPath);
										flagcheck = true;
								   }
								   catch(Exception e){
										loger.error("startFTP() : Exception while SFTP download of file, Error="+e.getMessage());
										flagcheck = false;
							    		
								   }
								   finally{
										try {
											if (outStream != null){
												outStream.close();
												outStream = null;
											}
										} 
										catch (Exception e) {
											loger.error("startFTP() : Exception in closing outputStream Error="+e.getMessage());
											flagcheck = false;
											e.printStackTrace();
										}
										
						           }
								   if(flagcheck){
								  	   
								 // channelSftp.rename(oListItem.getFilename(), oListItem.getFilename()+".done");	
								   
									// if download file successfully , backup this file first, then rm this file 	   
							       String outpath = appBackupDir + "//" + oListItem.getFilename();
							  //     System.out.println("outpath :"+outpath);
							       channelSftp.rename(oListItem.getFilename(), outpath);
							     //  System.out.println("outpath 0007 :"+oListItem.getFilename());
							       
							   	   // if download file successfully , it should rm file from download server 
							       //channelSftp.rm(oListItem.getFilename());
								  
								   }
							       //==============Renaming ==================//
								   /*renSftp = new LoggerSftpRename();
								   renSftp.sftpFileRename(user, host, password);
								*/
								  }
								catch(Exception e){
									loger.debug("startFTP() :Exception is="+e.getMessage());
									flagcheck = false;
			 					}
						}
							 // forever do not run into below part
					    else {
							  File remoteFile = new File(oListItem.getFilename());
							  OutputStream output = null;
							  
					          try {
			 			            if(remoteFile.getName().endsWith(".cdr") && !remoteFile.getName().contains("SDRSE00")) {	
										output = new FileOutputStream(errorPath + "//" + oListItem.getFilename());
										channelSftp.get(oListItem.getFilename(), output);
										channelSftp.rename(oListItem.getFilename(), oListItem.getFilename()+".err");
						                loger.debug("startFTP() : Invalid File Naming Convention "+oListItem.getFilename());
						            }
						            
					          }
					          catch (Exception e){
					        	loger.debug("startFTP() : Exception Error ="+e.getMessage());
					        	flagcheck = false;
					          }
					          finally{
					        	try {
									if (output != null){
										output.close();
										output = null;
									}
								} 
			 		        	catch (Exception e) {
									// TODO Auto-generated catch block
									loger.error("startFTP() : Exception in closing output, Error="+e.getMessage());
									flagcheck = false;
									e.printStackTrace();
								}
					         }
					    }
						 
					}
	            }
					else{
						loger.debug("startFTP() : Wait for the data to get written inside the file.");
					}
				} 
	            catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					flagcheck = false;
				}
            }

 	}

		catch (Exception ex){
			loger.error("startFTP() : Exception in main function, Error="+ex.getMessage()+"<<>>classname="+ ex.getClass().getName());
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
				loger.debug("startFTP() :Enter Exception is"+e.getMessage());
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
		
		
    	SDRloggerSftp getMyFiles =null;
    	
		String user = null;
		String ip = null;
		String passwd = null;
		String filelmodTime = null;
		String fileType = null;
		String applicationType = null;
		try {
			getMyFiles = new SDRloggerSftp();
			
		
	/*	
			user = "sftp";
			ip = "10.255.7.131";
			passwd = "66776666";
			//input backup directory 
			filelmodTime = "/home/88988";
	
			user = "sys";
			ip = "10.255.7.134";
			passwd = "8877664433";
			//input backup directory 
			filelmodTime = "/home/44555";
			// invoke which action
			fileType = "download";
			//reverse
			applicationType = "txt";
			
			if (fileType.toLowerCase().trim().equals("download")){
				getMyFiles.startFTP(user, ip, passwd, filelmodTime, fileType, applicationType);
			} else if (fileType.toLowerCase().trim().equals("upload")) {
			//	SftpUploadtoServer uploadMyFile =null;
				SftpUploadtoServer uploadMyFile = new SftpUploadtoServer();
				uploadMyFile.startFTP(user, ip, passwd, filelmodTime, fileType, applicationType);				
			} 
			else {
				getMyFiles.startFTP(user, ip, passwd, filelmodTime, fileType, applicationType);
			}
			
*/
			
			if(args.length >= 4){
				user = args[0];
				ip = args[1];
				passwd = args[2];
				filelmodTime = args[3];  //input backup directory 
				try{
					fileType = args[4];  // invoke which action
				 }catch (Exception e) {
					 fileType ="download";  //
				}
				try{
					applicationType = args[5]; 
				 }catch (Exception e) {
					 applicationType ="RET";
				}
					//getMyFiles.startFTP(user, ip, passwd, filelmodTime, fileType, applicationType);
					if (fileType.toLowerCase().trim().equals("download")){
						getMyFiles.startFTP(user, ip, passwd, filelmodTime, fileType, applicationType);
					} else if (fileType.toLowerCase().trim().equals("upload")) {
					//	SftpUploadtoServer uploadMyFile =null;
						SftpUploadtoServer uploadMyFile = new SftpUploadtoServer();
						uploadMyFile.startFTP(user, ip, passwd, filelmodTime, fileType, applicationType);				
					} 
					else {
						getMyFiles.startFTP(user, ip, passwd, filelmodTime, fileType, applicationType);
					}
			}
			else{
				loger.debug("main :Enter no asrguments are passed to the main class");
				return;
			} 
	
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			loger.error("main() :Enter Exception is"+e.getMessage());
			loger.error("main :Mandatory asrguments are not passed to the main class");
			e.printStackTrace();
		  }	   
		}
	}