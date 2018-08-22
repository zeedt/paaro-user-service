package com.zeed.user.controller;

import com.zeed.user.services.UserService;
import com.zeed.usermanagement.apimodels.ManagedUserModelApi;
import com.zeed.usermanagement.models.ManagedUser;
import com.zeed.usermanagement.requestmodels.PasswordResetRequestModel;
import com.zeed.usermanagement.requestmodels.UserUpdateRequestModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    public UserService userService;

    @ResponseBody
    @PreAuthorize("hasAuthority('VIEW_USER_DETAILS')")
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
    @PreAuthorize("hasAuthority('DEACTIVATE_USER')")
    @RequestMapping (value = "/deactivateUser", method = RequestMethod.GET)
    public ManagedUserModelApi deactivateUser(@RequestParam("email") String email, Principal principal) {
        return userService.deactivateUser(email);
    }

    @ResponseBody
    @PreAuthorize("hasAuthority('ACTIVATE_USER')")
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
    @ResponseBody
    @RequestMapping(value = "/forgotPassword", method = RequestMethod.POST)
    public ManagedUserModelApi forgotPassword(@RequestBody ManagedUserModelApi requestModel) throws IOException {
        return userService.forgotPassword(requestModel);
    }

    @ResponseBody
    @PreAuthorize("hasAuthority('CREATE_ADMIN_USER')")
    @RequestMapping(value = "/createAdminUser", method = RequestMethod.POST)
    public ManagedUserModelApi createAdminUser(@RequestBody ManagedUser managedUser){
        return userService.createAdminUser(managedUser);
    }

    @ResponseBody
    @RequestMapping (value = "/logout", method = RequestMethod.GET)
    public ManagedUserModelApi logout() {
        return userService.logout();
    }

}
