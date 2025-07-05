/*
 * Copyright 2010, Mysema Ltd
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
package com.querydsl.codegen.utils;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.tools.JavaFileManager;

import com.google.common.collect.MapMaker;

/**
 * @author tiwe
 */
public final class MemFileSystemRegistry {

  public static final MemFileSystemRegistry DEFAULT = new MemFileSystemRegistry();

  private final ConcurrentMap<JavaFileManager, String> jfm2prefix = new MapMaker().weakKeys().makeMap();

  private ConcurrentMap<String, WeakReference<JavaFileManager>> prefix2jfm = new MapMaker().weakValues().makeMap();

  private final String protocolName;

  private final AtomicInteger sequence = new AtomicInteger();

  private MemFileSystemRegistry() {
    var pkgName = MemFileSystemRegistry.class.getPackage().getName();
    protocolName = pkgName.substring(pkgName.lastIndexOf('.') + 1);
    var pkgs = System.getProperty("java.protocol.handler.pkgs");
    var parentPackage = pkgName.substring(0, pkgName.lastIndexOf('.'));
    pkgs = pkgs == null ? parentPackage : pkgs + "|" + parentPackage;
    System.setProperty("java.protocol.handler.pkgs", pkgs);
  }

  public JavaFileManager getFileSystem(URL url) {
    var prefix = url.getProtocol() + "://" + url.getHost() + "/";
    if (prefix2jfm.containsKey(prefix)) {
      return prefix2jfm.get(prefix).get();
    } else {
      return null;
    }
  }

  public String getUrlPrefix(JavaFileManager jfm) {
    return jfm2prefix.computeIfAbsent(
        jfm,
        key -> {
          var result = protocolName + "://jfm" + sequence.getAndIncrement() + "/";
          prefix2jfm.put(result, new WeakReference<>(key));
          return result;
        });
  }
}
