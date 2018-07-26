package com.zeed.user.controller;

import com.zeed.user.services.UserService;
import com.zeed.usermanagement.apimodels.ManagedUserModelApi;
import com.zeed.usermanagement.models.ManagedUser;
import com.zeed.usermanagement.requestmodels.PasswordResetRequestModel;
import com.zeed.usermanagement.requestmodels.UserUpdateRequestModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    public UserService userService;

    @ResponseBody
    @RequestMapping(value = "/getDetailsByemail")
    public ManagedUserModelApi getUserInformation(@RequestParam String email) throws Exception {
        return userService.getUserModelByEmail(email);
    }

    @ResponseBody
    @RequestMapping(value = "/createUser", method = RequestMethod.POST)
    public ManagedUserModelApi createUser(@RequestBody ManagedUser managedUser){
        return userService.addManagedUser(managedUser);
    }

    @ResponseBody
    @RequestMapping (value = "/deactivateUser", method = RequestMethod.GET)
    public ManagedUserModelApi deactivateUser(@RequestParam("email") String email) {
        return userService.deactivateUser(email);
    }

    @ResponseBody
    @RequestMapping (value = "/activateUser", method = RequestMethod.GET)
    public ManagedUserModelApi activateUser(@RequestParam("email") String email) {
        return userService.activateUser(email);
    }

    @ResponseBody
    @RequestMapping(value = "/reset_user_password", method = RequestMethod.POST)
    public ManagedUserModelApi resetUserPassword(@RequestBody PasswordResetRequestModel resetRequestModel) {
        return userService.resetUserPassword(resetRequestModel);
    }


    @ResponseBody
    @RequestMapping(value = "/updateUser", method = RequestMethod.POST)
    public ManagedUserModelApi updateUserDetails(@RequestBody UserUpdateRequestModel requestModel){
        return userService.updateUser(requestModel);
    }

}
