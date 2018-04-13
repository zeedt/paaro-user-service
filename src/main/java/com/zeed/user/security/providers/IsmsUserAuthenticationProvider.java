package com.zeed.user.security.providers;

import com.zeed.user.repository.AuthorityRepository;
import com.zeed.user.repository.ManagedUserAuthorityRepository;
import com.zeed.user.repository.ManagedUserRepository;
import com.zeed.user.security.AuthenticationProvider;
import com.zeed.usermanagement.models.Authority;
import com.zeed.usermanagement.models.ManagedUser;
import com.zeed.usermanagement.security.UserDetailsTokenEnvelope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class IsmsUserAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private ManagedUserRepository managedUserRepository;

    @Autowired
    private ManagedUserAuthorityRepository managedUserAuthorityRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        ManagedUser managedUser = managedUserRepository.findOneByUserName(authentication.getPrincipal().toString());

        if (managedUser == null ) {
            return null;
        }else{
            //Check if the password match
            if (passwordEncoder.matches(authentication.getCredentials().toString(),managedUser.getPassword())) {

                List<Long> authoritiesId = managedUserAuthorityRepository.findAllByManagedUserId(managedUser.getId());

                List<Authority> authorities = new ArrayList<>();

                if (authoritiesId!=null && !authoritiesId.isEmpty()) {
                    authoritiesId.forEach(aLong -> {
                        Authority authority = authorityRepository.findById(aLong);
                        if (authority!=null) {
                            authorities.add(authority);
                        }
                    });
                }
                UserDetailsTokenEnvelope userDetailsTokenEnvelope = new UserDetailsTokenEnvelope(authorities,managedUser);
                UsernamePasswordAuthenticationToken authenticationDetails = new UsernamePasswordAuthenticationToken(userDetailsTokenEnvelope,null,authorities);
                return authenticationDetails;
            } else{
                throw new BadCredentialsException("Authentication failed. Username/password incorrect");
            }
        }



    }

    @Override
    public boolean supports(Class var1) {
        return true;
    }
}
