package com.smartdownload.handler

import java.io._

import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import dispatch._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Left, Right, Try}
import com.smartdownload.config.ConfigParam._


/**,
 * Created by shri on 17/11/15.
 */
class HTTPHandler extends ProtocolHandler {
  val appConf = ConfigFactory.load()
  def protoUrlDelimiter = "://"
  var fileUrl:Option[String] = None
  val tmpFileExtension = ".tmp"
  def clean() = {fileUrl match {case Some(dUrl) => Runtime.getRuntime.exec("rm -f "+fileName(dUrl)+tmpFileExtension) case None=> }}

  val pathToSave = Try(appConf.getString(filePathConfig)) getOrElse System.getProperty(userDirProperty)
  def movePermanent(filePath:String) = Runtime.getRuntime.exec("mv "+filePath+tmpFileExtension+" "+filePath)
  def fileName(downloadUrl:String) = pathToSave+"/"+downloadUrl.substring(downloadUrl.indexOf(protoUrlDelimiter)+protoUrlDelimiter.length).replace("/","_")
  def download(downloadUrl:String): Future[Option[String]] = {
    fileUrl = Some(downloadUrl)
    val urlContext = url(downloadUrl)
    Http(urlContext).either.map {
      case Right(r) => {
        //make file name based on url i.e just replace / with _
        val filePath = fileName(downloadUrl)
        val fos = new FileOutputStream(new File(filePath+tmpFileExtension))
        fos.write(r.getResponseBodyAsBytes)
        fos.close()
        movePermanent(filePath)
        println("Successfully fetched the file and saved to: "+filePath)
        dispatch.Http.shutdown()
        Some(filePath)
      }
      case Left(l) =>
        println("Downloading the file at url "+downloadUrl+ "failed!! :"+l.getMessage)
        None
    }
  }
}

