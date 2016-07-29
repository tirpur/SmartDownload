package com.smartdownload.driver

import com.smartdownload.handler.{FTPHandler, SFTPHandler, DownloadHandler, HTTPHandler}
import com.typesafe.config.ConfigFactory

import scala.util.Try
import scala.concurrent.ExecutionContext.Implicits.global
import com.smartdownload.config.ConfigParam._


/**
 * Created by shri on 23/7/16.
 */
object Driver extends App {
  //register shutdown hook to clean all temporary files created
  sys addShutdownHook(shutdown)
  val appConf = ConfigFactory.load()

  val dHandler = new DownloadHandler
  dHandler.register("http",new HTTPHandler)
  dHandler.register("sftp",new SFTPHandler)
  dHandler.register("ftp",new FTPHandler)
  val urlsToDownload = Try(Some(appConf.getString(downloadUrlConfig))) getOrElse None
  urlsToDownload match {
    case Some(urls) =>
      dHandler.downloadNSave(urls).foreach(f=>
        f.map(res => res match {case Some(name) => println(name) case None =>})
      )
    case None => println(helpMessageNoUrlToDownload)
  }
  def shutdown: Unit = {
    //Cleanup all the handler's current temporary file created
    dHandler.handlerMap.mapValues(pHandler=>pHandler match {case Some(protoHandler) => protoHandler.clean() case None =>})
  }
}
