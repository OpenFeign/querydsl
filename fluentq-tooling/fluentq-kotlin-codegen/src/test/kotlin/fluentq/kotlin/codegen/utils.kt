package fluentq.kotlin.codegen

import fluentq.codegen.GeneratedAnnotationResolver

val generatedAnnotationImport = GeneratedAnnotationResolver.resolveDefault().name.replace("annotation", "`annotation`").replace("`annotation`s", "annotations")