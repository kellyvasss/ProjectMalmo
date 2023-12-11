package dev.project.Frontend.view;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import dev.project.Frontend.model.Book;
import dev.project.Frontend.view.layout.MainLayout;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Route(value = "user", layout = MainLayout.class)
@AnonymousAllowed
public class UserView extends VerticalLayout {

    private static final CloseableHttpClient httpClient = HttpClients.createDefault();
    private final TextArea textArea = new TextArea();
    private final Button getUserBtn = new Button("GET USERS");

    // detta är for Book
    Grid<Book> grid = new Grid<>(Book.class, false);
    BookForm form;

    TextField filterText = new TextField();
    public UserView() {

        configureGrid();
        configureForm();

        add (
                getToolBar(),
                getContent(),
                setUserBtn()
        );
        updateList();
        closeEditor();
    }

    private HorizontalLayout getContent() {
        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        content.setSizeFull();
        return content;
    }
    private void configureForm() {
        form = new BookForm();
        form.setWidth("25em");
        form.addSaveListener(this::saveBook);
        form.addDeleteListener(this::deleteBook);
        form.addCloseListener(e -> closeEditor());
    }
    private void saveBook(BookForm.SaveEvent event) {
        // Här skall kallas på backenden API för att spara en bok
        event.getBook(); // -> denna har den boken som skall sparas
        updateList();
        closeEditor();
    }
    private void deleteBook(BookForm.DeleteEvent event) {
        // Här skall kallas på backenden API för att radera en bok
        event.getBook(); // -> denna har den boken som skall raderas
        updateList();
        closeEditor();
    }
    private void configureGrid() {
        //grid.setSizeFull();

        grid.addColumn(Book::getTitle).setHeader("Titel");
        grid.addColumn(Book::getAuthor).setHeader("Författare");

        grid.addComponentColumn(book -> {
            Icon x = new Icon(VaadinIcon.CART);
            x.addClickListener(e -> {
                UI.getCurrent().navigate(CartView.class);
            });
            return x;
        }).setWidth("100px").setFlexGrow(0);
       // grid.getColumns().forEach(bookColumn -> bookColumn.setAutoWidth(true)); orsakade problem

        grid.asSingleSelect().addValueChangeListener(event ->
                editBook(event.getValue()));
    }

    private Component getToolBar() {
        filterText.setPlaceholder("Filer by title...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY); // gör att det är en viss fördröjning på när det skall utföras
        filterText.addValueChangeListener(event -> updateList());

        Button addBookButton = new Button("Add book");
        addBookButton.addClickListener(click -> addBook());

        return new HorizontalLayout(filterText, addBookButton);
    }
    public void editBook(Book book) {
        if (book == null) {
            closeEditor();
        } else {
            form.setBook(book);
            form.setVisible(true);
        }
    }
    private void closeEditor() {
        form.setBook(null);
        form.setVisible(false);
    }
    private void addBook() {
        grid.asSingleSelect().clear();
        editBook(new Book());
    }
    private void updateList() {
        grid.setItems(getBooks());
    }


    private Component setUserBtn() {
        getUserBtn.addClickListener(e -> getBooks());
        return new HorizontalLayout(getUserBtn);
    }



    private List<Book> getBooks(){
        HttpGet get = new HttpGet("http://localhost:8080/books");
        get.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " +
                //Skriv in jwt
"eyJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJzZWxmIiwic3ViIjoiYWRtaW4iLCJpYXQiOjE3MDE5NDU2NDgsInJvbGVzIjoiQURNSU4ifQ.sXOp81OrFTRc7lkhrFfVxrkJeF_KMElU5ATHag2ocl_q0eHqWa7Wfg0nf_WRT9g6CIsLMUi6VxNVd4TUD4F5f8dd95z7Z1NWiDRGIhGdqfd0dPRue3J37DtmSNkD40JmV3GIYPNrti2xKOWtR8ZPS5t-9uzWbqwk5z10-2QoVusoCyjbAC-hhodp9x9RVdUwuZh8PwwCKIm56vjrluedOd3rp1rSI0CpKPn1t2LqbmeUGFT8ZZsmK0wHD4HlnFVKRRT8C-9YGozN14BBHH0RQB6yVgaaIzuF7BezIT7HOpsmq6PRVxfxta8Xa40_HdwqPX-_9lfX_DQCWjqqP1QwPw");
        Book book = new Book("Hej", "Hå");

        List bookis = new ArrayList<>();
        bookis.add(book);
        try {
            CloseableHttpResponse response = httpClient.execute(get);

            if (response.getCode() != 200) {
                textArea.setValue(response.getReasonPhrase());
                return bookis;
            }

            HttpEntity entity = response.getEntity();

            ObjectMapper mapper = new ObjectMapper();
            ArrayList<Book> books = mapper.readValue(EntityUtils.toString(entity), new TypeReference<ArrayList<Book>>() {
            });
            System.out.println(books.get(0).getTitle());
            return books;
        } catch (IOException e) {
            System.out.println("IO EXEPTION " + e.getMessage());
        } catch (ParseException e) {
            System.out.println("PARSE EXEPTION " + e.getMessage());
            throw new RuntimeException(e);
        } return bookis;

    }
}
