/*
 * Copyright 2011, Mysema Ltd
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
package com.querydsl.codegen.utils.support;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Cat {

  private int breed;

  private java.sql.Date dateField;

  public enum Color {
    BLUE,
    GREEN,
    BROWN
  }

  private Color eyecolor;

  private List<Cat> kittens;

  private Cat[] kittenArray;

  private Map<String, Cat> kittensByName;

  private Cat mate;

  private String stringAsSimple;

  private java.sql.Time timeField;

  private String name;

  public void setName(String name) {
    this.name = name;
  }

  private Date birthdate;
  private int id;

  public Cat() {
    this.kittensByName = Collections.emptyMap();
  }

  public Cat(String name) {
    Cat kitten = new Cat();
    this.kittens = Arrays.asList(kitten);
    this.kittenArray = new Cat[] {kitten};
    this.kittensByName = Collections.singletonMap("Kitty", kitten);
    this.name = name;
  }

  public Cat(String name, String kittenName) {
    this(name);
    kittens.getFirst().setName(kittenName);
  }

  public Cat(String name, int id) {
    this(name);
    this.id = id;
  }

  public Cat(String name, int id, Date birthdate) {
    this(name, id);
    this.birthdate = new Date(birthdate.getTime());
    this.dateField = new java.sql.Date(birthdate.getTime());
    this.timeField = new java.sql.Time(birthdate.getTime());
  }

  public int getBreed() {
    return breed;
  }

  public java.sql.Date getDateField() {
    return dateField;
  }

  public Color getEyecolor() {
    return eyecolor;
  }

  public List<Cat> getKittens() {
    return kittens;
  }

  public Map<String, Cat> getKittensByName() {
    return kittensByName;
  }

  public Cat getMate() {
    return mate;
  }

  public String getStringAsSimple() {
    return stringAsSimple;
  }

  public java.sql.Time getTimeField() {
    return timeField;
  }

  public void setBreed(int breed) {
    this.breed = breed;
  }

  public void setDateField(java.sql.Date dateField) {
    this.dateField = new java.sql.Date(dateField.getTime());
  }

  public void setEyecolor(Color eyecolor) {
    this.eyecolor = eyecolor;
  }

  public void setKittens(List<Cat> kittens) {
    this.kittens = kittens;
  }

  public void setKittensByName(Map<String, Cat> kittensByName) {
    this.kittensByName = kittensByName;
  }

  public void setMate(Cat mate) {
    this.mate = mate;
  }

  public void setStringAsSimple(String stringAsSimple) {
    this.stringAsSimple = stringAsSimple;
  }

  public void setTimeField(java.sql.Time timeField) {
    this.timeField = timeField;
  }

  public Cat[] getKittenArray() {
    return kittenArray;
  }

  public void setKittenArray(Cat[] kittenArray) {
    this.kittenArray = kittenArray.clone();
  }

  public String toString() {
    return name;
  }
}
