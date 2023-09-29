package com.example.multiplayer.views.collaborative;

import java.util.stream.Stream;

import org.springframework.data.domain.PageRequest;

import com.example.multiplayer.data.entity.Contact;
import com.example.multiplayer.data.service.ContactService;
import com.example.multiplayer.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;

import jakarta.annotation.security.PermitAll;

@PageTitle("Contacts")
@Route(value = "collaborative", layout = MainLayout.class)
@PermitAll
public class CollaborativeList extends VerticalLayout {

    public CollaborativeList(ContactService contactService) {
        Grid<Contact> grid = new Grid<>(Contact.class, false);
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

        grid.addColumn("name").setHeader((String) null).setAutoWidth(true);
        grid.setItems(query -> fethcItems(contactService, query));

        grid.addItemDoubleClickListener(event -> {
            Contact contact = event.getItem();
            UI.getCurrent()
                    .navigate(String.format(
                            CollaborativePanel.CONTACT_VIEW_ROUTE_TEMPLATE,
                            contact.getId()));
        });

        add(grid);
        setSizeFull();
    }

    private static Stream<Contact> fethcItems(ContactService contactService,
            Query<Contact, Void> query) {
        return contactService
                .list(PageRequest.of(query.getPage(), query.getPageSize(),
                        VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream();
    }
}
