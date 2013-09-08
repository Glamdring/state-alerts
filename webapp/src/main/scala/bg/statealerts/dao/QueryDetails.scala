package bg.statealerts.dao

sealed trait BaseQueryDetails {
  def params: Map[String, Any]
  def pagination: Pagination
  def cacheOptions: Option[CacheOptions]
}
case class CacheOptions(cacheable: Option[Boolean] = None, cacheMode: Option[String] = None)

sealed trait Pagination
object NoPagination extends Pagination
case class DefaultPagination(start: Int, count: Int) extends Pagination


case class NamedQueryDetails(
     queryName: String,
     params: Map[String, Any] = Map(),
     pagination: Pagination = NoPagination,
     cacheOptions: Option[CacheOptions] = None) extends BaseQueryDetails


case class QueryDetails (
  query: String,
  params: Map[String, Any] = Map(),
  pagination: Pagination = NoPagination,
  cacheOptions: Option[CacheOptions] = None) extends BaseQueryDetails

