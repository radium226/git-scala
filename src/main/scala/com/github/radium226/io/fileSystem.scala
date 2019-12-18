package com.github.radium226.io

import java.nio.file.{Files, Path}

import cats.effect.{Blocker, ContextShift, Sync}

trait FileSystem[F[_]] {

  def makeFolder(folderPath: Path): F[Unit]

}

class LocalFileSystem[F[_]: Sync: ContextShift](ioBlocker: Blocker) extends FileSystem[F] {

  override def makeFolder(folderPath: Path): F[Unit] = {
    ioBlocker.delay(Files.createDirectories(folderPath))
  }

}

object FileSystem {

  implicit def fileSystemSystem[F[_]: System]: FileSystem[F] = System[F].fileSystem

  def local[F[_]: Sync: ContextShift](ioBlocker: Blocker): FileSystem[F] = new LocalFileSystem[F](ioBlocker)

  def apply[F[_]: FileSystem]: FileSystem[F] = implicitly

}

trait FileSystemSyntax {

  def makeFolder[F[_]: FileSystem](folderPath: Path): F[Unit] = FileSystem[F].makeFolder(folderPath)

}
