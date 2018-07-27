package com.zeed.user.security;

import com.zeed.user.exception.PaaroAuthenticationException;
import com.zeed.user.security.providers.UserAuthenticationProvider;
import com.zeed.user.services.UserService;
import com.zeed.usermanagement.models.Authority;
import com.zeed.usermanagement.models.ManagedUser;
import com.zeed.usermanagement.repository.ManagedUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class OauthAuthenticationProvider implements AuthenticationProvider {

    //For now, it's just one authentication provider that is needed
    @Autowired
    private UserAuthenticationProvider userAuthenticationProvider;
    @Autowired
    private ManagedUserRepository managedUserRepository;

    @Autowired
    private UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String userName = authentication.getName();
        String password = (authentication.getCredentials() !=null ) ? authentication.getCredentials().toString() : null;

        ManagedUser managedUser = managedUserRepository.findOneByEmail(userName);

        if (managedUser == null) {
            throw new PaaroAuthenticationException("User not found");
        }

        if (!managedUser.isActive()) {
            throw new PaaroAuthenticationException("User has been deactivated");
        }

        List<Authority> authorities = userService.getAuthoritiesByUserId(managedUser.getId());

        UsernamePasswordAuthenticationToken tokenAuthentication = new UsernamePasswordAuthenticationToken(userName, password, authorities);
        tokenAuthentication.setDetails(authentication.getDetails());

        //modify to log out first incase user is logged in already

        List<com.zeed.user.security.AuthenticationProvider> authenticationProviders = new ArrayList<>();
        authenticationProviders.add(userAuthenticationProvider);

        for (com.zeed.user.security.AuthenticationProvider authenticationProvider:authenticationProviders) {
            authentication = authenticationProvider.authenticate(tokenAuthentication);
            if (authentication!=null) {
                return authentication;
            }
        }
        return null;
    }

    @Override
    public boolean supports(Class<?> auth) {
        return auth.equals(UsernamePasswordAuthenticationToken.class);
    }
}
