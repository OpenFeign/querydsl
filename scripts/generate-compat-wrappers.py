import os
import re
import shutil

# Maps sub-modules where files belong to io.github.openfeign.querydsl instead of com.querydsl
OF_MODULES = ["querydsl-jpa-spring", "querydsl-sql-json", "fluentq-jpa-spring", "fluentq-sql-json"]

def get_legacy_package_prefix(module_name):
    if module_name in OF_MODULES:
        return "io.github.openfeign.querydsl"
    return "com.querydsl"

def parse_constructors(class_name, content):
    # Clean comments
    content_clean = re.sub(r'/\*.*?\*/', '', content, flags=re.DOTALL)
    content_clean = re.sub(r'//.*?\n', '\n', content_clean)
    
    # Match: [modifiers] ClassName( [params] ) [throws ...] {
    pattern = r'(public|protected|private)?\s+' + re.escape(class_name) + r'\s*\(([^)]*)\)\s*(throws\s+[^{]+)?\s*\{'
    matches = re.finditer(pattern, content_clean)
    constructors = []
    
    def split_parameters(params_str):
        depth = 0
        parts = []
        current = []
        for char in params_str:
            if char == '<':
                depth += 1
                current.append(char)
            elif char == '>':
                depth -= 1
                current.append(char)
            elif char == ',' and depth == 0:
                parts.append("".join(current).strip())
                current = []
            else:
                current.append(char)
        if current:
            parts.append("".join(current).strip())
        return parts

    for m in matches:
        modifier = m.group(1) or ''
        params_str = m.group(2) or ''
        throws_clause = m.group(3) or ''
        
        # We only generate visible constructors (not private)
        if modifier.strip() == 'private':
            continue
            
        params = [p.strip() for p in split_parameters(params_str) if p.strip()]
        constructors.append((modifier.strip(), params, throws_clause.strip()))
    return constructors

def get_param_names(params):
    names = []
    for p in params:
        # Split by spaces, take the last token as the variable name
        parts = p.split()
        if not parts:
            continue
        name = parts[-1].strip()
        # Clean name if it has annotations or brackets, though usually the last token is the clean name.
        if name.endswith('...'):
            name = name[:-3]
        names.append(name)
    return names

def extract_generic_vars(generics_part):
    if not generics_part:
        return ""
    inner = generics_part.strip()[1:-1]
    depth = 0
    parts = []
    current = []
    for char in inner:
        if char == '<':
            depth += 1
            current.append(char)
        elif char == '>':
            depth -= 1
            current.append(char)
        elif char == ',' and depth == 0:
            parts.append("".join(current).strip())
            current = []
        else:
            current.append(char)
    if current:
        parts.append("".join(current).strip())
    var_names = []
    for p in parts:
        match = re.match(r'^\s*([A-Za-z_]\w*)', p)
        if match:
            var_names.append(match.group(1))
    return "<" + ", ".join(var_names) + ">"

