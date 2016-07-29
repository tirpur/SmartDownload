package com.smartdownload.config

/**
 * Created by shri on 29/7/16.
 */
object ConfigParam {
  val filePathConfig = "download.file.path"
  val userDirProperty = "user.dir"
  val downloadUrlConfig = "download.urls"
  val helpMessageNoUrlToDownload =
    """
      |No urls given to download. Pass it as parameter.
      |If using sbt then sbt run -Ddownload.urls=<comma separated urls>.
      |If using uber jar pass -Ddownload.urls=<comma separated urls> as java arguments. For example
      |java -Ddownload.urls=ftp://speedtest.tele2.net/1KB.zip -Ddownload.file.path=/home/shri -jar /home/shri/SmartDownload/target/scala-2.11/SmartDownload-assembly-1.0.jar
    """.stripMargin

}
