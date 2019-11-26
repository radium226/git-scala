package com.github.radium226

import java.nio.file.Path

import cats.data.ReaderT
import cats.effect.Sync
import cats.tagless._
import cats.implicits._
import cats.tagless.implicits._
import cats._
import com.github.radium226.git.Service

package object git {

  type URI = String

  case class Repo[F[_]](folderPath: Path) { self =>

    def translate[G[_]](fgK: F ~> G)(gfK: G ~> F): Repo[G] = {
      Repo[G](folderPath)
    }

  }

  trait Service[F[_]] {
    self =>

    def clone(folderPath: Path, remoteURI: URI): F[Repo[F]]

    def init(folderPath: Path): F[Repo[F]]

    def addRemote(repo: Repo[F])(remoteName: String, remoteURI: URI): F[Unit]

    def translate[G[_]: Functor](fgK: F ~> G)(gfK: G ~> F): Service[G] = new Service[G] {

      def clone(folderPath: Path, remoteURI: URI): G[Repo[G]] = {
        fgK(self.clone(folderPath, remoteURI)).map(_.translate(fgK)(gfK))
      }

      def init(folderPath: Path): G[Repo[G]] = {
        fgK(self.init(folderPath)).map(_.translate(fgK)(gfK))
      }

      def addRemote(repo: Repo[G])(remoteName: String, remoteURI: URI): G[Unit] = {
        fgK(self.addRemote(repo.translate(gfK)(fgK))(remoteName, remoteURI))
      }

    }

  }

  object Service {

    def apply[F[_]: Service]: Service[F] = implicitly

  }

  trait DefaultInstances {

    implicit def instance[F[_]: Sync]: Service[Action[F, *]] = new Service[Action[F, *]] {

      def clone(folderPath: Path, remoteURI: URI): Action[F, Repo[Action[F, *]]] = {
        Action
            .make({ module =>
              Sync[F].delay(println(s"Clone ${remoteURI} in ${folderPath} (with module=${module})"))
            })
            .as(Repo(folderPath))
      }

      def init(folderPath: Path): Action[F, Repo[Action[F, *]]] = {
        Action
            .make({ module =>
              Sync[F].delay(println(s"Init in ${folderPath} (with module=${module})"))
            })
            .as(Repo(folderPath))
      }

      def addRemote(repo: Repo[Action[F, *]])(remoteName: String, remoteURI: URI): Action[F, Unit] = {
        Action
            .make[F, Unit]({ module =>
              Sync[F].delay(println(s"Add remote ${remoteName} in ${remoteURI} to ${repo} (with module=${module})"))
            })
            .void
      }

    }

  }

  trait Instances extends DefaultInstances {



    /*implicit def translatedInstance[F[_]: Service, G[_]: Functor](implicit fgK: F ~> G, gfK: G ~> F): Service[G] = {
      Service[F].translate[G](fgK)(gfK)
    }*/

  }

  object Instances extends Instances

  /*import Service.autoDerive._

  implicit class RepoOps[F[_]](repo: Repo[F]) {

    def addRemote(name: String, uri: URI): F[Unit] = {
      Service[F].addRemote(repo)(name, uri)
    }

  }*/

  case class Module()

  type Action[F[_], A] = ReaderT[F, Module, A]

  object Action {

    def make[F[_], A](f: Module => F[A]): Action[F, A] = {
      ReaderT(f)
    }

    def liftF[F[_], A](fa: F[A]): Action[F, A] = {
      ReaderT.liftF(fa)
    }

  }

}
