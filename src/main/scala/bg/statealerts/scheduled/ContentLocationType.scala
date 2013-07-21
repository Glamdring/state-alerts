package bg.statealerts.scheduled

object ContentLocationType extends Enumeration {
    type WeekDay = Value
    val Table, LinkedPage, LinkedDocumentOnLinkedPage, LinkedDocumentInTable = Value
}