package bg.statealerts.scraper

class Pager(url: String, bodyParams: Option[String], multiplier: Int = 1, firstPage: Int = 1){
	
  var currentPage: Int = firstPage
  
  def getPageUrl(): String = {
    url.replace("{x}", String.valueOf(currentPage * multiplier))
  }

  def getBodyParams(): String = {
    if (bodyParams.isEmpty) {
      return ""
    }
    bodyParams.get.replace("{x}", String.valueOf(currentPage * multiplier))
  }

  /**
   * Advances the pager. If there is no paging parameter, and therefore no next page, return false; otherwise return true
   */
  def next() = {
    currentPage += 1
  }
}