package dev.project.Frontend.view;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import dev.project.Frontend.model.Book;
import dev.project.Frontend.model.Katt;

@Route("katt")
public class GridView extends VerticalLayout {

    Grid<Book> grid = new Grid<>(Book.class, false);
    Book book = new Book("nisse", "bondkatt");
    public GridView() {
        grid.addColumn(Book::getTitle).setHeader("titel");
        grid.addColumn(Book::getAuthor).setHeader("författare");
        grid.setItems(book);

        add (
              grid
        );
    }
}
