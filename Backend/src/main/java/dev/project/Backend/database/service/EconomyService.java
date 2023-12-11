package dev.project.Backend.database.service;


import dev.project.Backend.database.entity.Article;
import dev.project.Backend.database.entity.History;
import dev.project.Backend.database.repository.ArticleRepository;
import dev.project.Backend.database.repository.HistoryRepository;
import dev.project.Backend.database.repository.UserRepository;
import dev.project.Backend.database.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EconomyService {

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final HistoryRepository historyRepository;

    /* Det skall finnas följande funktioner för economy:

     * USERS:
        * SE SIN EGEN KÖPHISTORIK, PRIS OCH PRODUKT  <1>

     * ADMIN:
        * SE ALL / SPECIFIK KÖPHISTORIK              <1,3>
        * UPPDATERA PRIS PÅ ARTIKEL                  <4>
        * SE ALL HISTORIK FÖR SPECIFIKA PRODUKTER    <2>
     */

    public List<History> getHistoryUser(User user) { // <1>
        return historyRepository.findByUser(user);
    }
    public List<History> getHistoryArticle(Article article) { // <2>
        return historyRepository.findByArticle(article);
    }
    public List<History> getAllHistory() { // <3>
        return historyRepository.findAll();
    }
    public void updatePrice(String name, Double newPrice) { // <4>
        Article article = articleRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Artikel finns inte"));

        article.setPrice(newPrice);
        articleRepository.save(article);
    }

}
