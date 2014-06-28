package bg.statealerts.services

import java.io.File
import java.net.URL
import java.util.ArrayList
import scala.collection.JavaConversions
import scala.collection.mutable.Buffer
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.TermQuery
import org.apache.lucene.store.FSDirectory
import com.gargoylesoftware.htmlunit.BrowserVersion
import com.gargoylesoftware.htmlunit.BrowserVersionFeatures
import com.gargoylesoftware.htmlunit.HttpMethod
import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.WebRequest
import com.gargoylesoftware.htmlunit.html.HtmlElement
import com.gargoylesoftware.htmlunit.html.HtmlPage
import org.apache.lucene.index.Term
import org.joda.time.DateTime

object Foo {
  def main(args: Array[String]) {
	  println(new DateTime().minusWeeks(1).getMillis());
	  val indexReader = DirectoryReader.open(FSDirectory.open(new File("c:/config/statealerts/index")));
	  println(indexReader.maxDoc())
	  val searcher = new IndexSearcher(indexReader)
	  println(searcher.search(new TermQuery(new Term("text", "технологии")), 50).totalHits)
  }

  def maina(args: Array[String]) {
    val bvf: Array[BrowserVersionFeatures] = new Array[BrowserVersionFeatures](1)
    bvf(0) = BrowserVersionFeatures.HTMLIFRAME_IGNORE_SELFCLOSING
    val bv: BrowserVersion = new BrowserVersion(BrowserVersion.FIREFOX_17.getApplicationName(),
      BrowserVersion.FIREFOX_24.getApplicationVersion(),
      BrowserVersion.FIREFOX_24.getUserAgent(),
      BrowserVersion.FIREFOX_24.getBrowserVersionNumeric(),
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