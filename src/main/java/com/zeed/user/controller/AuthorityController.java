package com.zeed.user.controller;

import com.zeed.user.services.AuthorityService;
import com.zeed.usermanagement.apimodels.ManagedUserModelApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

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
    @RequestMapping(value = "/mapAuthoritiesToUser", method = RequestMethod.POST)
    public ManagedUserModelApi mapAuthoritiesToUser(@RequestBody String email, @RequestBody List<String> authorities){
        return authorityService.mapAuthoritiesToUser(email, authorities);
    }


    @ResponseBody
    @RequestMapping(value = "/mapAuthorityToUser", method = RequestMethod.POST)
    public ManagedUserModelApi mapAuthorityToUser(@RequestBody String email, @RequestBody String authority){
        return authorityService.mapAuthorityToUser(email, authority);
    }



}
