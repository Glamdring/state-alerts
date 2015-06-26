package bg.statealerts.scraper.config

object ContentLocationType extends Enumeration {
	type ContentLocationType = Value
    val Table, LinkedPage, LinkedDocumentOnLinkedPage, LinkedDocumentInTable = Value
}

/**
 * Java-friendly set of constants
 */
object ContentLocationTypeConstants {
	val TABLE = "Table"
	val LINKED_PAGE = "LinkedPage"
	val LINKED_DOCUMENT_ON_LINKED_PAGE = "LinkedDocumentOnLinkedPage"
	val LINKED_DOCUMENT_IN_TABLE = "LinkedDocumentInTable"
}