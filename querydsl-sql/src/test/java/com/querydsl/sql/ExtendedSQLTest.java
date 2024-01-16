package com.querydsl.sql;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.PathMetadataFactory;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.DatePath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.sql.mysql.MySQLQuery;
import java.util.Date;
import org.junit.Test;

public class ExtendedSQLTest {

  public static class QAuthor extends RelationalPathBase<QAuthor> {

    private static final long serialVersionUID = -512402580246687292L;

    public static final QAuthor author = new QAuthor("author");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath firstName = createString("firstName");

    public final StringPath lastName = createString("lastName");

    public QAuthor(String variable) {
      super(QAuthor.class, PathMetadataFactory.forVariable(variable), "", "AUTHOR");
      addMetadata();
    }

    protected void addMetadata() {
      addMetadata(id, ColumnMetadata.named("ID"));
      addMetadata(firstName, ColumnMetadata.named("FIRST_NAME"));
      addMetadata(lastName, ColumnMetadata.named("LAST_NAME"));
    }
  }

  public static class QBook extends RelationalPathBase<QBook> {

    private static final long serialVersionUID = 4842689279054229095L;

    public static final QBook book = new QBook("book");

    public final NumberPath<Integer> authorId = createNumber("authorId", Integer.class);

    public final StringPath language = createString("language");

    public final DatePath<Date> published = createDate("published", Date.class);

    public QBook(String variable) {
      super(QBook.class, PathMetadataFactory.forVariable(variable), "", "BOOK");
      addMetadata();
    }

    protected void addMetadata() {
      addMetadata(authorId, ColumnMetadata.named("AUTHOR_ID"));
      addMetadata(language, ColumnMetadata.named("LANGUAGE"));
      addMetadata(published, ColumnMetadata.named("PUBLISHED"));
    }
  }

  @Test
  public void test() {
    //        SELECT FIRST_NAME, LAST_NAME, COUNT(*)
    //        FROM AUTHOR
    //        JOIN BOOK ON AUTHOR.ID = BOOK.AUTHOR_ID
    //       WHERE LANGUAGE = 'DE'
    //         AND PUBLISHED > '2008-01-01'
    //    GROUP BY FIRST_NAME, LAST_NAME
    //      HAVING COUNT(*) > 5
    //    ORDER BY LAST_NAME ASC NULLS FIRST
    //       LIMIT 2
    //      OFFSET 1
    //         FOR UPDATE
    //          OF FIRST_NAME, LAST_NAME

    QAuthor author = QAuthor.author;
    QBook book = QBook.book;
    MySQLQuery<?> query = new MySQLQuery<Void>(null);
    query
        .from(author)
        .join(book)
        .on(author.id.eq(book.authorId))
        .where(book.language.eq("DE"), book.published.eq(new Date()))
        .groupBy(author.firstName, author.lastName)
        .having(Wildcard.count.gt(5))
        .orderBy(author.lastName.asc())
        .limit(2)
        .offset(1)
        .forUpdate();
    // of(author.firstName, author.lastName)

    query
        .getMetadata()
        .setProjection(Projections.tuple(author.firstName, author.lastName, Wildcard.count));

    SQLSerializer serializer = new SQLSerializer(new Configuration(new MySQLTemplates()));
    serializer.serialize(query.getMetadata(), false);

    assertThat(serializer.toString())
        .isEqualTo(
            """
            select author.FIRST_NAME, author.LAST_NAME, count(*)
            from AUTHOR author
            join BOOK book
            on author.ID = book.AUTHOR_ID
            where book.LANGUAGE = ? and book.PUBLISHED = ?
            group by author.FIRST_NAME, author.LAST_NAME
            having count(*) > ?
            order by author.LAST_NAME asc
            limit ?
            offset ?
            for update\
            """);
  }
}
