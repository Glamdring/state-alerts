package bg.statealerts.controllers

import javax.inject.Inject
import org.springframework.stereotype.Controller
import bg.statealerts.services.ManagementService
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping(Array("/mgmt"))
//TODO password-protect
class ManagementController {

  @Inject
  var managmentService: ManagementService = _
  
  @RequestMapping(Array("/reindex"))
  def reindex() = {
    managmentService.reindex
  }  
  
  @RequestMapping(Array("/sendAlerts"))
  def sendAlerts() = {
    managmentService.sendAlerts
  }
}