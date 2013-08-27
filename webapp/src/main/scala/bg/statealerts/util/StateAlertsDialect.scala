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

class StateAlertsDialect extends MySQL5InnoDBDialect {
    registerFunction("string_agg", new MysqlGroupConcatFunction())
}

// thanks to http://stackoverflow.com/questions/7005354/jpa-criteria-api-group-concat-usage
class MysqlGroupConcatFunction extends SQLFunction {

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
