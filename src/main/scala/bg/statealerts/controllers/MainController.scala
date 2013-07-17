package bg.statealerts.controllers

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

import bg.statealerts.model.Document
import bg.statealerts.services.SearchService
import javax.inject.Inject

@Controller
class MainController {

  @Inject
  var searcher: SearchService = _
  
  @RequestMapping(Array("/search"))
  @ResponseBody
  def search(@RequestParam keywords: String): List[Document] = {
    searcher.search(keywords)
  }
}