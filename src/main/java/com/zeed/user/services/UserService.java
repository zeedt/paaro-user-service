package com.zeed.user.services;

import com.google.common.base.Strings;
import com.zeed.usermanagement.apimodels.ManagedUserModelApi;
import com.zeed.usermanagement.enums.ResponseStatus;
import com.zeed.usermanagement.models.Authority;
import com.zeed.usermanagement.models.ManagedUser;
import com.zeed.usermanagement.models.ManagedUserAuthority;
import com.zeed.usermanagement.models.UserCategory;
import com.zeed.usermanagement.repository.AuthorityRepository;
import com.zeed.usermanagement.repository.ManagedUserAuthorityRepository;
import com.zeed.usermanagement.repository.ManagedUserRepository;
import com.zeed.usermanagement.requestmodels.PasswordResetRequestModel;
import com.zeed.usermanagement.requestmodels.UserUpdateRequestModel;
import com.zeed.usermanagement.security.UserDetailsTokenEnvelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {


    private Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public ManagedUserRepository managedUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public AuthorityRepository authorityRepository;

    @Autowired
    public ManagedUserAuthorityRepository managedUserAuthorityRepository;

    public ManagedUserModelApi getUserModelByEmail(String email) throws Exception {

        try {
            ManagedUser user = managedUserRepository.findOneByEmail(email);

            if (user == null) {
                return new ManagedUserModelApi(ResponseStatus.NOT_FOUND, "User not found");
            } else{
                List<Authority> authorities = getAuthoritiesByUserId(user.getId());

                user.setPassword("");

                return new ManagedUserModelApi(user,authorities,ResponseStatus.SUCCESSFUL, "User found");
            }
        } catch (Exception e) {
            logger.error("Error occurred while getting user model by email user due to ", e);
            throw new Exception("Unexpected error occured due to " + e);
        }

    }

    public List<Authority> getAuthoritiesByUserId (Long id){

        try {
            List<ManagedUserAuthority> managedUserAuthorities = managedUserAuthorityRepository.findAllByManagedUserId(id);

            List<Long> authoritiesIds = new ArrayList<>();

            managedUserAuthorities.stream().parallel().forEach(managedUserAuthority -> {
                authoritiesIds.add(managedUserAuthority.getAuthorityId());
            });

            return authorityRepository.findAllByIdIn(authoritiesIds);

        } catch (Exception e) {
            logger.error("Error occurred while fetching user authorities due to ", e);
            return null;
        }

    }

    public ManagedUserModelApi addManagedUser(ManagedUser managedUser) {

        try {
            if (managedUser == null || Strings.isNullOrEmpty(managedUser.getEmail())) {
                return new ManagedUserModelApi(managedUser, null, ResponseStatus.INVALID_REQUEST, "Email cannot be null");
            }

            ManagedUser user = managedUserRepository.findOneByEmail(managedUser.getEmail());
            if (user!=null) {
                return new ManagedUserModelApi(managedUser, null,null, ResponseStatus.ALREADY_EXIST);
            }
            if (Strings.isNullOrEmpty(managedUser.getPassword()) || managedUser.getPassword().trim().length() < 6) {
                return new ManagedUserModelApi(managedUser, null, ResponseStatus.INVALID_REQUEST, "Password cannot be blank and must not be less than 6 characters ");
            }
            if (Strings.nullToEmpty(managedUser.getFirstName()).trim().length() < 3 || Strings.nullToEmpty(managedUser.getLastName()).trim().length()<3) {
                return new ManagedUserModelApi(managedUser, null, ResponseStatus.INVALID_REQUEST, "First name and last name cannot be less than 3 characters ");
            }
            if (Strings.nullToEmpty(managedUser.getPhoneNumber()).length() < 11 || Strings.nullToEmpty(managedUser.getPhoneNumber()).length() > 20) {
                return new ManagedUserModelApi(managedUser, null, ResponseStatus.INVALID_REQUEST, "Phone number must be between 11-20 characters");
            }
            managedUser.setDateCreated(new java.util.Date());
            managedUser.setActive(true);
            managedUser.setUserCategory(UserCategory.CUSTOMER);
            managedUser.setPassword(passwordEncoder.encode(managedUser.getPassword()));
            managedUserRepository.save(managedUser);
            managedUser.setPassword("");
            return new ManagedUserModelApi(managedUser,null, null,ResponseStatus.SUCCESSFUL);
        } catch (Exception e) {
            logger.error("Error occurred while creating user due to ", e);
            return new ManagedUserModelApi(managedUser,null, ResponseStatus.SYSTEM_ERROR,"Error occurred while creating user due to " + e.getCause().toString());
        }
    }

    public ManagedUserModelApi deactivateUser(String email) {

        try {
            ManagedUser user = managedUserRepository.findOneByEmail(email);
            if (user == null) {
                return new ManagedUserModelApi(null,null, ResponseStatus.NOT_FOUND, "User not found");
            }
            user.setActive(false);
            managedUserRepository.save(user);
            return new ManagedUserModelApi(null,null,ResponseStatus.SUCCESSFUL, "User successfully deactivated");
        } catch (Exception e) {
            logger.error("Error occurred while deactivating user due to ", e);
            return new ManagedUserModelApi(null,null, ResponseStatus.SYSTEM_ERROR,"Error occurred while deactivating due to " + e.getCause().toString());
        }
    }

    public ManagedUserModelApi activateUser(String email) {

        try {
            ManagedUser user = managedUserRepository.findOneByEmail(email);
            if (user == null) {
                return new ManagedUserModelApi(null,null, ResponseStatus.NOT_FOUND, "User not found");
            }
            user.setActive(true);
            managedUserRepository.save(user);
            return new ManagedUserModelApi(null,null,ResponseStatus.SUCCESSFUL, "User successfully activated");
        } catch (Exception e) {
            logger.error("Error occurred while activating user due to ", e);
            return new ManagedUserModelApi(null,null, ResponseStatus.SYSTEM_ERROR,"Error occurred while activating due to " + e.getCause().toString());
        }
    }

    public ManagedUserModelApi resetUserPassword(PasswordResetRequestModel resetRequestModel) {

        if (resetRequestModel == null) {
            return new ManagedUserModelApi(null,null, ResponseStatus.INVALID_REQUEST, "Invalid request");
        }

        UserDetailsTokenEnvelope userDetailsTokenEnvelope = (UserDetailsTokenEnvelope) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetailsTokenEnvelope.managedUser.getEmail();

        if (Strings.isNullOrEmpty(email)) {
            return new ManagedUserModelApi(null,null, ResponseStatus.INVALID_REQUEST, "Email not found for user");
        }

        if (Strings.isNullOrEmpty(resetRequestModel.getOldPassword()) || Strings.isNullOrEmpty(resetRequestModel.getNewPassword())) {
            return new ManagedUserModelApi(null,null, ResponseStatus.INVALID_REQUEST, "Both old and new passwords cannot be blank");
        }

        if (resetRequestModel.getNewPassword().trim().length() < 6) {
            return new ManagedUserModelApi(null, null, ResponseStatus.INVALID_REQUEST, "Password must not be less than 6 characters ");
        }

        try {
            String oldEncodedPassword = passwordEncoder.encode(resetRequestModel.getOldPassword());
            ManagedUser user = managedUserRepository.findOneByEmail(email);
            if (user == null) {
                return new ManagedUserModelApi(null,null, ResponseStatus.NOT_FOUND, "User not found");
            }
            boolean exist =  passwordEncoder.matches(resetRequestModel.getOldPassword(), user.getPassword());
            if (!exist) {
                return new ManagedUserModelApi(null,null, ResponseStatus.NOT_FOUND, "Incorrect old password. Please supply correct password");
            }
            String newEncodedPassword = passwordEncoder.encode(resetRequestModel.getNewPassword());
            user.setPassword(newEncodedPassword);
            managedUserRepository.save(user);
            return new ManagedUserModelApi(null,null,ResponseStatus.SUCCESSFUL, "Password reset successful");
        } catch (Exception e) {
            logger.error("Error occurred while resetting password due to ", e);
            return new ManagedUserModelApi(null,null, ResponseStatus.SYSTEM_ERROR,"Error occurred while resetting password due to " + e.getCause().toString());
        }
    }


    public ManagedUserModelApi updateUser(UserUpdateRequestModel requestModel) {

        try {
            if (requestModel == null) {
                return new ManagedUserModelApi(null,null, ResponseStatus.INVALID_REQUEST, "Invalid request");
            }
            UserDetailsTokenEnvelope userDetailsTokenEnvelope = (UserDetailsTokenEnvelope) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String email = userDetailsTokenEnvelope.managedUser.getEmail();

            if (StringUtils.isEmpty(email)) {
                return new ManagedUserModelApi(ResponseStatus.NOT_FOUND, "User is not logged in");
            }

            if (Strings.isNullOrEmpty(requestModel.getNewEmail())) {
                return new ManagedUserModelApi(null,null, ResponseStatus.INVALID_REQUEST, "Both old and new Email cannot be blank");
            }

            ManagedUser user = managedUserRepository.findOneByEmail(email);
            if (user == null) {
                return new ManagedUserModelApi(null,null,ResponseStatus.NOT_FOUND, "User not found");
            }

            if (!email.trim().equals(requestModel.getNewEmail().trim())) {
                ManagedUser user1 = managedUserRepository.findOneByEmail(requestModel.getNewEmail());
                if (user1 != null) {
                    return new ManagedUserModelApi(null,null,ResponseStatus.ALREADY_EXIST, "A user already exist with the email address");
                }
            }

            user.setEmail(requestModel.getNewEmail().trim());
            user.setLastName(requestModel.getLastName());
            user.setFirstName(requestModel.getFirstName());
            user.setPhoneNumber(requestModel.getPhoneNumber());
            managedUserRepository.save(user);
            user.setPassword("");
            return new ManagedUserModelApi(user, null,ResponseStatus.SUCCESSFUL, "User information updated successfully");
        } catch (Exception e) {
            logger.error("Error occurred while updating details due to ==> ", e);
            return new ManagedUserModelApi(null, null, ResponseStatus.SYSTEM_ERROR, "System error occured while updating details due to ==> " + e.getCause().toString());
        }

    }




}
