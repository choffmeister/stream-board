package de.choffmeister.streamboard.utils

import java.nio.file.{Path, Paths}

import com.typesafe.config.{ConfigException, Config => RawConfig}

object RichConfig {
  implicit class RichConfig(val underlying: RawConfig) extends AnyVal {
    def getOptionalString(path: String): Option[String] = try {
      Some(underlying.getString(path))
    } catch {
      case e: ConfigException.Missing => None
    }

    def getOptionalInt(path: String): Option[Int] = getOptionalString(path).map(_.toInt)

    def getPath(path: String): Path = Paths.get(underlying.getString(path))

    def getOptionalPath(path: String): Option[Path] = getOptionalString(path).map(p => Paths.get(p))
  }
}
