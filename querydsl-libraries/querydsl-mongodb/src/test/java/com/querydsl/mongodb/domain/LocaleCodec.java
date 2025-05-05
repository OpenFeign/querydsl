package com.querydsl.mongodb.domain;

import java.util.Locale;
import org.bson.*;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class LocaleCodec implements Codec<Locale> {

  @Override
  public void encode(BsonWriter writer, Locale value, EncoderContext encoderContext) {
    writer.writeString(value.toLanguageTag());
  }

  @Override
  public Locale decode(BsonReader reader, DecoderContext decoderContext) {
    String tag = reader.readString();
    return Locale.forLanguageTag(tag);
  }

  @Override
  public Class<Locale> getEncoderClass() {
    return Locale.class;
  }
}
