package com.smartdownload.handler

import java.io._

import com.typesafe.config.ConfigFactory
import org.apache.commons.vfs2.impl.StandardFileSystemManager
import org.apache.commons.vfs2.provider.ftp.FtpFileSystemConfigBuilder
import org.apache.commons.vfs2.{FileSystemOptions, Selectors}
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future
import scala.util.Try
import com.smartdownload.config.ConfigParam._

/**,
 * Created by shri on 17/11/15.
 */
class FTPHandler extends ProtocolHandler {
  val appConf = ConfigFactory.load()
  def protoUrlDelimiter = "://"
  val tmpFileExtension = ".tmp"
  //system command to move the tmp file to permananet
  def cmdToMoveTempToPerm(filePath:String) = "mv "+filePath+tmpFileExtension+" "+filePath

  var fileUrl:Option[String] = None
  //supported clean method for current processing to remove temporary file created
  def clean() = {fileUrl match {case Some(dUrl) => Runtime.getRuntime.exec("rm -f "+fileName(dUrl)+tmpFileExtension) case None=> }}
  val pathToSave = Try(appConf.getString(filePathConfig)) getOrElse System.getProperty(userDirProperty)
  //get file name. In case of URI with access details get file name differently than that of anonymous access
  def fileName(downloadUrl:String) = if(!downloadUrl.contains("@"))
    pathToSave+"/"+downloadUrl.substring(downloadUrl.indexOf(protoUrlDelimiter)+protoUrlDelimiter.length).replace("/","_")
  else pathToSave+"/"+downloadUrl.substring(downloadUrl.indexOf("@")+1).replace("/","_")

  def movePermanent(filePath:String) = Runtime.getRuntime.exec(cmdToMoveTempToPerm(filePath))

  def getDefOptions = {
    val opts = new FileSystemOptions()
    FtpFileSystemConfigBuilder.getInstance().setPassiveMode(opts, true)
    FtpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(opts, true)
    FtpFileSystemConfigBuilder.getInstance().setDataTimeout(opts, 10000)
    opts
  }
  def download(downloadUrl:String): Future[Option[String]] = {
    fileUrl = Some(downloadUrl)
    val manager = new StandardFileSystemManager()
    try {
      manager.init()
      val file = new File(fileName(downloadUrl))
      //save to .tmp file as we dont the partially completed file
      val localFile = manager.resolveFile(file.getAbsolutePath+tmpFileExtension)
      val remoteFile = manager.resolveFile(downloadUrl,getDefOptions)
      localFile.copyFrom(remoteFile,Selectors.SELECT_SELF)
      //download complete so get rid of temp file
      movePermanent(file.getAbsolutePath)

      println("Download successul! File saved to "+file.getAbsolutePath)
      Future{Some(file.getAbsolutePath)}
    } catch {
      case e:Exception => /*e.printStackTrace()*/
        println("Some exception occurrred while downloading file from URL "+downloadUrl+"  "+e.getMessage)
        Future{None}
    } finally {
      manager.close()
    }
  }
}