def generate_java_wrapper(module_name, rel_path, file_content, fluentq_filepath):
    path_parts = rel_path.split('/')
    filename = path_parts[-1]
    class_name = filename[:-5] # remove .java
    
    fluentq_package = '.'.join(path_parts[:-1])
    
    legacy_prefix = get_legacy_package_prefix(module_name)
    legacy_package = fluentq_package.replace("fluentq", legacy_prefix)
    
    is_test = "/src/test/" in fluentq_filepath.replace('\\', '/') or "/src/apt/" in fluentq_filepath.replace('\\', '/')

    legacy_prefix_slashes = legacy_prefix.replace('.', '/')

    if filename == "package-info.java":
        compat_content = file_content.replace(fluentq_package, legacy_package)
        compat_content = compat_content.replace("package fluentq;", f"package {legacy_prefix};")
        compat_content = compat_content.replace("package fluentq.", f"package {legacy_prefix}.")
        compat_content = compat_content.replace("package fluentq\n", f"package {legacy_prefix}\n")
        compat_content = compat_content.replace("package fluentq\r\n", f"package {legacy_prefix}\r\n")
        compat_content = compat_content.replace("fluentq.jpa.spring", "io.github.openfeign.querydsl.jpa.spring")
        compat_content = compat_content.replace("fluentq.sql.json", "io.github.openfeign.querydsl.sql.json")
        compat_content = compat_content.replace("fluentq.", "com.querydsl.")
        compat_content = compat_content.replace("fluentq/jpa/spring", "io.github.openfeign.querydsl.jpa.spring")
        compat_content = compat_content.replace("fluentq/sql/json", "io.github.openfeign.querydsl.sql.json")
        compat_content = compat_content.replace("fluentq/", f"{legacy_prefix_slashes}/")
        compat_content = compat_content.replace('"com", "fluentq"', '"com", "querydsl"')
        compat_content = compat_content.replace('"fluentq"', f'"{legacy_prefix}"')
        compat_content = compat_content.replace("import fluentq.jpa.spring", "import io.github.openfeign.querydsl.jpa.spring")
        compat_content = compat_content.replace("import fluentq.sql.json", "import io.github.openfeign.querydsl.sql.json")
        compat_content = compat_content.replace("import fluentq.", "import com.querydsl.")
        compat_content = compat_content.replace("FluentQ", "Querydsl")
        compat_content = compat_content.replace("fluentq", "querydsl")
        if not is_test:
            compat_content = "@Deprecated\n" + compat_content
        return compat_content, None
        
    # Copy-paste style with deprecation
    compat_content = file_content.replace(fluentq_package, legacy_package)
    compat_content = compat_content.replace("package fluentq;", f"package {legacy_prefix};")
    compat_content = compat_content.replace("package fluentq.", f"package {legacy_prefix}.")
    compat_content = compat_content.replace("package fluentq\n", f"package {legacy_prefix}\n")
    compat_content = compat_content.replace("package fluentq\r\n", f"package {legacy_prefix}\r\n")
    compat_content = compat_content.replace("fluentq.jpa.spring", "io.github.openfeign.querydsl.jpa.spring")
    compat_content = compat_content.replace("fluentq.sql.json", "io.github.openfeign.querydsl.sql.json")
    compat_content = compat_content.replace("fluentq.", "com.querydsl.")
    compat_content = compat_content.replace("fluentq/jpa/spring", "io.github.openfeign.querydsl.jpa.spring")
    compat_content = compat_content.replace("fluentq/sql/json", "io.github.openfeign.querydsl.sql.json")
    compat_content = compat_content.replace("fluentq/", f"{legacy_prefix_slashes}/")
    compat_content = compat_content.replace('"com", "fluentq"', '"com", "querydsl"')
    compat_content = compat_content.replace('"fluentq"', f'"{legacy_prefix}"')
    compat_content = compat_content.replace("import fluentq.jpa.spring", "import io.github.openfeign.querydsl.jpa.spring")
    compat_content = compat_content.replace("import fluentq.sql.json", "import io.github.openfeign.querydsl.sql.json")
    compat_content = compat_content.replace("import fluentq.", "import com.querydsl.")
    
    # Replace FluentQ and fluentq in contents
    compat_content = compat_content.replace("FluentQ", "Querydsl")
    compat_content = compat_content.replace("fluentq", "querydsl")
    
    # Inject extends/implements for bridge interfaces
    if filename == "Expression.java" and legacy_package == "com.querydsl.core.types":
        compat_content = compat_content.replace(
            "public interface Expression<T> extends Serializable",
            "public interface Expression<T> extends Serializable, fluentq.core.types.Expression<T>"
        )
        last_brace_idx = compat_content.rfind('}')
        if last_brace_idx != -1:
            default_accept = """
  @Override
  @Nullable
  default <R, C> R accept(fluentq.core.types.Visitor<R, C> v, @Nullable C context) {
      if (v instanceof Visitor) {
          return accept((Visitor<R, C>) v, context);
      }
      return ExpressionAdapter.adapt(this).accept(v, context);
  }
"""
            compat_content = compat_content[:last_brace_idx] + default_accept + compat_content[last_brace_idx:]
            
    elif filename == "Visitor.java" and legacy_package == "com.querydsl.core.types":
        compat_content = compat_content.replace(
            "public interface Visitor<R, C>",
            "public interface Visitor<R, C> extends fluentq.core.types.Visitor<R, C>"
        )
        last_brace_idx = compat_content.rfind('}')
        if last_brace_idx != -1:
            default_visits = """
  @Override
  @Nullable
  default R visit(fluentq.core.types.Constant<?> expr, @Nullable C context) {
      return visit((Constant<?>) expr, context);
  }
  @Override
  @Nullable
  default R visit(fluentq.core.types.FactoryExpression<?> expr, @Nullable C context) {
      return visit((FactoryExpression<?>) expr, context);
  }
  @Override
  @Nullable
  default R visit(fluentq.core.types.Operation<?> expr, @Nullable C context) {
      return visit((Operation<?>) expr, context);
  }
  @Override
  @Nullable
  default R visit(fluentq.core.types.ParamExpression<?> expr, @Nullable C context) {
      return visit((ParamExpression<?>) expr, context);
  }
  @Override
  @Nullable
  default R visit(fluentq.core.types.Path<?> expr, @Nullable C context) {
      return visit((Path<?>) expr, context);
  }
  @Override
  @Nullable
  default R visit(fluentq.core.types.SubQueryExpression<?> expr, @Nullable C context) {
      return visit((SubQueryExpression<?>) expr, context);
  }
  @Override
  @Nullable
  default R visit(fluentq.core.types.TemplateExpression<?> expr, @Nullable C context) {
      return visit((TemplateExpression<?>) expr, context);
  }
"""
            compat_content = compat_content[:last_brace_idx] + default_visits + compat_content[last_brace_idx:]
            
    else:
        simple_bridges = {
            "Predicate.java": ("public interface Predicate extends Expression<Boolean>", "public interface Predicate extends Expression<Boolean>, fluentq.core.types.Predicate")
        }
        if filename in simple_bridges and legacy_package == "com.querydsl.core.types":
            target_str, replacement_str = simple_bridges[filename]
            compat_content = compat_content.replace(target_str, replacement_str)

    # Prepend @Deprecated before class/interface/enum/@interface declaration if not already present (only for main classes)
    if not is_test:
        legacy_class_name = class_name.replace("FluentQ", "Querydsl")
        decl_pattern = r'(\b(public|protected|private)?\s*(final\s+|abstract\s+)?(class|interface|enum|@interface)\s+' + re.escape(legacy_class_name) + r'\b)'
        match = re.search(decl_pattern, compat_content)
        if match:
            prefix = compat_content[max(0, match.start() - 100):match.start()]
            if "@Deprecated" not in prefix:
                compat_content = re.sub(decl_pattern, r'@Deprecated\n\1', compat_content, count=1)
        
    return compat_content, None

