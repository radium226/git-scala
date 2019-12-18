package com.github.radium226

package object git {

  // Remote
  type RemoteName = String
  type RemoteURI = String

  // Credentials
  type UserName = String
  type Password = String

  object git extends GitSyntax

}
