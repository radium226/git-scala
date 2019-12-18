package com.github.radium226.git

import java.nio.file.Path

import com.github.radium226.io._

import cats.Applicative
import cats.data.NonEmptyList
import cats.implicits._

case class Remote(name: RemoteName, uri: RemoteURI)

case class Credentials(userName: UserName, password: Password)

case class Repo(folderPath: Path)

trait Git[F[_]] {

  def clone(folderPath: Path, remoteURI: RemoteURI): F[Repo]

}

object Git {

  def apply[F[_]: Git]: Git[F] = implicitly

  implicit def gitInstance[F[_]: Applicative: System]: Git[F] = new Git[F] {

    override def clone(folderPath: Path, remoteURI: RemoteURI): F[Repo] = {
      executor.execute(NonEmptyList.of("echo", "Comment ça va, les gars ?")).as(Repo(folderPath))
    }

  }

}

trait GitSyntax {

  def clone[F[_]: Git](folderPath: Path, remoteURI: RemoteURI): F[Repo] = Git[F].clone(folderPath, remoteURI)

}
