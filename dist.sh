#!/bin/sh
rm -rf target/dist
mkdir -p target/dist
mvn javadoc:aggregate

for module in apt collections hibernate-search jpa jdo lucene3 lucene4 sql sql-codegen
do
  mvn -pl querydsl-$module -Dtest=X clean assembly:assembly
done

