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
import fluentq.sql.TursoTemplates;

/**
 * @author tiwe
 */
public final class Mode {

  public static final ThreadLocal<String> mode = new ThreadLocal<>();

  public static final ThreadLocal<Target> target = new ThreadLocal<>();

  public static SQLTemplates getSQLTemplates() {
    return switch (target.get()) {
      case CUBRID -> new CUBRIDTemplates();
      case DERBY -> new DerbyTemplates();
      case H2 -> new H2Templates();
      case HSQLDB -> new HSQLDBTemplates();
      case SQLSERVER -> new SQLServer2008Templates();
      case MYSQL -> new MySQLTemplates();
      case ORACLE -> new OracleTemplates();
      case POSTGRESQL -> new PostgreSQLTemplates();
      case SQLITE -> new SQLiteTemplates();
      case TURSO -> new TursoTemplates();
      case TERADATA -> new TeradataTemplates();
      default -> throw new IllegalStateException("Unknown mode " + mode);
    };
  }

  private Mode() {}
}
