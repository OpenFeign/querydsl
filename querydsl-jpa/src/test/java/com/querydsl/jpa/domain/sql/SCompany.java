package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.*;
import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SCompany is a Querydsl query type for SCompany */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SCompany extends com.querydsl.sql.RelationalPathBase<SCompany> {

  private static final long serialVersionUID = -434698507;

  public static final SCompany company = new SCompany("company");

  public final NumberPath<Integer> id = createNumber("id", Integer.class);

  public final StringPath name = createString("name");

  public final StringPath officialName = createString("officialName");

  public final NumberPath<Integer> ratingordinal = createNumber("ratingordinal", Integer.class);

  public final StringPath ratingstring = createString("ratingstring");

  public final NumberPath<Integer> ceoId = createNumber("ceoId", Integer.class);

  public final com.querydsl.sql.PrimaryKey<SCompany> primary = createPrimaryKey(id);

  public final com.querydsl.sql.ForeignKey<SEmployee_> companyCEOIDFK =
      createForeignKey(ceoId, "ID");

  public final com.querydsl.sql.ForeignKey<SCompanyDepartment_> _companyDepartment_CompanyIDFK =
      createInvForeignKey(id, "Company_ID");

  public SCompany(String variable) {
    super(SCompany.class, forVariable(variable), "null", "company");
    addMetadata();
  }

  public SCompany(String variable, String schema, String table) {
    super(SCompany.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SCompany(String variable, String schema) {
    super(SCompany.class, forVariable(variable), schema, "company");
    addMetadata();
  }

  public SCompany(Path<? extends SCompany> path) {
    super(path.getType(), path.getMetadata(), "null", "company");
    addMetadata();
  }

  public SCompany(PathMetadata metadata) {
    super(SCompany.class, metadata, "null", "company");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.INTEGER).withSize(10).notNull());
    addMetadata(
        name, ColumnMetadata.named("NAME").withIndex(2).ofType(Types.VARCHAR).withSize(255));
    addMetadata(
        officialName,
        ColumnMetadata.named("official_name").withIndex(3).ofType(Types.VARCHAR).withSize(255));
    addMetadata(
        ratingordinal,
        ColumnMetadata.named("RATINGORDINAL").withIndex(4).ofType(Types.INTEGER).withSize(10));
    addMetadata(
        ratingstring,
        ColumnMetadata.named("RATINGSTRING").withIndex(5).ofType(Types.VARCHAR).withSize(255));
    addMetadata(
        ceoId, ColumnMetadata.named("CEO_ID").withIndex(6).ofType(Types.INTEGER).withSize(10));
  }
}
