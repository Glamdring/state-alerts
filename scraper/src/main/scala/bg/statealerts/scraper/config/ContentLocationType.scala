package bg.statealerts.scraper.config

object ContentLocationType extends Enumeration {
	type ContentLocationType = Value
    val Table, LinkedPage, LinkedDocumentOnLinkedPage, LinkedDocumentInTable = Value
}