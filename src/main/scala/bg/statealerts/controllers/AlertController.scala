package bg.statealerts.controllers

import org.springframework.stereotype.Controller
import javax.inject.Inject
import org.springframework.web.bind.annotation.RequestMapping
import bg.statealerts.model.Alert
import bg.statealerts.services.AlertService
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestParam
import scala.collection.JavaConversions
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping(Array("/alerts"))
class AlertController {

  @Inject
  var ctx: UserContext = _

  @Inject
  var alertService: AlertService = _

  @RequestMapping(value = Array("/new"))
  def newAlert(): String = {
    return "alert";
  }

  @RequestMapping(value = Array("/save"))
  def saveAlert(alert: Alert): String = {
    if (ctx.user == null) {
      return "redirect:/"
    }
    alertService.saveAlert(alert, ctx.user)
    return "redirect:/alerts/list"
  }

  @RequestMapping(value = Array("/list"))
  def list(model: Model): String = {
    if (ctx.user == null) {
      return "redirect:/"
    }
    model.addAttribute("alerts", JavaConversions.asJavaList(alertService.getAlerts(ctx.user)))
    return "alertList"
  }

  @RequestMapping(value = Array("/delete"))
  @ResponseBody
  def saveAlert(@RequestParam id: Int) = {
    alertService.delete(id)
  }
}