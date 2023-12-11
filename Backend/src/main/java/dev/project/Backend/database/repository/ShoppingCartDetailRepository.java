package dev.project.Backend.database.repository;


import dev.project.Backend.database.entity.Article;
import dev.project.Backend.database.entity.ShoppingCart;
import dev.project.Backend.database.entity.ShoppingCartDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ShoppingCartDetailRepository extends JpaRepository<ShoppingCartDetail, Long> {

    List<ShoppingCartDetail> findByCart(ShoppingCart cart);
    void deleteAllByCart(ShoppingCart cart);

    // Radera alla rader i tbl som tillhör en kundkorg (använd vid köp)
    @Transactional
    @Modifying
    @Query("DELETE FROM ShoppingCartDetail sd WHERE sd.cart = :shoppingCart")
    void deleteByShoppingCart(@Param("shoppingCart") ShoppingCart shoppingCart);

    // Uppdaterar antalet artiklar
    // Det behövs 3 parametrar: ShoppingChart, Artikel och nytt antal
    @Modifying
    @Query("UPDATE ShoppingCartDetail sd SET sd.quantity = :newQuantity WHERE sd.cart = :shoppingCart AND sd.article = :article")
    void updateQuantity(@Param("shoppingCart") ShoppingCart shoppingCart,
                        @Param("article") Article article,
                        @Param("newQuantity") int newQuantity);
}
