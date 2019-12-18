package com.github.radium226.io

import cats.effect.{Blocker, ContextShift, Sync}

import scala.jdk.CollectionConverters._

trait Executor[F[_]] {

  def execute(command: Command): F[ExitCode]

}

object Executor {

  implicit def executorSystem[F[_]: System]: Executor[F] = System[F].executor

  def local[F[_]: Sync: ContextShift](blocker: Blocker): LocalExecutor[F] = new LocalExecutor[F](blocker)

  def apply[F[_]: Executor]: Executor[F] = implicitly

}

trait ExecutorSyntax {

  def execute[F[_]: Executor](command: Command): F[ExitCode] = {
    Executor[F].execute(command)
  }

}

object ExecutorSyntax extends ExecutorSyntax

class LocalExecutor[F[_]: Sync: ContextShift](blocker: Blocker) extends Executor[F] {

  def execute(command: Command): F[ExitCode] = {
    blocker.delay[F, ExitCode]({
      val process = new ProcessBuilder()
        .command(command.toList.asJava)
        .inheritIO()
        .start()
      process.waitFor()
    })
  }

}
