package com.querydsl.jpa.hibernate;

import com.querydsl.core.JoinExpression;
import com.querydsl.core.QueryFlag;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Operator;
import com.querydsl.core.types.Path;
import com.querydsl.jpa.JPQLSerializer;
import com.querydsl.jpa.JPQLTemplates;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jetbrains.annotations.Nullable;

public class HQLSerializer extends JPQLSerializer {

  protected final Set<Path<?>> withAliases = new HashSet<>();

  public HQLSerializer(JPQLTemplates templates) {
    super(templates);
  }

  public HQLSerializer(JPQLTemplates templates, EntityManager em) {
    super(templates, em);
  }

  @Override
  public void serialize(QueryMetadata metadata, boolean forCountRow, @Nullable String projection) {
    final Set<QueryFlag> flags = metadata.getFlags();
    final var hasFlags = !flags.isEmpty();

    if (hasFlags) {
      List<Expression<?>> withFlags = new ArrayList<>();
      for (QueryFlag flag : flags) {
        if (flag.getPosition() == QueryFlag.Position.WITH) {
          withFlags.add(flag.getFlag());
        }
      }
      if (!withFlags.isEmpty()) {
        append("with\n");
        handle(",\n", withFlags);
        append("\n");
      }
    }

    super.serialize(metadata, forCountRow, projection);
  }

  @Override
  protected void visitOperation(
      Class<?> type, Operator operator, List<? extends Expression<?>> args) {
    if (operator == HQLOps.WITH && args.size() == 3 && args.get(0) instanceof Path<?> alias) {
      handle(alias);
      withAliases.add(alias);
      append(" as ");
      if (args.get(1) instanceof ConstantImpl<?> materializedParam
          && materializedParam.getConstant() instanceof Boolean materialized) {
        if (!materialized) {
          append("not ");
        }
        append("materialized ");
      }
      handle(args.get(2));
    } else {
      super.visitOperation(type, operator, args);
    }
  }

  @Override
  protected void handleJoinTarget(JoinExpression je) {
    if (je.getTarget() instanceof Path<?> pe && withAliases.contains(pe)) {
      append(pe.getMetadata().getName()).append(" ");
      handle(je.getTarget());
    } else {
      super.handleJoinTarget(je);
    }
  }
}
