package bg.statealerts.services.extractors

import java.net.URL
import java.util.ArrayList

import scala.collection.JavaConversions
import scala.collection.mutable.Buffer

import com.gargoylesoftware.htmlunit.BrowserVersion
import com.gargoylesoftware.htmlunit.BrowserVersionFeatures
import com.gargoylesoftware.htmlunit.HttpMethod
import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.WebRequest
import com.gargoylesoftware.htmlunit.html.HtmlAnchor
import com.gargoylesoftware.htmlunit.html.HtmlElement
import com.gargoylesoftware.htmlunit.html.HtmlPage

object Foo {
  def main(args: Array[String]) {
    val webClient = new WebClient();

    var page: HtmlPage = webClient.getPage("http://dv.parliament.bg/DVWeb/broeveList.faces").asInstanceOf[HtmlPage];

    val list = page.getByXPath("//table[@id='broi_form:dataTable1']//a/img/..").asInstanceOf[ArrayList[HtmlAnchor]]
    for (link <- JavaConversions.asScalaBuffer(list)) {
        val commandString = link.getOnClickAttribute().replaceAll("return ", "");
        System.out.println(commandString);

        val executeJavaScript = page.executeJavaScript(commandString);

        val newPage = executeJavaScript.getNewPage();
        println(newPage.getWebResponse().getResponseHeaders())

        page = webClient.getPage("http://dv.parliament.bg/DVWeb/broeveList.faces");
    }
}

  def maina(args: Array[String]) {
    val bvf: Array[BrowserVersionFeatures] = new Array[BrowserVersionFeatures](1)
    bvf(0) = BrowserVersionFeatures.HTMLIFRAME_IGNORE_SELFCLOSING
    val bv: BrowserVersion = new BrowserVersion(BrowserVersion.FIREFOX_17.getApplicationName(),
      BrowserVersion.FIREFOX_17.getApplicationVersion(),
      BrowserVersion.FIREFOX_17.getUserAgent(),
      BrowserVersion.FIREFOX_17.getBrowserVersionNumeric(),
      bvf);
    val client: WebClient = new WebClient(bv)

    //val htmlPage: HtmlPage = client.getPage("file:///C:/Users/bozho/Desktop/bills.html");
    val url = "http://www.parliament.bg/bg/bills"
    val httpMethod = "POST"
    val request: WebRequest = new WebRequest(new URL(url), HttpMethod.valueOf(httpMethod));
    // POST parameters are set in the request body
    if (HttpMethod.valueOf(httpMethod) == HttpMethod.POST) {
      request.setRequestBody("from=&to=&L_ActL_title=&L_Ses_id=&L_Act_sign=&L_Act_im_id=&A_ns_C_id=&submit=%D0%A2%D1%8A%D1%80%D1%81%D0%B8")
    }
    val htmlPage: HtmlPage = client.getPage(request);
    println(htmlPage.getWebResponse.getContentAsString)

    var list: ArrayList[HtmlElement] = htmlPage.getByXPath("//table[@class='billsresult']/tbody/tr/td[4]").asInstanceOf[ArrayList[HtmlElement]]
    var buff: Buffer[HtmlElement] = JavaConversions.asScalaBuffer(list)
    for (element <- buff) {
      println(element.getTextContent())
    }
  }
}