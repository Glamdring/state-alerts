package bg.statealerts.services.extractors

object ContentLocationType extends Enumeration {
	type ContentLocationType = Value
    val Table, LinkedPage, LinkedDocumentOnLinkedPage, LinkedDocumentInTable = Value
}