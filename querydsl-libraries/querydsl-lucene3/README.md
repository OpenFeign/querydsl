## Querydsl Lucene 3

The Lucene module provides integration with the Lucene 3 indexing library.

**Maven integration**

 Add the following dependencies to your Maven project :

```XML
<dependency>
  <groupId>io.github.openfeign.querydsl</groupId>
  <artifactId>querydsl-lucene3</artifactId>
  <version>${querydsl.version}</version>
</dependency>
```

**Creating the query types**

With fields year and title a manually created query type could look something like this:

```JAVA 
public class QDocument extends EntityPathBase<Document>{
    private static final long serialVersionUID = -4872833626508344081L;
        
    public QDocument(String var) {
        super(Document.class, PathMetadataFactory.forVariable(var));
    }

    public final StringPath year = createString("year");
        
    public final StringPath title = createString("title");
}
```

QDocument represents a Lucene document with the fields year and title.

Code generation is not available for Lucene, since no schema data is available.

**Querying**

Querying with Querydsl Lucene is as simple as this:

```JAVA 
QDocument doc = new QDocument("doc");

IndexSearcher searcher = new IndexSearcher(index);
LuceneQuery query = new LuceneQuery(true, searcher); 
List<Document> documents = query
    .where(doc.year.between("1800", "2000").and(doc.title.startsWith("Huckle"))
    .fetch();
 ```

which is transformed into the following Lucene query :

```
+year:[1800 TO 2000] +title:huckle*
```

For more information on the Querydsl Lucene module visit the reference documentation http://www.querydsl.com/static/querydsl/latest/reference/html/ch02s05.html
