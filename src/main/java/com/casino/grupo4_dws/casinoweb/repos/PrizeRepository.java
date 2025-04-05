package com.casino.grupo4_dws.casinoweb.repos;

import com.casino.grupo4_dws.casinoweb.model.Prize;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PrizeRepository extends JpaRepository<Prize, Long> {
    @Override
    <S extends Prize> S save(S entity);
    @Override
    <S extends Prize> List<S> findAll(Example<S> example);
    Optional<Prize> findPrizeById(int id);
    List<Prize> findAllByOwnerIsNull();
    @Query("SELECT p FROM Prize p WHERE " +
            "(:title is null OR LOWER(p.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
            "(:minPrice is null OR p.price >= :minPrice) AND " +
            "(:maxPrice is null OR p.price <= :maxPrice)")
    List<Prize> findByFilters(
            @Param("title") String title,
            @Param("minPrice") Integer minPrice,
            @Param("maxPrice") Integer maxPrice
    );
}
