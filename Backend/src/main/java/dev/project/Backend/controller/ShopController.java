package dev.project.Backend.controller;

import dev.project.Backend.database.entity.Article;
import dev.project.Backend.database.entity.ShoppingCartDetail;
import dev.project.Backend.database.entity.User;
import dev.project.Backend.database.service.ShopService;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@PermitAll
public class ShopController {

    private final ShopService service;
    @GetMapping("")
    @PermitAll
    public ResponseEntity<List<Article>> start() {
        return ResponseEntity.ok(service.getArticles(null));
    }
    @PostMapping("")
    public ResponseEntity<List<ShoppingCartDetail>> getDetails(Authentication authentication) {

        User user = service.getUser(authentication.getName());
        return ResponseEntity.ok(service.getShoppingCartDetails(user));
    }
    @PostMapping("add")
    public ResponseEntity<ShoppingCartDetail> add(@RequestParam Long id, @RequestParam int quantity, Authentication authentication) {
        User user = service.getUser(authentication.getName());
        return ResponseEntity.ok(service.add(authentication.getName(), id, quantity));
    }


}
