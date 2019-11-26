package com.github.radium226.git

import java.nio.file.Paths

import cats.data.ReaderT
import cats.effect.{Blocker, ExitCode, IO, IOApp}
import cats.~>
import cats.tagless.implicits._
import cats.implicits._

object Example extends IOApp {

  import Instances._

  case class Env(module: Module)

  type App[A] = ReaderT[IO, Env, A]

  implicit def actionToAppFunctionK: Action[IO, *] ~> App = λ[Action[IO, *] ~> App](_.local(_.module))
  implicit def appToActionFunctionK: App ~> Action[IO, *] = λ[App ~> Action[IO, *]](_.local(Env(_)))

  override def run(arguments: List[String]): IO[ExitCode] = {
    val service = Service[Action[IO, *]].translate(actionToAppFunctionK)(appToActionFunctionK)

    val program = for {
      repo <- service.init(Paths.get("/tmp"))
      _    <- service.addRemote(repo)("toto", "http://coucou.fr")
    } yield ExitCode.Success

    program.run(Env(Module()))
  }

}
