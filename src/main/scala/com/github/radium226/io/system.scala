package com.github.radium226.io

import cats.effect.{Blocker, ContextShift, Resource, Sync}

case class System[F[_]](fileSystem: FileSystem[F], executor: Executor[F])

object System {

  def apply[F[_]: System]: System[F] = implicitly

  def local[F[_]: Sync: ContextShift]: Resource[F, System[F]] = {
    for {
      fileSystemBlocker <- Blocker[F]
      executorBlocker   <- Blocker[F]
    } yield System(FileSystem.local(fileSystemBlocker), Executor.local(executorBlocker))
  }

}