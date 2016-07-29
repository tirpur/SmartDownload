package com.smartdownload.handler

import scala.concurrent.Future


/**
 * Created by shri on 24/7/16.
 */
class DownloadHandler {
  var handlerMap = Map[String,Option[ProtocolHandler]]()
  def register(protocol:String,protocolHandler: ProtocolHandler) = handlerMap += (protocol->Some(protocolHandler))
  def downloadNSave(urlList:String) = {
    var futureList = List[Future[Option[String]]]()
    urlList.split(",").foreach(dUrl=>{
      val proto = if(dUrl.contains(":")) dUrl.substring(0,dUrl.indexOf(':')) else dUrl
      handlerMap.getOrElse(proto,None) match {
        case Some(handler) => futureList ++= List(handler.download(dUrl))
        case None => println("No valid handler registered for protocol "+proto+". Download to url "+dUrl+" failed!")}
    })
    futureList
  }
}
