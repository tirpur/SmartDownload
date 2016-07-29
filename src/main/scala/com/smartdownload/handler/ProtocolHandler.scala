package com.smartdownload.handler

import scala.concurrent.Future

/**
 * Created by shri on 24/7/16.
 */
trait ProtocolHandler {
  def protoUrlDelimiter():String
  def fileName(downloadUrl:String):String
  def download(downloadUrl:String):Future[Option[String]]
  def clean()
}
