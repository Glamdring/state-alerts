package bg.statealerts.controllers

import org.springframework.context.annotation.DependsOn
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import org.springframework.stereotype.Controller
import org.springframework.stereotype.Service
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import scala.collection.JavaConversions._

import org.joda.time.Interval

import bg.statealerts.model.Document
import bg.statealerts.services.SearchService
import javax.inject.Inject

@Controller
class MainController {

  @Inject 
  var searcher: SearchService = _

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

  @RequestMapping(Array("/about"))
  def about(): String = {
    "layout:about"
  } 
}