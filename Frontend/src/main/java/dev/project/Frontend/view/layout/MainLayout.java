package dev.project.Frontend.view.layout;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.theme.lumo.LumoUtility;
import dev.project.Frontend.login.LoginResponse;
import dev.project.Frontend.model.User;
import dev.project.Frontend.view.AdminView;
import dev.project.Frontend.view.CartView;
import dev.project.Frontend.view.GridView;
import dev.project.Frontend.view.HistoryView;
import jakarta.servlet.http.Cookie;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.IOException;
import java.util.Arrays;

public class MainLayout extends AppLayout {
    private static final CloseableHttpClient httpClient = HttpClients.createDefault();

    private static String jwtToken = "";
    public static String getJwtToken() {
        return jwtToken;
    }
    public MainLayout() {
        createHeader();
        createDrawer();
    }

    private void createDrawer() {
        addToDrawer(new VerticalLayout(
                new HorizontalLayout(new RouterLink("Kundkorg", CartView.class), new Icon(VaadinIcon.CART)),
                new HorizontalLayout(new RouterLink("Köphistorik", HistoryView.class), new Icon(VaadinIcon.PIGGY_BANK_COIN)),
                new HorizontalLayout(new RouterLink("Admin", GridView.class), new Icon(VaadinIcon.USER))
        ));

    }

    private void createHeader() {
        H1 logo = new H1("Malmö Shop");
        Button login = new Button("Logga in", event -> openLoginDialog());
        Button register = new Button("Registrera", event -> openRegistrationDialog());


        var header = new HorizontalLayout(new DrawerToggle(), logo, login, register);

        addToNavbar(header);
    }

    private void openLoginDialog() {
        Dialog loginDialog = new Dialog();
        loginDialog.add(createLoginForm(loginDialog));
        loginDialog.open();
    }
    private void openRegistrationDialog() {
        Dialog registerDialog = new Dialog();
        registerDialog.add(createRegistrationForm(registerDialog));
        registerDialog.open();
    }
    private FormLayout createLoginForm(Dialog loginDialog) {
        FormLayout loginForm = new FormLayout();
        TextField username = new TextField("Användarnamn");
        PasswordField password = new PasswordField("Lösenord");
        Button loginButton = new Button("Logga in", event -> {
            try {
                handleLoginResult(loginDialog, username.getValue(), password.getValue());
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });
        loginForm.add(username, password, loginButton);
        return loginForm;
    }
    private FormLayout createRegistrationForm(Dialog registrationDialog) {
        FormLayout registerForm = new FormLayout();
        TextField username = new TextField("Användarnamn");
        PasswordField password = new PasswordField("Lösenord");
        Button registerButton = new Button("Registrera", event -> {
            handleRegistrationResult(registrationDialog, username.getValue(), password.getValue());
        });
        registerForm.add(username, password, registerButton);
        return registerForm;
    }
    private void handleLoginResult(Dialog loginDialog, String username, String password) throws IOException, ParseException {
        boolean loginSuccess = performLogin(username, password);
        if (loginSuccess) {
            // Spara token
            System.out.println("Inlogg lyckad");
            loginDialog.close();
        }
        else System.out.println("Inlogg misslyckad");
    }
    private void handleRegistrationResult(Dialog registrationDialog, String username, String password) {
        boolean registrationSuccess = performRegistration(username, password);
        if (registrationSuccess) {
            System.out.println("Registrering lyckad");
        }
        else System.out.println("Registrering misslyckad");
    }
    private boolean performLogin(String username, String password) throws IOException, ParseException {
        // kalla på API här
        User loginUser = new User(username, password);
        HttpPost request = new HttpPost("http://localhost:8080/auth/login");
        request.setEntity(createPayload(loginUser));

        CloseableHttpResponse response = httpClient.execute(request);

        if (response.getCode() != 200) {
            System.out.println("Inlogg misslyckad, okänt fel");
            return false;
        }

        HttpEntity payload = response.getEntity();

        ObjectMapper mapper = new ObjectMapper();
        LoginResponse loginResponse = mapper.readValue(EntityUtils.toString(payload), new TypeReference<LoginResponse>() {});

        if (loginResponse.user() == null) {
            System.out.println("Felaktigt användarnamn eller lösenord");
            return false;
        }
        if (loginResponse.user().getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ADMIN"))) {
            UI.getCurrent().navigate(HistoryView.class);

        } else UI.getCurrent().navigate(CartView.class);
        System.out.println(loginResponse.jwt());
        jwtToken = loginResponse.jwt();
        System.out.println(jwtToken);

        UI.getCurrent().navigate(AdminView.class);
        return true;
    }

    private boolean performRegistration(String username, String password) {
        // kalla på API här
        return false;
    }
    private String getBearerToken() {
        // använd svar från api för att få fram token
        // kanske spara i en speciell fil i projektet?
        // eller spara token direkt i en fil i systemet under lyckad inlogg?
        return "token";
    }
    private void hideLoginAndRegistration() {
        // vid lyckad registrering/inlogg, dölj alternativ för att kunna logga in/registrera sig
    }
    public static StringEntity createPayload(Object object) throws JsonProcessingException {
        //Skapa och inkludera en Payload till request
        ObjectMapper mapper = new ObjectMapper();
        StringEntity payload = new StringEntity(mapper.writeValueAsString(object), ContentType.APPLICATION_JSON);

        return payload;
    }


}
