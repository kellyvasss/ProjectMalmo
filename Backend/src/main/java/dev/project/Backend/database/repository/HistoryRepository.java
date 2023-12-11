package dev.project.Backend.database.repository;


import dev.project.Backend.database.entity.Article;
import dev.project.Backend.database.entity.History;
import org.springframework.data.jpa.repository.JpaRepository;
import dev.project.Backend.database.entity.User;

import java.util.List;

public interface HistoryRepository extends JpaRepository<History, Long> {

    List<History> findByUser(User user);
    List<History> findByArticle(Article article);
}
