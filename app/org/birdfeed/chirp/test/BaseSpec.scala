package org.birdfeed.chirp.test

import org.birdfeed.chirp.database.models.ApiKey
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.libs.ws.WSClient

class BaseSpec extends PlaySpec with GuiceOneServerPerSuite {

  val wsClient = app.injector.instanceOf[WSClient]
  var testKey = ApiKey(true).create.key
}
