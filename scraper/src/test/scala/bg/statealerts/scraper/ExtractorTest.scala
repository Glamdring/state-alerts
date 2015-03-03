package bg.statealerts.scraper

import java.io.File
import java.io.FileOutputStream
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.joda.time.DateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import bg.statealerts.scraper.config.ExtractorDescriptor
import bg.statealerts.scraper.config.ElementPaths
import bg.statealerts.scraper.config.HttpRequest

class ExtractorTest {

  @Test
  @throws(classOf[Exception])
  def linkedDocumentOnLinkedPageTest() = {
    var tmpDir = System.getProperty("java.io.tmpdir")
    if (!tmpDir.startsWith("/")) {
      tmpDir = "/" + tmpDir
    }
    copyFiles(tmpDir)
    
    val descriptor = new ExtractorDescriptor(
        sourceKey = "ParliamentLawProjects", 
        sourceDisplayName = Some("LawProjects"), 
        enabled = Some(true), 
        documentType = "PDF", 
        contentLocationType = "LinkedDocumentOnLinkedPage",
        url = "file://" + tmpDir + "/bills.htm",
        httpRequest = Some(new HttpRequest(
            method = Some("POST"),
            bodyParams = Some("from=&to=&L_ActL_title=&L_Ses_id=&L_Act_sign=&L_Act_im_id=&A_ns_C_id=&submit=%D0%A2%D1%8A%D1%80%D1%81%D0%B8"),
            headers = None, warmUpRequest = None, warmUpRequestUrl = None
        )),
        paths = new ElementPaths(
            tableRowPath = "//table[@class='billsresult']/tbody/tr[position()>1]",
			documentLinkPath = Some(".//td[2]/a"),
			documentPageLinkPath = Some("//table[@class='bills']/tbody/tr[5]/td[2]/a"),
			datePath = Some(".//td[4]"),
			titlePath = Some(".//td[2]/a"),
			externalIdPath = Some(".//td[3]"),
			additionalMetaDataPaths = None, metaDataUrlPath = None,
			documentPageTitlePath = None, contentPath = None, documentPageDatePath = None
        ),
        pagingMultiplier = 0,
		firstPage = 1,
        dateFormat = "dd/MM/yyyy",
        dateLocale = None,
        entriesPerRow = None,
        heuristics = None,
        dateRegex = None,
        javascriptRequired = None,
        failOnError = None
    )
    
    val extractor = new Extractor(descriptor)
    extractor.baseUrl = ""
    val result = extractor.extract(new DateTime().withMonthOfYear(7).withYear(2013).withDayOfMonth(5))
    assertEquals(12, result.size)
    assertTrue(result(0).content.contains("народната просвета"))
    assertEquals("354-01-44", result(0).externalId)
  }
  
  private def copyFiles(tmpDir: String) = {
    var file = new File(tmpDir, "bills.htm")
    file.deleteOnExit()
    var in = getClass().getResourceAsStream("/pages/bills.htm")
    var content = IOUtils.toString(in)
    FileUtils.writeStringToFile(file, content.replace("${tmpdir}", tmpDir))
    
    file = new File(tmpDir, "bill.htm")
    file.deleteOnExit()
    in = getClass().getResourceAsStream("/pages/bill.htm")
    content = IOUtils.toString(in)
    FileUtils.writeStringToFile(file, content.replace("${tmpdir}", tmpDir))
    
    file = new File(tmpDir, "354-01-50.pdf")
    file.deleteOnExit()
    val out = new FileOutputStream(file)
    in = getClass().getResourceAsStream("/pages/354-01-50.pdf")
    IOUtils.copy(in, out)
    out.close()
  }
}