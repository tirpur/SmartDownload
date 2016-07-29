Environment:
===========
1. JDK 8.0
2. Scala 2.11.7
3. SBT 0.13

Compilation and Execution:
=========================
SmartDownload uses SBT as build tool.
1. Use "sbt test" to run regression test
2. Use "sbt run" to execute the SmartDownload after compiling
3. Use "sbt assembly" to create uber jar

Configuration and Usage:
========================
The configuration can be passed the SmartDownlod using java style config i.e -Dconfig.name=value
1. Use "download.urls" to give comma separated URLs for the SmartDownload to download and save.
Example: 
sbt run -Ddownload.urls=http://typesafehub.github.io/config/latest/api/com/typesafe/config/Config.html,ftp://speedtest.tele2.net/1KB.zip
java -Ddownload.urls=http://typesafehub.github.io/config/latest/api/com/typesafe/config/Config.html,ftp://speedtest.tele2.net/1KB.zip -jar target/scala-2.11/SmartDownload-assembly-1.0.jar

2. Use "download.file.path" to tell explicitly the directory where the files to be saved.
Example: 
sbt run -Ddownload.urls=http://typesafehub.github.io/config/latest/api/com/typesafe/config/Config.html,ftp://speedtest.tele2.net/1KB.zip -Ddownload.file.path=/home/shri/downloads
java -Ddownload.urls=http://typesafehub.github.io/config/latest/api/com/typesafe/config/Config.html,ftp://speedtest.tele2.net/1KB.zip -Ddownload.file.path=/home/shri/downloads -jar target/scala-2.11/SmartDownload-assembly-1.0.jar

Note that when this config is not provided SmartDownload will save the file into current directory.

Extension:
==========
To add a new download protocol and its handler follow the below given steps:
1. Add new handler class extending com.smartdownload.handler.ProtocolHandler trait.
2. Register the protocol to DownloadHandler by calling register giving protocol name and instance of the handler.
 For example to register SFTP protocol handler:  dHandler.register("sftp",new SFTPHandler)
Note that protcol name registered must be same as it is called in the URI.
3. Add UT cases in TestSpec.scala

Caveats:
========
1. We had to choose correctness over performance for example rather than using Buffered Input stream had to use non bufferred to satisfy requirement of bigger files.
2. Single threaded so giving a long list of comma separated urls may be slow.

Future Plans:
=============
1. Use actors for async parallel downloads.
3. Distibuting tasks on multiple processes/machines of huge downloads based on byte ranges to make it faster.
