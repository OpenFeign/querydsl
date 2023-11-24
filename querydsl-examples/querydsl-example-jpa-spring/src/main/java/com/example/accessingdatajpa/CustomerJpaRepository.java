package com.example.accessingdatajpa;

import io.github.openfeign.querydsl.jpa.spring.repository.QuerydslJpaRepository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerJpaRepository extends JpaRepository<Customer, Long> {
 
}
