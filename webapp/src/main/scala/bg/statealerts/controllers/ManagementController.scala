package bg.statealerts.controllers

import javax.inject.Inject
import org.springframework.stereotype.Controller
import bg.statealerts.services.ManagementService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping(Array("/mgmt"))
class ManagementController {

  @Inject
  var managmentService: ManagementService = _

  @Inject
  var userContext: UserContext = _
  
  @RequestMapping(Array("/reindex"))
  def reindex() = {
    if (userContext.getUser != null && userContext.getUser.admin) {
    	managmentService.reindex
    }
  }  
  
  @RequestMapping(Array("/sendAlerts"))
  def sendAlerts() = {
    if (userContext.getUser != null && userContext.getUser.admin) {
    	managmentService.sendAlerts
    }
  }
  
  @RequestMapping(Array("/sendTestMail"))
  def sendTestMail(@RequestParam to: String) = {
    val user = userContext.getUser
    if (user != null && user.admin) {
      managmentService.sendTestMail(to)
    }
  }
}