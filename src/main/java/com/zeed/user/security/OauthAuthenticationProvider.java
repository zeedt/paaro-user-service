package com.zeed.user.security;

import com.zeed.user.security.providers.UserAuthenticationProvider;
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

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String userName = authentication.getName();
        String password = (authentication.getCredentials() !=null ) ? authentication.getCredentials().toString() : null;

        UsernamePasswordAuthenticationToken tokenAuthentication = new UsernamePasswordAuthenticationToken(userName, password);
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
