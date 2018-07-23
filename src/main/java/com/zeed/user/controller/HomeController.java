package com.zeed.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.ArrayList;

@Controller
@RequestMapping("/")
public class HomeController {

    @RequestMapping(value = "login", method = RequestMethod.GET)
    public String login(){
        return "login";
    }

    @RequestMapping(value = "logout", method = RequestMethod.GET)
    public String logout(Principal principal){
        return "login";
    }

    @RequestMapping(value = "helloo", method = RequestMethod.GET)
    public String hello(Principal principal){
        return "home";
    }

    @RequestMapping(value = "hello", method = RequestMethod.POST)
    public String hellopost(Principal principal){
        return "home";
    }

    @ResponseBody
    @RequestMapping(value = "what", method = RequestMethod.GET)
    public Object getWhat(){
        return new ArrayList<>();
    }


}
