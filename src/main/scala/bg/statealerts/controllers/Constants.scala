package bg.statealerts.controllers

import org.joda.time.DateTimeConstants

object Constants {
  val CookieAge = DateTimeConstants.SECONDS_PER_WEEK
  val AuthTokenCookieName = "authToken"
  val AuthTokenSeriesCookieName = "authSeries"
}