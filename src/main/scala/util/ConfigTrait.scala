package util

import com.typesafe.config.{Config, ConfigFactory}

trait ConfigTrait:
  lazy val config: Config = ConfigFactory.load()
