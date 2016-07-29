package com.smartdownload

import java.io.File

import com.smartdownload.handler.{SFTPHandler, FTPHandler, HTTPHandler, DownloadHandler}
import com.typesafe.config.ConfigFactory
import org.scalatest.time.{Span, Seconds}
import org.scalatest.{Matchers, FlatSpec}
import scala.concurrent.ExecutionContext.Implicits.global
import org.scalatest.concurrent.ScalaFutures


/**
 * Created by shri on 27/7/16.
 */

class TestSpec extends FlatSpec with Matchers with ScalaFutures {
  "A valid HTTP url" should "get downloaded and saved correctly" in {
    val dHandler = new DownloadHandler
    dHandler.register("http",new HTTPHandler)
    val url = "http://ajax.googleapis.com/ajax/libs/jquery/1/jquery.min.js"
    val fileName = dHandler.handlerMap("http").get.fileName(url)

    whenReady(dHandler.downloadNSave(url).head,timeout(Span(6, Seconds))) {res => res
      match {
      case Some(rslt)=> {
        val tmpFile = new File(rslt)
        assert(tmpFile.exists())
      }
      case None=> fail()}
    }
  }

  "A valid FTP url with anonymous access" should "get downloaded and saved correctly" in {
    val appConf = ConfigFactory.load()

    val dHandler = new DownloadHandler
    dHandler.register("ftp",new FTPHandler)
    val url = "ftp://speedtest.tele2.net/1KB.zip"
    val fileName = dHandler.handlerMap("ftp").get.fileName(url)

    whenReady(dHandler.downloadNSave(url).head,timeout(Span(6, Seconds))) {res => res
    match {
      case Some(rslt)=> {
        val tmpFile = new File(rslt)
        assert(tmpFile.exists())
      }
      case None=> fail()}
    }
  }


  "A valid FTP url with user and password" should "get downloaded and saved correctly" in {
    val appConf = ConfigFactory.load()

    val dHandler = new DownloadHandler
    dHandler.register("ftp",new FTPHandler)
    val url = "ftp://demo:password@test.rebex.net/readme.txt"
    val fileName = dHandler.handlerMap("ftp").get.fileName(url)

    whenReady(dHandler.downloadNSave(url).head,timeout(Span(6, Seconds))) {res => res
    match {
      case Some(rslt)=> {
        val tmpFile = new File(rslt)
        assert(tmpFile.exists())
      }
      case None=> fail()}
    }
  }

  "A valid SFTP url with user and password" should "get downloaded and saved correctly" in {
    val appConf = ConfigFactory.load()

    val dHandler = new DownloadHandler
    dHandler.register("sftp",new SFTPHandler)
    val url = "sftp://demo:password@test.rebex.net:22/readme.txt"
    val fileName = dHandler.handlerMap("sftp").get.fileName(url)

    whenReady(dHandler.downloadNSave(url).head,timeout(Span(6, Seconds))) {res => res
    match {
      case Some(rslt)=> {
        val tmpFile = new File(rslt)
        assert(tmpFile.exists())
      }
      case None=> fail()}
    }
  }

  "An invalid HTTP url" should "throw exception" in {
    val dHandler = new DownloadHandler
    dHandler.register("http",new HTTPHandler)
    val url = "http://failureisrequired.org/ajax/libs/jquery/1/jquery.min.js"

    whenReady(dHandler.downloadNSave(url).head,timeout(Span(6, Seconds))) {res => res
    match {case Some(rslt)=> fail() case None=>assert(true)}
    }
    //      .map(x=> x match{case Some(name) => assert(name != fileName) case None => fail})
  }

  "An invalid FTP url" should "throw exception" in {
    val dHandler = new DownloadHandler
    dHandler.register("ftp",new FTPHandler)
    val badUrl = "ftp:/speedtest.tele2.net/1KB.zip"

    whenReady(dHandler.downloadNSave(badUrl).head,timeout(Span(6, Seconds))) {res => res
    match {case Some(rslt)=> fail() case None=>assert(true)}
    }
    //      .map(x=> x match{case Some(name) => assert(name != fileName) case None => fail})
  }

  "A valid HTTP URL" should "result into unique file name" in {
    val dHandler = new DownloadHandler
    dHandler.register("http",new HTTPHandler)
    val url1 = "http://ajax.googleapis.com/ajax/libs/jquery/1/jquery.min.js"
    val url2 = "http://ajax.amazon.com/ajax/libs/jquery/1/jquery.min.js"

    assert(dHandler.handlerMap("http").get.fileName(url1) != dHandler.handlerMap("http").get.fileName(url2))
  }

  "A valid FTP/SFTP URL with anonymous access" should "result into unique file name" in {
    val dHandler = new DownloadHandler
    dHandler.register("ftp",new FTPHandler)
    val url1 = "ftp://speedtest.tele2.net/1KB.zip"
    val url2 = "ftp://speedtest.tele3.net/1KB.zip"

    assert(dHandler.handlerMap("ftp").get.fileName(url1) != dHandler.handlerMap("ftp").get.fileName(url2))
  }

  "Two valid FTP/SFTP URIs with access details different but server and file name same" should "result into same file name" in {
    val dHandler = new DownloadHandler
    dHandler.register("ftp",new FTPHandler)
    val url1 = "ftp://user1:password1@speedtest.tele2.net/1KB.zip"
    val url2 = "ftp://user2:password2@speedtest.tele2.net/1KB.zip"

    assert(dHandler.handlerMap("ftp").get.fileName(url1) == dHandler.handlerMap("ftp").get.fileName(url2))
  }

}

