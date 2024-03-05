package com.roshan.services

import zio.{&, Console, Scope, ZIO, ZLayer}
import zio.http.{Body, Client, Header, Headers, MediaType, Request}

case class HttpClientService() {
  def request(url: String, bdy: String, token: String): ZIO[Client & Scope, Throwable, String] = {
    val headers = Headers(Header.ContentType(MediaType.application.`json`),Header.Authorization.Bearer(token))
    for {
      res <- Client.request(Request.post(url, Body.fromString(bdy)).addHeaders(headers))
      data <- res.body.asString
      _ <- Console.printLine(data)
    } yield (data)

  }
}

object HttpClientService {
  val live = ZLayer.fromFunction(HttpClientService.apply _)
}
