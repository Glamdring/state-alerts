/**
 * Copyright (C) 2009-2011 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fusesource.scalate.spring.view

import java.util.Locale
import javax.servlet.ServletConfig
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.fusesource.scalate.RenderContext
import org.fusesource.scalate.servlet.ServletRenderContext
import org.fusesource.scalate.servlet.ServletTemplateEngine
import org.springframework.web.context.ServletConfigAware
import scala.collection.JavaConversions._
import org.fusesource.scalate.TemplateException
import org.springframework.web.servlet.view.{ AbstractView, AbstractTemplateView, AbstractUrlBasedView }
import org.slf4j.LoggerFactory
import org.fusesource.scalate.util.ResourceNotFoundException
import org.fusesource.scalate.util.Logging

trait ScalateRenderStrategy extends Logging {
  def render(context: ServletRenderContext, model: Map[String, Any])
}

trait LayoutScalateRenderStrategy extends AbstractUrlBasedView with ScalateRenderStrategy {
  def render(context: ServletRenderContext, model: Map[String, Any]) {
    debug("Rendering view with name '" + getUrl + "' with model " + model)
    for ((key, value) <- model) {
      context.attributes(key) = value
    }
    context.engine.layout(getUrl, context)
  }
}

trait DefaultScalateRenderStrategy extends AbstractUrlBasedView with ScalateRenderStrategy {
  override def render(context: ServletRenderContext, model: Map[String, Any]) {
    debug("Rendering view with name '" + getUrl + "' with model " + model)
    context.render(getUrl, model)
  }
}

trait ViewScalateRenderStrategy extends AbstractUrlBasedView with  ScalateRenderStrategy {
  override def isUrlRequired() = false
  override def render(context: ServletRenderContext, model: Map[String, Any]) {
    debug("Rendering with model " + model)
    val it = model.get("it")
    if (it.isEmpty)
      throw new TemplateException("No 'it' model object specified.  Cannot render request")
    context.view(it.get.asInstanceOf[AnyRef])
  }
}

trait AbstractScalateView extends AbstractUrlBasedView {
  this: ScalateRenderStrategy =>
    
  var templateEngine: ServletTemplateEngine = _;
  
  def checkResource(locale: Locale): Boolean;
  
  override def renderMergedOutputModel(model: java.util.Map[String, Object],
    request: HttpServletRequest,
    response: HttpServletResponse): Unit = {

    val context = new ServletRenderContext(templateEngine, request, response, getServletContext)
    RenderContext.using(context) {
      render(context, model.asInstanceOf[java.util.Map[String, Any]].toMap)
    }
  }
}

abstract class ScalateUrlView extends AbstractScalateView with ScalateRenderStrategy {

  override def checkResource(locale: Locale): Boolean = try {
    debug("Checking for resource " + getUrl)
    templateEngine.load(getUrl)
    true
  } catch {
    case e: ResourceNotFoundException => {
      info("Could not find resource " + getUrl);
      false
    }
  }

}

class ScalateView extends AbstractScalateView with ViewScalateRenderStrategy {

  override def checkResource(locale: Locale) = true;
  
  override def isUrlRequired() = false

}
