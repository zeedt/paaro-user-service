package com.zeed.user.controller;

import com.zeed.user.services.AuthorityService;
import com.zeed.usermanagement.apimodels.ManagedUserModelApi;
import com.zeed.usermanagement.models.Authority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/authority")
public class AuthorityController {

    @Autowired
    private AuthorityService authorityService;

    @ResponseBody
    @RequestMapping(value = "/fetchAllAuthorities", method = RequestMethod.GET)
    public ManagedUserModelApi fetchAllAuthorities(){
        return authorityService.getAllAuthorities();
    }


    @ResponseBody
    @PreAuthorize("hasAuthority('MAP_AUTHORITIES_TO_USER')")
    @RequestMapping(value = "/mapAuthoritiesToUser", method = RequestMethod.POST)
    public ManagedUserModelApi mapAuthoritiesToUser(@RequestBody ManagedUserModelApi userModelApi){
        return authorityService.mapAuthoritiesToUser(userModelApi.getEmail(), userModelApi.getAuthorities());
    }


    @ResponseBody
    @PreAuthorize("hasAuthority('MAP_AUTHORITIES_TO_USER')")
    @RequestMapping(value = "/mapAuthorityToUser", method = RequestMethod.POST)
    public ManagedUserModelApi mapAuthorityToUser(@RequestBody ManagedUserModelApi userModelApi){
        return authorityService.mapAuthorityToUser(userModelApi.getEmail(), userModelApi.getAuthority());
    }

    @ResponseBody
    @PreAuthorize("hasAuthority('ADD_AUTHORITY')")
    @RequestMapping(value = "/addAuthority", method = RequestMethod.POST)
    public ManagedUserModelApi addAuthority(@RequestBody Authority authority){
        return authorityService.addAuthority(authority);
    }



}
