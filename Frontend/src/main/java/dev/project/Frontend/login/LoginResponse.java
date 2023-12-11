package dev.project.Frontend.login;

import dev.project.Frontend.model.User;
import jakarta.servlet.http.Cookie;

public record LoginResponse(User user, String jwt) {
}
