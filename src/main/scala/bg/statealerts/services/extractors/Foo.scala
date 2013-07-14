package bg.statealerts.services.extractors

import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.HtmlPage
import java.util.ArrayList
import com.gargoylesoftware.htmlunit.html.HtmlElement
import scala.collection.mutable.Buffer
import scala.collection.JavaConversions

object Foo {
  def main(args: Array[String]) {
    val client: WebClient = new WebClient()
    client.setJavaScriptEnabled(false)
    val htmlPage: HtmlPage = client.getPage("file:///C:/Users/bozho/Desktop/bills.html");
    var list: ArrayList[HtmlElement] = htmlPage.getByXPath("//table[@class='billsresult']/tbody/tr/td[4]").asInstanceOf[ArrayList[HtmlElement]]
    var buff: Buffer[HtmlElement] = JavaConversions.asScalaBuffer(list)
    for (element <- buff) {
      println(element.getTextContent())
    }
  }
}