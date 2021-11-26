# SftpJava

This application supports two functions:
1 download files from the server (only get files with three suffixes: CSV flg XML) to the local server
2 upload files from local to server
Command to download  files (files with three suffixes: CSV flg XML) from the server
java -jar  sftp_ AML_ cron.jar sftpaccount 10.255.7.131 aaaa /home/test download
Command to upload files to the server
java -jar  sftp_ AML_ cron.jar sftpaccount 10.255.7.131 aaaa /home/test upload
Command format:
java -jar  sftp_ AML_ Cron.jar remote_server_user_name remote_server_IP remote_server_password remote_server_directory action_(download_or_upload)

这个小程序支持两个功能：
1 从服务器下载文件（只获取三种后缀名：csv flg xml 的文件）到本地
2 从本地上传文件到服务器

从服务器上下载AML文件(获取三种后缀名：csv flg xml 的文件)的命令
java -jar  sftp_AML_cron.jar sftpaccount 10.255.7.131 aaaa /home/test download

上传文件到服务器的命令

java -jar  sftp_AML_cron.jar sftpaccount 10.255.7.131 aaaa /home/test upload

命令格式：
java -jar  sftp_AML_cron.jar 远程服务器的用户名 远程服务器的IP 远程服务器的密码 远程服务器的目录 动作（download or upload)
  

