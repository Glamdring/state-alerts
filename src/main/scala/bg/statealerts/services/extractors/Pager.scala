package bg.statealerts.services.extractors

class Pager(url: String, multiplier: Int = 1){
	
  var currentPage: Int = 0
  def getNextPageUrl(): String = {
    currentPage += 1
    url.replace("${x}", String.valueOf(currentPage * multiplier))
  }
}