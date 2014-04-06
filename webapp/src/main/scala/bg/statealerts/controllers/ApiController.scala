package bg.statealerts.controllers

import scala.collection.JavaConversions._
import org.springframework.context.annotation.DependsOn
import org.springframework.stereotype.Controller
import org.springframework.stereotype.Service
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import bg.statealerts.model.Document
import bg.statealerts.services.SearchService
import bg.statealerts.services.UserService
import javax.inject.Inject
import javax.persistence.Entity
import org.springframework.web.bind.annotation.RequestMethod
import org.joda.time.Interval
import com.mangofactory.swagger.annotations.ApiInclude
import com.wordnik.swagger.annotations.Api
import org.joda.time.DateTime

@Controller
@RequestMapping(Array("/api"))
@Api("api")
class ApiController {

  @Inject
  var searcher: SearchService = _

  @Inject
  var userService: UserService = _
  
  @RequestMapping(value=Array("/sources"), method=Array(RequestMethod.GET))
  @ResponseBody
  @ApiInclude
  def getSources(): java.util.List[String] = {
    seqAsJavaList(searcher.getSources());
  }
  
  @RequestMapping(value=Array("/search"), method=Array(RequestMethod.GET))
  @ResponseBody
  @ApiInclude
  def apiSearch(
      @RequestParam keywords: String,
      @RequestParam since: Long,
      @RequestParam token: String,
      @RequestParam(required=false) sources: java.util.List[String]): java.util.List[Document] = {
    
    if (userService.canUseApi(token)) {
    	searcher.logApiUsage(token, keywords, asScalaBuffer(sources).toList, "search");
	    val results: java.util.List[Document] = 
	            searcher.search(
	                            keywords,
	                            new Interval(since, System.currentTimeMillis()),
	                            if (sources == null || sources.isEmpty()) Nil else asScalaBuffer(sources))
	    results;
    } else {
    	throw new IllegalStateException("Not allowed to perform search");
    }
  }
  
  @RequestMapping(value=Array("/list"), method=Array(RequestMethod.GET))
  @ResponseBody
  @ApiInclude
  def list(
      @RequestParam since: Long,
      @RequestParam token: String,
      @RequestParam(required=false) sources: java.util.List[String]): java.util.List[Document] = {

    if (userService.canUseApi(token)) {
		searcher.logApiUsage(token, null, asScalaBuffer(sources).toList, "list");
	    val results: java.util.List[Document] = searcher.list(new DateTime(since));
	    results;
    } else {
    	throw new IllegalStateException("Not allowed to use API");
    }
  }
}