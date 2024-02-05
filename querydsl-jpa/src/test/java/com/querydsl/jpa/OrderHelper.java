package com.querydsl.jpa;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.dsl.PathBuilder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;

public final class OrderHelper {

  private OrderHelper() {}

  private static final Pattern DOT = Pattern.compile("\\.");

  @SuppressWarnings("unchecked")
  public static PathBuilder<?> join(
      JPQLQuery<?> query, PathBuilder<?> builder, Map<String, PathBuilder<?>> joins, String path) {
    PathBuilder<?> rv = joins.get(path);
    if (rv == null) {
      if (path.contains(".")) {
        String[] tokens = DOT.split(path);
        String[] parent = new String[tokens.length - 1];
        System.arraycopy(tokens, 0, parent, 0, tokens.length - 1);
        String parentKey = StringUtils.join(parent, ".");
        builder = join(query, builder, joins, parentKey);
        rv = new PathBuilder(Object.class, StringUtils.join(tokens, "_"));
        query.leftJoin((EntityPath) builder.get(tokens[tokens.length - 1]), rv);
      } else {
        rv = new PathBuilder(Object.class, path);
        query.leftJoin((EntityPath) builder.get(path), rv);
      }
      joins.put(path, rv);
    }
    return rv;
  }

  @SuppressWarnings("unchecked")
  public static PathBuilder<?> join(
      JPQLSubQuery<?> query,
      PathBuilder<?> builder,
      Map<String, PathBuilder<?>> joins,
      String path) {
    PathBuilder<?> rv = joins.get(path);
    if (rv == null) {
      if (path.contains(".")) {
        String[] tokens = DOT.split(path);
        String[] parent = new String[tokens.length - 1];
        System.arraycopy(tokens, 0, parent, 0, tokens.length - 1);
        String parentKey = StringUtils.join(parent, ".");
        builder = join(query, builder, joins, parentKey);
        rv = new PathBuilder(Object.class, StringUtils.join(tokens, "_"));
        query.leftJoin((EntityPath) builder.get(tokens[tokens.length - 1]), rv);
      } else {
        rv = new PathBuilder(Object.class, path);
        query.leftJoin((EntityPath) builder.get(path), rv);
      }
      joins.put(path, rv);
    }
    return rv;
  }

  @SuppressWarnings("unchecked")
  public static void orderBy(JPQLQuery<?> query, EntityPath<?> entity, List<String> order) {
    PathBuilder<?> builder = new PathBuilder(entity.getType(), entity.getMetadata());
    Map<String, PathBuilder<?>> joins = new HashMap<>();

    for (String entry : order) {
      String[] tokens = DOT.split(entry);
      if (tokens.length > 1) {
        String[] parent = new String[tokens.length - 1];
        System.arraycopy(tokens, 0, parent, 0, tokens.length - 1);
        PathBuilder<?> parentAlias = join(query, builder, joins, StringUtils.join(parent, "."));
        query.orderBy(parentAlias.getString(tokens[tokens.length - 1]).asc());
      } else {
        query.orderBy(builder.getString(tokens[0]).asc());
      }
    }
  }

  @SuppressWarnings("unchecked")
  public static void orderBy(JPQLSubQuery<?> query, EntityPath<?> entity, List<String> order) {
    PathBuilder<?> builder = new PathBuilder(entity.getType(), entity.getMetadata());
    Map<String, PathBuilder<?>> joins = new HashMap<>();

    for (String entry : order) {
      String[] tokens = DOT.split(entry);
      if (tokens.length > 1) {
        String[] parent = new String[tokens.length - 1];
        System.arraycopy(tokens, 0, parent, 0, tokens.length - 1);
        PathBuilder<?> parentAlias = join(query, builder, joins, StringUtils.join(parent, "."));
        query.orderBy(parentAlias.getString(tokens[tokens.length - 1]).asc());
      } else {
        query.orderBy(builder.getString(tokens[0]).asc());
      }
    }
  }
}
