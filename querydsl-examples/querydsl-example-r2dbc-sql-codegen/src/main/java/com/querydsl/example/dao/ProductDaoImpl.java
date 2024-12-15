package com.querydsl.example.dao;

import static com.querydsl.core.types.Projections.bean;
import static com.querydsl.example.r2dbc.QProduct.product;
import static com.querydsl.example.r2dbc.QProductL10n.productL10n;
import static com.querydsl.example.r2dbc.QSupplier.supplier;

import com.querydsl.core.dml.StoreClause;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.QBean;
import com.querydsl.example.dto.Product;
import com.querydsl.example.dto.ProductL10n;
import com.querydsl.example.dto.Supplier;
import com.querydsl.r2dbc.R2DBCQueryFactory;
import com.querydsl.r2dbc.group.ReactiveGroupBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Transactional
public class ProductDaoImpl implements ProductDao {

  @Autowired R2DBCQueryFactory queryFactory;

  final QBean<ProductL10n> productL10nBean =
      bean(ProductL10n.class, productL10n.description, productL10n.lang, productL10n.name);

  final QBean<Product> productBean =
      bean(
          Product.class,
          product.id,
          product.name,
          product.otherProductDetails,
          product.price,
          bean(Supplier.class, supplier.all()).as("supplier"),
          GroupBy.set(productL10nBean).as("localizations"));

  @Override
  public Mono<Product> findById(long id) {
    return getBaseQuery(product.id.eq(id)).singleOrEmpty();
  }

  @Override
  public Flux<Product> findAll(Predicate... where) {
    return getBaseQuery(where);
  }

  private <T extends StoreClause<T>> T populate(T dml, Product p) {
    return dml.set(product.name, p.getName())
        .set(product.otherProductDetails, p.getOtherProductDetails())
        .set(product.price, p.getPrice())
        .set(product.supplierId, p.getSupplier().getId());
  }

  @Override
  public Mono<Product> save(Product p) {
    var id = p.getId();

    if (id == null) {
      return insert(p);
    }

    return update(p);
  }

  public Mono<Product> insert(Product p) {
    return populate(queryFactory.insert(product), p)
        .executeWithKey(product.id)
        .flatMapIterable(
            id -> {
              var insert = queryFactory.insert(productL10n);
              return p.getLocalizations().stream()
                  .map(
                      l10n ->
                          insert
                              .set(productL10n.productId, id)
                              .set(productL10n.description, l10n.getDescription())
                              .set(productL10n.lang, l10n.getLang())
                              .set(productL10n.name, l10n.getName())
                              .execute())
                  .map(
                      __ -> {
                        p.setId(id);

                        return p;
                      })
                  .toList();
            })
        .reduce((a, b) -> a);
  }

  public Mono<Product> update(Product p) {
    var id = p.getId();

    return populate(queryFactory.update(product), p)
        .where(product.id.eq(id))
        .execute()
        .flatMap(
            __ ->
                queryFactory
                    .delete(productL10n)
                    .where(productL10n.productId.eq(id))
                    .execute()
                    .flatMapIterable(
                        ___ -> {
                          var insert = queryFactory.insert(productL10n);
                          return p.getLocalizations().stream()
                              .map(
                                  l10n ->
                                      insert
                                          .set(productL10n.productId, id)
                                          .set(productL10n.description, l10n.getDescription())
                                          .set(productL10n.lang, l10n.getLang())
                                          .set(productL10n.name, l10n.getName())
                                          .execute())
                              .map(
                                  ____ -> {
                                    p.setId(id);

                                    return p;
                                  })
                              .toList();
                        })
                    .reduce((a, b) -> a));
  }

  @Override
  public Mono<Long> count() {
    return queryFactory.from(product).fetchCount();
  }

  @Override
  public Mono<Void> delete(Product p) {
    // TODO use combined delete clause
    return queryFactory
        .delete(productL10n)
        .where(productL10n.productId.eq(p.getId()))
        .execute()
        .then(queryFactory.delete(product).where(product.id.eq(p.getId())).execute())
        .then();
  }

  private Flux<Product> getBaseQuery(Predicate... where) {
    return (Flux<Product>)
        queryFactory
            .from(product)
            .innerJoin(product.supplierFk, supplier)
            .leftJoin(product._productFk, productL10n)
            .where(where)
            .transform(ReactiveGroupBy.groupBy(product.id).flux(productBean));
  }
}
