package com.github.radium226

import java.nio.file.Path


package object workspace {

  case class Workspace[F[_]](folderPath: Path)

}
