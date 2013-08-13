package bg.statealerts.controllers

import org.joda.time.DateTimeConstants

object Constants {
  val COOKIE_AGE = DateTimeConstants.SECONDS_PER_WEEK
  val AUTH_TOKEN_COOKIE_NAME = "authToken"
  val AUTH_TOKEN_SERIES_COOKIE_NAME = "authSeries"
}