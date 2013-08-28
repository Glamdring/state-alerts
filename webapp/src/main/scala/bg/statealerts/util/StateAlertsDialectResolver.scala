package bg.statealerts.util

import org.hibernate.`type`.Type
import org.hibernate.engine.spi.Mapping
import org.hibernate.`type`.StandardBasicTypes
import org.hibernate.QueryException
import org.hibernate.engine.spi.SessionFactoryImplementor
import java.util.{ List => JList }
import java.util.{ List => JList }
import org.hibernate.QueryException
import org.hibernate.dialect.function.SQLFunction
import org.hibernate.dialect.MySQL5Dialect
import org.hibernate.dialect.MySQL5InnoDBDialect
import org.hibernate.service.jdbc.dialect.internal.StandardDialectResolver
import org.hibernate.service.jdbc.dialect.internal.AbstractDialectResolver
import java.sql.DatabaseMetaData
import org.hibernate.dialect.Dialect
import org.hibernate.dialect.MySQLDialect
import org.hibernate.dialect.H2Dialect
import org.hibernate.dialect.MySQLInnoDBDialect

class StateAlertsDialectResolver extends AbstractDialectResolver {
  override def resolveDialectInternal(metaData: DatabaseMetaData): Dialect = {
    val databaseName = metaData.getDatabaseProductName();
    val databaseMajorVersion = metaData.getDatabaseMajorVersion();

    if ("H2".equals(databaseName)) {
      return new H2Dialect with GroupConcatAwareDialect {
        override def toString() = "H2Dialect with GroupConcat"
      }
    }

    if ("MySQL".equals(databaseName)) {
      if (databaseMajorVersion >= 5) {
        return new MySQL5InnoDBDialect with GroupConcatAwareDialect {
          override def toString() = "MySQL5InnoDBDialect with GroupConcat"
        }
      }
      return new MySQLInnoDBDialect with GroupConcatAwareDialect {
        override def toString() = "MySQLInnoDBDialect with GroupConcat"
      }
    }
    return null
  }
}

trait GroupConcatAwareDialect extends Dialect {

  override def registerFunction(name: String, function: SQLFunction) = super.registerFunction(name, function)

  registerFunction("string_agg", new GroupConcatFunction())
}

// thanks to http://stackoverflow.com/questions/7005354/jpa-criteria-api-group-concat-usage
class GroupConcatFunction extends SQLFunction {

  override def hasArguments() = true

  override def hasParenthesesIfNoArguments() = true

  @throws(classOf[QueryException])
  override def getReturnType(firstArgumentType: Type, mapping: Mapping) = StandardBasicTypes.STRING

  @throws(classOf[QueryException])
  override def render(firstArgumentType: Type, arguments: JList[_], factory: SessionFactoryImplementor) = {
    if (arguments.size() != 1) {
      throw new QueryException(new IllegalArgumentException("group_concat should have exactly one arg"))
    }
    "group_concat(" + arguments.get(0) + ")"
  }
}
