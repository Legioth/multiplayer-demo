package com.example.multiplayer.security;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.multiplayer.data.entity.User;
import com.example.multiplayer.data.service.UserRepository;
import com.vaadin.collaborationengine.UserInfo;
import com.vaadin.flow.spring.security.AuthenticationContext;

@Component
public class AuthenticatedUser {

    private final UserRepository userRepository;
    private final AuthenticationContext authenticationContext;

    public AuthenticatedUser(AuthenticationContext authenticationContext,
            UserRepository userRepository) {
        this.userRepository = userRepository;
        this.authenticationContext = authenticationContext;
    }

    @Transactional
    public Optional<User> get() {
        return authenticationContext.getAuthenticatedUser(UserDetails.class)
                .map(userDetails -> userRepository
                        .findByUsername(userDetails.getUsername()));
    }

    public void logout() {
        authenticationContext.logout();
    }
 
    @Transactional
    public Optional<UserInfo> getAsUserInfo() {
        return get().map(user -> new UserInfo(user.getUsername(),
                user.getName(), user.getProfilePictureUrl()));
    }

}
