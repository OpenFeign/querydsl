package fluentq.jpa;

import fluentq.core.Target;
import fluentq.sql.CUBRIDTemplates;
import fluentq.sql.DerbyTemplates;
import fluentq.sql.H2Templates;
import fluentq.sql.HSQLDBTemplates;
import fluentq.sql.MySQLTemplates;
import fluentq.sql.OracleTemplates;
import fluentq.sql.PostgreSQLTemplates;
import fluentq.sql.SQLServer2008Templates;
import fluentq.sql.SQLTemplates;
import fluentq.sql.SQLiteTemplates;
import fluentq.sql.TeradataTemplates;

/**
 * @author tiwe
 */
public final class Mode {

  public static final ThreadLocal<String> mode = new ThreadLocal<>();

  public static final ThreadLocal<Target> target = new ThreadLocal<>();

  public static SQLTemplates getSQLTemplates() {
    switch (target.get()) {
      case CUBRID:
        return new CUBRIDTemplates();
      case DERBY:
        return new DerbyTemplates();
      case H2:
        return new H2Templates();
      case HSQLDB:
        return new HSQLDBTemplates();
      case SQLSERVER:
        return new SQLServer2008Templates();
      case MYSQL:
        return new MySQLTemplates();
      case ORACLE:
        return new OracleTemplates();
      case POSTGRESQL:
        return new PostgreSQLTemplates();
      case SQLITE:
        return new SQLiteTemplates();
      case TERADATA:
        return new TeradataTemplates();
    }
    throw new IllegalStateException("Unknown mode " + mode);
  }

  private Mode() {}
}
