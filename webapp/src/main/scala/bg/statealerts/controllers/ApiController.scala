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
	                            if (sources == null || sources.isEmpty()) Nil else asScalaBuffer(sources))
	    results;
    } else {
      throw new IllegalStateException("Not allowed to perform searched");
    }
  }
}