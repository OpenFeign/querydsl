package com.querydsl.sql.codegen;

import java.io.File;
import java.sql.DriverManager;

public final class Export {

  private Export() {}

  public static void main(String[] args) throws Exception {
    Class.forName("com.mysql.jdbc.Driver");
    var url = "jdbc:mysql://localhost:3306/querydsl";
    var conn = DriverManager.getConnection(url, "querydsl", "querydsl");

    var config = new MetadataExporterConfigImpl();
    config.setNamePrefix("S");
    config.setPackageName("com.querydsl.jpa.domain.sql");
    config.setTargetFolder(new File("../querydsl-jpa/src/test/java"));

    var exporter = new MetaDataExporter(config);
    exporter.export(conn.getMetaData());

    conn.close();
  }
}
