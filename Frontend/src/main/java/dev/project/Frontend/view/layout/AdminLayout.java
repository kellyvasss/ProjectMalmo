package dev.project.Frontend.view.layout;

import com.vaadin.flow.component.applayout.AppLayout;

public class AdminLayout extends AppLayout {

    private static String jwt = MainLayout.getJwtToken();
    public AdminLayout() {
        createHeader();
        createDrawer();
    }

    private void createDrawer() {
        addToDrawer();
    }

    private void createHeader() {

        addToNavbar();
    }
}
