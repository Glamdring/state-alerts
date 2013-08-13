package bg.statealerts.dao

case class QueryDetails {
    var query: String = _
    var queryName: String = _
    var paramNames: Array[String] = new Array[String](0)
    var paramValues: Array[Any] = new Array[Any](0)
    var start: Int = -1
    var count: Int = -1
    var cacheable: Boolean = _
}
