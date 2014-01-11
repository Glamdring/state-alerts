package bg.statealerts.controllers

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import bg.statealerts.model.Document
import bg.statealerts.services.SearchService
import javax.inject.Inject
import scala.collection.JavaConversions._
import scala.collection.mutable.Buffer
import com.codahale.jerkson.Json
import javax.servlet.http.HttpServletResponse
import org.apache.commons.io.IOUtils
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.ui.Model
import org.joda.time.Interval
import scala.collection.JavaConversions
import org.springframework.web.bind.annotation.RequestMethod
import bg.statealerts.services.UserService

@Controller
class MainController {

  @Inject
  var searcher: SearchService = _

  @Inject
  var userService: UserService = _
  
  @Inject
  var ctx: UserContext = _

  @RequestMapping(Array("/"))
  def index = {
    "layout:index"
  }

  @RequestMapping(Array("/search"))
  def search(
      @RequestParam keywords: String,
      @RequestParam(required=false, defaultValue="0") start: Long,
      @RequestParam(required=false) sources: java.util.List[String],
      model: Model): String = {
    val results: java.util.List[Document] = 
        if (start == 0 && sources == null) {
            searcher.search(keywords)
        } else {
            searcher.search(
                            keywords,
                            new Interval(start, System.currentTimeMillis()),
                            if (sources == null || sources.isEmpty()) Nil else sources)
        }
    model.addAttribute("results", results)
    "searchResults"
  }

  @RequestMapping(value=Array("/api/sources"), method=Array(RequestMethod.GET))
  @ResponseBody
  def getSources(): java.util.List[String] = {
    asJavaList(searcher.getSources());
  }
  
  @RequestMapping(value=Array("/api/search"), method=Array(RequestMethod.GET))
  @ResponseBody
  def apiSearch(
      @RequestParam keywords: String,
      @RequestParam start: Long,
      @RequestParam token: String,
      @RequestParam(required=false) sources: java.util.List[String],
      model: Model): java.util.List[Document] = {
    if (userService.canPerformApiSearch(token)) {
    	searcher.logApiSearch(token, keywords, asScalaBuffer(sources).toList);
	    val results: java.util.List[Document] = 
	            searcher.search(
	                            keywords,
	                            new Interval(start, System.currentTimeMillis()),
	                            if (sources == null || sources.isEmpty()) Nil else sources)
	    results;
    } else {
      throw new IllegalStateException("Not allowed to perform searched");
    }
  }
      
  @RequestMapping(Array("/about"))
  def about(): String = {
    "layout:about"
  }
  
  // serializing to JSON (using the built-in mechanisms doesn't work well)
  // val result = Json.generate(bufferAsJavaList(buffer))
  // IOUtils.write(result, response.getOutputStream())
}