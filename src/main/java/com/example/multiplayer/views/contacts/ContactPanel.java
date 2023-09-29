package com.example.multiplayer.views.contacts;

import java.util.Optional;

import org.springframework.orm.ObjectOptimisticLockingFailureException;

import com.example.multiplayer.data.entity.Contact;
import com.example.multiplayer.data.service.ContactService;
import com.example.multiplayer.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.PermitAll;

@Route(value = "contacts/:contactID/:action?(edit)", layout = MainLayout.class)
@PermitAll
public class ContactPanel extends VerticalLayout
        implements BeforeEnterObserver {
    private static final String CONTACT_ID = "contactID";
    private static final String ACTION = "action";
    private static final String CONTACT_EDIT_ROUTE_TEMPLATE = "contacts/%s/edit";

    public static final String CONTACT_VIEW_ROUTE_TEMPLATE = "contacts/%s";

    private final TextField name = new TextField("Name");
    private final TextField email = new TextField("Email");
    private final TextField phone = new TextField("Phone");
    private final TextField occupation = new TextField("Occupation");

    private final Button edit = new Button("Edit");
    private final Button cancel = new Button("Cancel");

    private final Button save = new Button("Save");
    private final Button back = new Button("Back");

    private final Binder<Contact> binder;

    private final ContactService contactService;

    private final Component viewButtonLayout;
    private final Component editButtonLayout;

    private Contact contact;

    public ContactPanel(ContactService contactService) {
        this.contactService = contactService;

        binder = new Binder<>(Contact.class);
        binder.bindInstanceFields(this);

        back.addClickListener(
                e -> UI.getCurrent().navigate(ContactsList.class));

        edit.addClickListener(e -> editContact());

        cancel.addClickListener(e -> viewContact());

        save.addClickListener(e -> save());

        viewButtonLayout = createViewButtonLayout();
        editButtonLayout = createEditorButtonLayout();

        FormLayout form = new FormLayout(name, email, phone, occupation);

        add(form, viewButtonLayout, editButtonLayout);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        long contactId = event.getRouteParameters().get(CONTACT_ID)
                .map(Long::parseLong).get();
        Optional<Contact> contactFromBackend = contactService.get(contactId);

        if (contactFromBackend.isPresent()) {
            contact = contactFromBackend.get();

            boolean edit = event.getRouteParameters().get(ACTION).isPresent();

            updateForm(edit);
        } else {
            Notification.show(String.format(
                    "The requested contact was not found, ID = %s", contactId),
                    3000, Notification.Position.BOTTOM_START);
            event.forwardTo(ContactsList.class);
        }
    }

    private void save() {
        try {
            binder.writeBean(contact);
            contact = contactService.update(contact);
            binder.readBean(contact);

            viewContact();
            Notification.show("Data updated");
        } catch (ObjectOptimisticLockingFailureException exception) {
            Notification n = Notification.show(
                    "Error updating the data. Somebody else has updated the record while you were making changes.");
            n.setPosition(Position.MIDDLE);
            n.addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (ValidationException validationException) {
            Notification.show(
                    "Failed to update the data. Check again that all values are valid");
        }
    }

    private Component createEditorButtonLayout() {
        return createButtonLayout(save, cancel);
    }

    private Component createViewButtonLayout() {
        return createButtonLayout(edit, back);
    }

    private Component createButtonLayout(Button primary, Button tertiary) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        primary.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        tertiary.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        buttonLayout.add(primary, tertiary);

        return buttonLayout;
    }

    private void viewContact() {
        UI.getCurrent().getPage().getHistory().replaceState(null,
                String.format(CONTACT_VIEW_ROUTE_TEMPLATE, contact.getId()));

        updateForm(false);
    }

    private void editContact() {
        UI.getCurrent().getPage().getHistory().replaceState(null,
                String.format(CONTACT_EDIT_ROUTE_TEMPLATE, contact.getId()));

        updateForm(true);
    }

    private void updateForm(boolean edit) {
        if (!edit) {
            binder.readBean(contact);
        }
        binder.getFields().forEach(field -> field.setReadOnly(!edit));
        editButtonLayout.setVisible(edit);
        viewButtonLayout.setVisible(!edit);
    }
}
