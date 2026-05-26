/*
 * Copyright 2015, The FluentQ Team (http://www.fluentq.com/team)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fluentq.spatial.locationtech.jts;

import fluentq.core.types.Expression;
import fluentq.core.types.ExpressionUtils;
import fluentq.core.types.Operation;
import fluentq.core.types.OperationImpl;
import fluentq.core.types.Operator;
import fluentq.core.types.Visitor;
import java.util.Arrays;
import java.util.List;
import org.locationtech.jts.geom.Polygon;

/**
 * {@code JTSPolygonOperation} extends {@link JTSPolygonExpression} to implement the {@link
 * Operation} interface
 *
 * @author tiwe
 * @param <T>
 */
public class JTSPolygonOperation<T extends Polygon> extends JTSPolygonExpression<T>
    implements Operation<T> {

  private static final long serialVersionUID = 3433471874808633698L;

  private final OperationImpl<T> opMixin;

  protected JTSPolygonOperation(Class<? extends T> type, Operator op, Expression<?>... args) {
    this(type, op, Arrays.asList(args));
  }

  protected JTSPolygonOperation(Class<? extends T> type, Operator op, List<Expression<?>> args) {
    super(ExpressionUtils.operation(type, op, args));
    this.opMixin = (OperationImpl<T>) mixin;
  }

  @Override
  public final <R, C> R accept(Visitor<R, C> v, C context) {
    return v.visit(opMixin, context);
  }

  @Override
  public Expression<?> getArg(int index) {
    return opMixin.getArg(index);
  }

  @Override
  public List<Expression<?>> getArgs() {
    return opMixin.getArgs();
  }

  @Override
  public Operator getOperator() {
    return opMixin.getOperator();
  }
}