def process_all_files():
    fluentq_libs = "fluentq-libraries"
    fluentq_tool = "fluentq-tooling"
    
    # Map from fluentq module to querydsl module
    for root in [fluentq_libs, fluentq_tool]:
        for sub in os.listdir(root):
            subpath = os.path.join(root, sub)
            if not os.path.isdir(subpath):
                continue
                
            # Corresponding legacy module
            legacy_module = sub.replace("fluentq-", "querydsl-")
            legacy_subpath = os.path.join("querydsl/querydsl-libraries" if root == fluentq_libs else "querydsl/querydsl-tooling", legacy_module)
            
            sub_roots = [
                "src/main/java", "src/main/kotlin", "src/main/scala",
                "src/test/java", "src/test/kotlin", "src/test/scala",
                "src/apt"
            ]
            
            # Clean up target source directories first to avoid stale/renamed files
            for sr in sub_roots:
                target_dir = os.path.join(legacy_subpath, sr)
                if os.path.exists(target_dir):
                    shutil.rmtree(target_dir)
            
            # Source directory in fluentq
            for sub_root in sub_roots:
                src_dir = os.path.join(subpath, sub_root)
                if not os.path.exists(src_dir):
                    continue
                    
                print(f"Scanning source directory: {src_dir}")
                fluentq_root_dir = src_dir
                    
                for dirpath, dirnames, filenames in os.walk(fluentq_root_dir):
                    for filename in filenames:
                        # Full path to fluentq file
                        fluentq_filepath = os.path.join(dirpath, filename)
                        
                        # Relative path from src_dir (starts with fluentq/...)
                        rel_path = os.path.relpath(fluentq_filepath, src_dir)
                        
                        # Compute legacy target path
                        legacy_prefix = get_legacy_package_prefix(legacy_module)
                        # Replace 'fluentq/' with 'com/querydsl/' or 'io/github/openfeign/querydsl/'
                        legacy_rel_path = rel_path.replace("fluentq", legacy_prefix.replace('.', '/'))
                        # Also replace 'FluentQ' with 'Querydsl' in the filename/path
                        legacy_rel_path = legacy_rel_path.replace("FluentQ", "Querydsl")
                        
                        # Target filepath in querydsl module
                        target_sub_root = sub_root
                        legacy_filepath = os.path.join(legacy_subpath, target_sub_root, legacy_rel_path)
                        
                        # Create target parent dir
                        os.makedirs(os.path.dirname(legacy_filepath), exist_ok=True)
                        
                        # Read fluentq file content
                        with open(fluentq_filepath, "r", encoding="utf-8") as f:
                            file_content = f.read()
                            
                        # Generate compat wrapper
                        if filename.endswith(".java"):
                            compat_content, modified_fluentq_content = generate_java_wrapper(legacy_module, rel_path, file_content, fluentq_filepath)
                            if modified_fluentq_content is not None:
                                with open(fluentq_filepath, "w", encoding="utf-8") as f:
                                    f.write(modified_fluentq_content)
                                print(f"Removed final from {fluentq_filepath}")
                        elif filename.endswith(".kt") or filename.endswith(".scala"):
                            # Copy content, replace packages
                            legacy_package = '.'.join(os.path.dirname(legacy_rel_path).split('/'))
                            fluentq_package = '.'.join(os.path.dirname(rel_path).split('/'))
                            
                            legacy_prefix_slashes = legacy_prefix.replace('.', '/')
                            
                            compat_content = file_content.replace(fluentq_package, legacy_package)
                            compat_content = compat_content.replace("package fluentq;", f"package {legacy_prefix};")
                            compat_content = compat_content.replace("package fluentq.", f"package {legacy_prefix}.")
                            compat_content = compat_content.replace("package fluentq\n", f"package {legacy_prefix}\n")
                            compat_content = compat_content.replace("package fluentq\r\n", f"package {legacy_prefix}\r\n")
                            compat_content = compat_content.replace("fluentq.jpa.spring", "io.github.openfeign.querydsl.jpa.spring")
                            compat_content = compat_content.replace("fluentq.sql.json", "io.github.openfeign.querydsl.sql.json")
                            compat_content = compat_content.replace("fluentq.", "com.querydsl.")
                            compat_content = compat_content.replace("fluentq/jpa/spring", "io.github.openfeign.querydsl.jpa.spring")
                            compat_content = compat_content.replace("fluentq/sql/json", "io.github.openfeign.querydsl.sql.json")
                            compat_content = compat_content.replace("fluentq/", f"{legacy_prefix_slashes}/")
                            compat_content = compat_content.replace('"com", "fluentq"', '"com", "querydsl"')
                            compat_content = compat_content.replace('"fluentq"', f'"{legacy_prefix}"')
                            compat_content = compat_content.replace("import fluentq.jpa.spring", "import io.github.openfeign.querydsl.jpa.spring")
                            compat_content = compat_content.replace("import fluentq.sql.json", "import io.github.openfeign.querydsl.sql.json")
                            compat_content = compat_content.replace("import fluentq.", "import com.querydsl.")
                            
                            # Replace FluentQ and fluentq in contents
                            compat_content = compat_content.replace("FluentQ", "Querydsl")
                            compat_content = compat_content.replace("fluentq", "querydsl")
                        else:
                            # Other files, copy and replace packages
                            legacy_prefix_slashes = legacy_prefix.replace('.', '/')
                            compat_content = file_content.replace("package fluentq;", f"package {legacy_prefix};")
                            compat_content = compat_content.replace("package fluentq.", f"package {legacy_prefix}.")
                            compat_content = compat_content.replace("package fluentq\n", f"package {legacy_prefix}\n")
                            compat_content = compat_content.replace("package fluentq\r\n", f"package {legacy_prefix}\r\n")
                            compat_content = compat_content.replace("fluentq.jpa.spring", "io.github.openfeign.querydsl.jpa.spring")
                            compat_content = compat_content.replace("fluentq.sql.json", "io.github.openfeign.querydsl.sql.json")
                            compat_content = compat_content.replace("fluentq.", "com.querydsl.")
                            compat_content = compat_content.replace("fluentq/jpa/spring", "io.github.openfeign.querydsl.jpa.spring")
                            compat_content = compat_content.replace("fluentq/sql/json", "io.github.openfeign.querydsl.sql.json")
                            compat_content = compat_content.replace("fluentq/", f"{legacy_prefix_slashes}/")
                            compat_content = compat_content.replace('"com", "fluentq"', '"com", "querydsl"')
                            compat_content = compat_content.replace('"fluentq"', f'"{legacy_prefix}"')
                            compat_content = compat_content.replace("fluentq", "querydsl")
                            compat_content = compat_content.replace("FluentQ", "Querydsl")
                            
                        # Write target legacy file
                        with open(legacy_filepath, "w", encoding="utf-8") as f:
                            f.write(compat_content)

    # Write ExpressionAdapter.java
    adapter_content = """package com.querydsl.core.types;

import java.lang.reflect.AnnotatedElement;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import org.jetbrains.annotations.Nullable;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.QueryModifiers;
import com.querydsl.core.JoinExpression;
import com.querydsl.core.JoinFlag;
import com.querydsl.core.JoinType;
import com.querydsl.core.QueryFlag;

public final class ExpressionAdapter {

    @SuppressWarnings("unchecked")
    public static <T> fluentq.core.types.Expression<T> adapt(Expression<T> expr) {
        if (expr == null) {
            return null;
        }
        if (expr instanceof fluentq.core.types.Expression) {
            return (fluentq.core.types.Expression<T>) expr;
        }
        if (expr instanceof Path) {
            return (fluentq.core.types.Expression<T>) new PathAdapter<>((Path<T>) expr);
        }
        if (expr instanceof Constant) {
            return (fluentq.core.types.Expression<T>) new ConstantAdapter<>((Constant<T>) expr);
        }
        if (expr instanceof Operation) {
            return (fluentq.core.types.Expression<T>) new OperationAdapter<>((Operation<T>) expr);
        }
        if (expr instanceof TemplateExpression) {
            return (fluentq.core.types.Expression<T>) new TemplateExpressionAdapter<>((TemplateExpression<T>) expr);
        }
        if (expr instanceof FactoryExpression) {
            return (fluentq.core.types.Expression<T>) new FactoryExpressionAdapter<>((FactoryExpression<T>) expr);
        }
        if (expr instanceof SubQueryExpression) {
            return (fluentq.core.types.Expression<T>) new SubQueryExpressionAdapter<>((SubQueryExpression<T>) expr);
        }
        if (expr instanceof ParamExpression) {
            return (fluentq.core.types.Expression<T>) new ParamExpressionAdapter<>((ParamExpression<T>) expr);
        }
        
        // Fallback generic expression wrapper
        return new GenericExpressionAdapter<>(expr);
    }

    public static Object adaptObject(Object arg) {
        if (arg instanceof Expression) {
            return adapt((Expression<?>) arg);
        }
        return arg;
    }

    private static class AdaptedOperator implements fluentq.core.types.Operator {
        private final String name;
        private final Class<?> type;

        AdaptedOperator(String name, Class<?> type) {
            this.name = name;
            this.type = type;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public Class<?> getType() {
            return type;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof fluentq.core.types.Operator)) return false;
            return name.equals(((fluentq.core.types.Operator) obj).name());
        }
        
        @Override
        public int hashCode() {
            return name.hashCode();
        }
    }

    private static fluentq.core.QueryModifiers adaptModifiers(QueryModifiers modifiers) {
        if (modifiers == null) return null;
        return new fluentq.core.QueryModifiers(modifiers.getLimit(), modifiers.getOffset());
    }

    private static fluentq.core.JoinType adaptJoinType(JoinType type) {
        if (type == null) return null;
        return fluentq.core.JoinType.valueOf(type.name());
    }

    private static fluentq.core.JoinFlag.Position adaptJoinFlagPosition(JoinFlag.Position pos) {
        if (pos == null) return null;
        return fluentq.core.JoinFlag.Position.valueOf(pos.name());
    }

    private static fluentq.core.QueryFlag.Position adaptQueryFlagPosition(QueryFlag.Position pos) {
        if (pos == null) return null;
        return fluentq.core.QueryFlag.Position.valueOf(pos.name());
    }

    private static fluentq.core.types.OrderSpecifier<?> adaptOrderSpecifier(OrderSpecifier<?> spec) {
        if (spec == null) return null;
        return new fluentq.core.types.OrderSpecifier<>(
            adaptOrder(spec.getOrder()),
            adapt(spec.getTarget()),
            adaptNullHandling(spec.getNullHandling())
        );
    }
    
    private static fluentq.core.types.Order adaptOrder(Order order) {
        if (order == null) return null;
        return fluentq.core.types.Order.valueOf(order.name());
    }
    
    private static fluentq.core.types.OrderSpecifier.NullHandling adaptNullHandling(OrderSpecifier.NullHandling nh) {
        if (nh == null) return null;
        return fluentq.core.types.OrderSpecifier.NullHandling.valueOf(nh.name());
    }

    @SuppressWarnings("unchecked")
    public static fluentq.core.QueryMetadata adaptQueryMetadata(QueryMetadata meta) {
        if (meta == null) return null;
        fluentq.core.DefaultQueryMetadata result = new fluentq.core.DefaultQueryMetadata();
        
        if (meta.getWhere() != null) {
            result.addWhere((fluentq.core.types.Predicate) adapt(meta.getWhere()));
        }
        for (Expression<?> g : meta.getGroupBy()) {
            result.addGroupBy(adapt(g));
        }
        if (meta.getHaving() != null) {
            result.addHaving((fluentq.core.types.Predicate) adapt(meta.getHaving()));
        }
        for (OrderSpecifier<?> o : meta.getOrderBy()) {
            result.addOrderBy(adaptOrderSpecifier(o));
        }
        if (meta.getProjection() != null) {
            result.setProjection(adapt(meta.getProjection()));
        }
        result.setModifiers(adaptModifiers(meta.getModifiers()));
        
        for (JoinExpression join : meta.getJoins()) {
            result.addJoin(adaptJoinType(join.getType()), adapt(join.getTarget()));
            for (JoinFlag flag : join.getFlags()) {
                result.addJoinFlag(new fluentq.core.JoinFlag(adapt(flag.getFlag()), adaptJoinFlagPosition(flag.getPosition())));
            }
        }
        for (QueryFlag flag : meta.getFlags()) {
            result.addFlag(new fluentq.core.QueryFlag(adaptQueryFlagPosition(flag.getPosition()), adapt(flag.getFlag())));
        }
        
        for (Map.Entry<ParamExpression<?>, ?> entry : meta.getParams().entrySet()) {
            result.setParam((fluentq.core.types.ParamExpression) adapt(entry.getKey()), entry.getValue());
        }
        
        result.setDistinct(meta.isDistinct());
        result.setUnique(meta.isUnique());
        
        return result;
    }

    private static class GenericExpressionAdapter<T> implements fluentq.core.types.Expression<T> {
        protected final Expression<T> delegate;
        
        GenericExpressionAdapter(Expression<T> delegate) {
            this.delegate = delegate;
        }

        @Override
        public Class<? extends T> getType() {
            return delegate.getType();
        }

        @Override
        @SuppressWarnings("unchecked")
        public <R, C> R accept(fluentq.core.types.Visitor<R, C> v, @Nullable C context) {
            if (v instanceof Visitor) {
                return delegate.accept((Visitor<R, C>) v, context);
            }
            throw new UnsupportedOperationException("Unknown expression type: " + delegate.getClass().getName());
        }
        
        @Override
        public String toString() {
            return delegate.toString();
        }
    }

    private static class PathAdapter<T> extends GenericExpressionAdapter<T> implements fluentq.core.types.Path<T> {
        private final Path<T> path;

        PathAdapter(Path<T> path) {
            super(path);
            this.path = path;
        }

        @Override
        public fluentq.core.types.PathMetadata getMetadata() {
            PathMetadata meta = path.getMetadata();
            if (meta == null) return null;
            return new fluentq.core.types.PathMetadata(
                (fluentq.core.types.Path<?>) adapt(meta.getParent()),
                meta.getElement(),
                adaptPathType(meta.getPathType())
            );
        }

        @Override
        public AnnotatedElement getAnnotatedElement() {
            return path.getAnnotatedElement();
        }

        @Override
        public fluentq.core.types.Path<?> getRoot() {
            return (fluentq.core.types.Path<?>) adapt(path.getRoot());
        }

        @Override
        public <R, C> R accept(fluentq.core.types.Visitor<R, C> v, @Nullable C context) {
            return v.visit(this, context);
        }
    }

    private static class ConstantAdapter<T> extends GenericExpressionAdapter<T> implements fluentq.core.types.Constant<T> {
        private final Constant<T> constant;

        ConstantAdapter(Constant<T> constant) {
            super(constant);
            this.constant = constant;
        }

        @Override
        public T getConstant() {
            return constant.getConstant();
        }

        @Override
        public <R, C> R accept(fluentq.core.types.Visitor<R, C> v, @Nullable C context) {
            return v.visit(this, context);
        }
    }

    private static class OperationAdapter<T> extends GenericExpressionAdapter<T> implements fluentq.core.types.Operation<T> {
        private final Operation<T> op;

        OperationAdapter(Operation<T> op) {
            super(op);
            this.op = op;
        }

        @Override
        @SuppressWarnings("unchecked")
        public List<fluentq.core.types.Expression<?>> getArgs() {
            List<fluentq.core.types.Expression<?>> result = new ArrayList<>();
            for (Expression<?> arg : op.getArgs()) {
                result.add((fluentq.core.types.Expression<?>) adapt((Expression) arg));
            }
            return result;
        }

        @Override
        @SuppressWarnings("unchecked")
        public fluentq.core.types.Expression<?> getArg(int index) {
            return adapt((Expression) op.getArg(index));
        }

        @Override
        public fluentq.core.types.Operator getOperator() {
            Operator operator = op.getOperator();
            if (operator == null) return null;
            return new AdaptedOperator(operator.name(), operator.getType());
        }

        @Override
        public <R, C> R accept(fluentq.core.types.Visitor<R, C> v, @Nullable C context) {
            return v.visit(this, context);
        }
    }

    private static class TemplateExpressionAdapter<T> extends GenericExpressionAdapter<T> implements fluentq.core.types.TemplateExpression<T> {
        private final TemplateExpression<T> template;

        TemplateExpressionAdapter(TemplateExpression<T> template) {
            super(template);
            this.template = template;
        }

        @Override
        public List<?> getArgs() {
            List<Object> result = new ArrayList<>();
            for (Object arg : template.getArgs()) {
                result.add(adaptObject(arg));
            }
            return result;
        }

        @Override
        public Object getArg(int index) {
            return adaptObject(template.getArg(index));
        }

        @Override
        public fluentq.core.types.Template getTemplate() {
            Template t = template.getTemplate();
            if (t == null) return null;
            return fluentq.core.types.TemplateFactory.DEFAULT.create(t.toString());
        }

        @Override
        public <R, C> R accept(fluentq.core.types.Visitor<R, C> v, @Nullable C context) {
            return v.visit(this, context);
        }
    }

    private static class FactoryExpressionAdapter<T> extends GenericExpressionAdapter<T> implements fluentq.core.types.FactoryExpression<T> {
        private final FactoryExpression<T> factory;

        FactoryExpressionAdapter(FactoryExpression<T> factory) {
            super(factory);
            this.factory = factory;
        }

        @Override
        @SuppressWarnings("unchecked")
        public List<fluentq.core.types.Expression<?>> getArgs() {
            List<fluentq.core.types.Expression<?>> result = new ArrayList<>();
            for (Expression<?> arg : factory.getArgs()) {
                result.add((fluentq.core.types.Expression<?>) adapt((Expression) arg));
            }
            return result;
        }

        @Override
        @Nullable
        public T newInstance(Object... args) {
            return factory.newInstance(args);
        }

        @Override
        public <R, C> R accept(fluentq.core.types.Visitor<R, C> v, @Nullable C context) {
            return v.visit(this, context);
        }
    }

    private static class SubQueryExpressionAdapter<T> extends GenericExpressionAdapter<T> implements fluentq.core.types.SubQueryExpression<T> {
        private final SubQueryExpression<T> sub;

        SubQueryExpressionAdapter(SubQueryExpression<T> sub) {
            super(sub);
            this.sub = sub;
        }

        @Override
        public fluentq.core.QueryMetadata getMetadata() {
            return adaptQueryMetadata(sub.getMetadata());
        }

        @Override
        public <R, C> R accept(fluentq.core.types.Visitor<R, C> v, @Nullable C context) {
            return v.visit(this, context);
        }
    }

    private static class ParamExpressionAdapter<T> extends GenericExpressionAdapter<T> implements fluentq.core.types.ParamExpression<T> {
        private final ParamExpression<T> param;

        ParamExpressionAdapter(ParamExpression<T> param) {
            super(param);
            this.param = param;
        }

        @Override
        public String getName() {
            return param.getName();
        }

        @Override
        public boolean isAnon() {
            return param.isAnon();
        }

        @Override
        public String getNotSetMessage() {
            return param.getNotSetMessage();
        }

        @Override
        public <R, C> R accept(fluentq.core.types.Visitor<R, C> v, @Nullable C context) {
            return v.visit(this, context);
        }
    }

    private static fluentq.core.types.PathType adaptPathType(PathType type) {
        if (type == null) return null;
        try {
            return fluentq.core.types.PathType.valueOf(type.name());
        } catch (Exception e) {
            return null;
        }
    }
}
"""
    adapter_dir = "querydsl/querydsl-libraries/querydsl-core/src/main/java/com/querydsl/core/types"
    if os.path.exists(adapter_dir):
        with open(os.path.join(adapter_dir, "ExpressionAdapter.java"), "w", encoding="utf-8") as f:
            f.write(adapter_content)

if __name__ == "__main__":
    process_all_files()
    print("All compatibility wrappers generated successfully!")
