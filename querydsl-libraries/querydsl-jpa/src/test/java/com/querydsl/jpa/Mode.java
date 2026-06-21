package com.querydsl.jpa;

import com.querydsl.core.Target;
import com.querydsl.sql.CUBRIDTemplates;
import com.querydsl.sql.DerbyTemplates;
import com.querydsl.sql.H2Templates;
import com.querydsl.sql.HSQLDBTemplates;
import com.querydsl.sql.MySQLTemplates;
import com.querydsl.sql.OracleTemplates;
import com.querydsl.sql.PostgreSQLTemplates;
import com.querydsl.sql.SQLServer2008Templates;
import com.querydsl.sql.SQLTemplates;
import com.querydsl.sql.SQLiteTemplates;
import com.querydsl.sql.TeradataTemplates;
import com.querydsl.sql.TursoTemplates;

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
