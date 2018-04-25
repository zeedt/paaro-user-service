package com.zeed.user.services;

import com.zeed.usermanagement.apimodels.UserModelApi;
import com.zeed.usermanagement.models.Authority;
import com.zeed.usermanagement.models.ManagedUser;
import com.zeed.usermanagement.models.ManagedUserAuthority;
import com.zeed.usermanagement.repository.AuthorityRepository;
import com.zeed.usermanagement.repository.ManagedUserAuthorityRepository;
import com.zeed.usermanagement.repository.ManagedUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {


    @Autowired
    public ManagedUserRepository managedUserRepository;

    @Autowired
    public AuthorityRepository authorityRepository;

    @Autowired
    public ManagedUserAuthorityRepository managedUserAuthorityRepository;

    public UserModelApi getUserModelByUsername (String username) throws Exception {

        try {
            ManagedUser user = managedUserRepository.findOneByUserName(username);

            if (user == null) {
                throw new Exception("User not found");
            } else{
                List<Authority> authorities = getAuthoritiesByUserId(user.getId());

                user.setPassword("");

                return new UserModelApi(user,authorities);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Unexpected error occured due to " + e);
        }

    }

    public List<Authority> getAuthoritiesByUserId (Long id){

        List<ManagedUserAuthority> managedUserAuthorities = managedUserAuthorityRepository.findAllByManagedUserId(id);

        List<Long> authoritiesIds = new ArrayList<>();

        managedUserAuthorities.stream().parallel().forEach(managedUserAuthority -> {
            authoritiesIds.add(managedUserAuthority.getId());
        });

        List<Authority> authorities = authorityRepository.findAllByIdIn(authoritiesIds);

        return authorities;

    }

}