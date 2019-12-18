package com.github.radium226

import cats.data.NonEmptyList

package object io {

  type ExitCode = Int

  type Argument = String

  type Command = NonEmptyList[Argument]

  object executor extends ExecutorSyntax

  object fileSystem extends FileSystemSyntax

}
