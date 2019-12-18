package com.github.radium226

import java.nio.file.Paths

import cats.effect.{ExitCode, IO, IOApp}
import com.github.radium226.git._
import com.github.radium226.io._

import cats.implicits._

import fileSystem._

object Program extends IOApp {

  override def run(arguments: List[String]): IO[ExitCode] = {
    System.local[IO]
      .use({ implicit localSystem =>
        for {
          repo <- git.clone[IO](Paths.get("/tmp/test"), "http://example.com")
          _    <- makeFolder[IO](Paths.get("/tmp"))
        } yield ()
      })
      .as(ExitCode.Success)
  }

}
