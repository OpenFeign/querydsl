package com.querydsl.mongodb.document;

import dev.morphia.Datastore;
import dev.morphia.mapping.Mapper;
import dev.morphia.mapping.codec.writer.DocumentWriter;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;

public class DocumentUtils {
  public static Document getAsDocument(Datastore morphia, Object value) {
    Mapper mapper = morphia.getMapper();
    CodecRegistry codecRegistry = morphia.getCodecRegistry();
    return DocumentWriter.encode(value, mapper, codecRegistry);
  }
}
