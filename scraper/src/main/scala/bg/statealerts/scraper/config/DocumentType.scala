package bg.statealerts.scraper.config

object DocumentType extends Enumeration {
	type DocumentType = Value
	val PDF, DOC, HTML = Value
}

/**
 * Java-friendly set of constants
 */
object DocumentTypeConstants {
	val PDF = "PDF"
	val DOC = "DOC"
	val HTML = "HTML"
}