package fluentq.sql.codegen;

import java.io.File;
import java.sql.DriverManager;

public final class Export {

  private Export() {}

  public static void main(String[] args) throws Exception {
    Class.forName("com.mysql.jdbc.Driver");
    var url = "jdbc:mysql://localhost:3306/fluentq";
    var conn = DriverManager.getConnection(url, "fluentq", "fluentq");

    var config = new MetadataExporterConfigImpl();
    config.setNamePrefix("S");
    config.setPackageName("fluentq.jpa.domain.sql");
    config.setTargetFolder(new File("../fluentq-jpa/src/test/java"));

    var exporter = new MetaDataExporter(config);
    exporter.export(conn.getMetaData());

    conn.close();
  }
}
