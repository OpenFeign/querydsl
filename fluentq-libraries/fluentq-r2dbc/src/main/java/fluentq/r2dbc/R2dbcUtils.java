package fluentq.r2dbc;

import fluentq.r2dbc.binding.BindMarkers;
import java.util.List;

/** R2dbcUtils */
public final class R2dbcUtils {

  private R2dbcUtils() {}

  public static String replaceBindingArguments(
      BindMarkers bindMarkers, List<Object> objects, String originalSql) {
    var sql = originalSql;
    for (Object o : objects) {
      var index = sql.indexOf('?');
      if (index == -1) {
        break;
      }
      var next = bindMarkers.next();
      var first = sql.substring(0, index);
      var second = sql.substring(index + 1);
      sql = first + next.getPlaceholder() + second;
    }
    return sql;
  }
}
