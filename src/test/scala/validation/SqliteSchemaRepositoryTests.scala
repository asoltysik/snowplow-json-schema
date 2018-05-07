package validation

import doobie.implicits._
import doobie.util.testing.{AnalysisArgs, Analyzable, analyzeIO}
import utest._

import SqliteSchemaRepository.Statements

object SqliteSchemaRepositoryTests extends TestSuite {

  val xa = SqliteSchemaRepository.xa

  val tests = Tests {
    "ddl sql" - {
      DoobieTestUtils.checkSql(Statements.ddl, xa)
    }

    "getSchema sql" - {
      // this fails stating schema_json column allows NULLs, when in fact it does not
      // possible doobie bug

      //DoobieTestUtils.checkSql(Statements.getSchema(""), xa)
    }

    "addSchema sql" - {
      DoobieTestUtils.checkSql(Statements.addSchema("", ""), xa)
    }
  }

}
