package bg.statealerts.controllers

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

import bg.statealerts.model.Alert
import bg.statealerts.scraper.Extractor
import bg.statealerts.services.AlertService
import javax.annotation.Resource
import javax.inject.Inject

@Controller
@RequestMapping(Array("/alerts"))
class AlertController {

  @Inject
  var ctx: UserContext = _

  @Inject
  var alertService: AlertService = _

  @Resource(name="extractors")
  var extractors: List[Extractor] = _
  
  @ModelAttribute("sources")
  def getSource() = {
    extractors.map(_.descriptor)
  }

  @RequestMapping(value = Array("/new"))
  def newAlert() = {
    if (ctx.user == null) {
      "redirect:/"
    }
    else {
      "layout:alert"
    }
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
    model.addAttribute("alerts", alertService.getAlerts(ctx.user))
    return "layout:alerts"
  }

  @RequestMapping(value = Array("/delete"))
  @ResponseBody
  def delete(@RequestParam id: Int) = {
    alertService.delete(id)
  }
}