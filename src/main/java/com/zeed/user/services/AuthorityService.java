package com.zeed.user.services;

import com.zeed.usermanagement.apimodels.ManagedUserModelApi;
import com.zeed.usermanagement.enums.ResponseStatus;
import com.zeed.usermanagement.models.Authority;
import com.zeed.usermanagement.models.ManagedUser;
import com.zeed.usermanagement.models.ManagedUserAuthority;
import com.zeed.usermanagement.repository.AuthorityRepository;
import com.zeed.usermanagement.repository.ManagedUserAuthorityRepository;
import com.zeed.usermanagement.repository.ManagedUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthorityService {


    @Autowired
    public ManagedUserRepository managedUserRepository;

    @Autowired
    public AuthorityRepository authorityRepository;

    @Autowired
    public ManagedUserAuthorityRepository managedUserAuthorityRepository;

    Logger logger = LoggerFactory.getLogger(AuthorityService.class.getName());

    public ManagedUserModelApi mapAuthoritiesToUser(String email, List<String> authorities) {

        try {

            if (CollectionUtils.isEmpty(authorities)) {
                return new ManagedUserModelApi(ResponseStatus.INVALID_REQUEST, "No authority passed");
            }

            if (StringUtils.isEmpty(email)) {
                return new ManagedUserModelApi(ResponseStatus.INVALID_REQUEST, "Email cannot be empty");
            }

            Set<String> setAuthorities = authorities.stream().filter(authority->!StringUtils.isEmpty(authority)).collect(Collectors.toSet());

            if (CollectionUtils.isEmpty(setAuthorities)) {
                return new ManagedUserModelApi(ResponseStatus.INVALID_REQUEST, "No authority passed");
            }

            List<Authority> savedAuthorities = authorityRepository.findAuthoritiesByAuthorityIn(setAuthorities);

            if (savedAuthorities == null) {
                return new ManagedUserModelApi(null,null,ResponseStatus.NOT_FOUND, "None of the authorities found");
            }

            ManagedUser managedUser = managedUserRepository.findOneByEmail(email);

            if (managedUser == null) {
                return new ManagedUserModelApi(null,null,ResponseStatus.NOT_FOUND, "User not found");
            }

            List<Authority> successfullyPersisted = new ArrayList<>();
            savedAuthorities.stream().parallel().forEach(authority -> {
                try {

                    List<ManagedUserAuthority> existingManagedUserAuthority = managedUserAuthorityRepository.findAllByManagedUserIdAndAuthorityId(managedUser.getId(), authority.getId());
                    if (CollectionUtils.isEmpty(existingManagedUserAuthority)) {
                        ManagedUserAuthority managedUserAuthority = new ManagedUserAuthority();
                        managedUserAuthority.setAuthorityId(authority.getId());
                        managedUserAuthority.setManagedUserId(managedUser.getId());
                        managedUserAuthorityRepository.save(managedUserAuthority);
                        successfullyPersisted.add(authority);
                    }
                } catch (Exception e) {
                    logger.error("Error occurred while persisting managed user authority due to ", e);
                }
            });

            ManagedUserModelApi managedUserModelApi = new ManagedUserModelApi();
            managedUserModelApi.setResponseStatus(ResponseStatus.SUCCESSFUL);
            managedUserModelApi.setAuthorityList(successfullyPersisted);
            managedUserModelApi.setMessage(String.format("%d out of %d authorities mapped to user successfully", successfullyPersisted.size(), authorities.size()));
            return managedUserModelApi;

        } catch (Exception e) {
            logger.error("Error occurred while mapping authority to user all authorities ", e);
            return new ManagedUserModelApi(null, null,ResponseStatus.SYSTEM_ERROR, "Error occurred while fetching details");
        }
    }


    public ManagedUserModelApi getAllAuthorities() {

        try {
            List<Authority> authorities = (List<Authority>) authorityRepository.findAll();

            if (authorities == null) {
                return new ManagedUserModelApi(null,null,ResponseStatus.NULL_RESPONSE, "No authority found in system");
            }

            ManagedUserModelApi managedUserModelApi = new ManagedUserModelApi();
            managedUserModelApi.setResponseStatus(ResponseStatus.SUCCESSFUL);
            managedUserModelApi.setAuthorityList(authorities);
            managedUserModelApi.setMessage("Authorities fetched successfully");

            return managedUserModelApi;

        } catch (Exception e) {
            logger.error("Error occurred while fetching all authorities ", e);
            return new ManagedUserModelApi(null, null,ResponseStatus.SYSTEM_ERROR, "Error occurred while fetching details");
        }
    }

    public ManagedUserModelApi mapAuthorityToUser(String email, String authority) {

        try {

            if (StringUtils.isEmpty(email) || StringUtils.isEmpty(authority)) {
                return new ManagedUserModelApi(ResponseStatus.INVALID_REQUEST, "Email and authority cannot be blank");
            }

            Authority savedAuthority = authorityRepository.findAuthorityByAuthority(authority);

            if (savedAuthority == null) {
                return new ManagedUserModelApi(null,null,ResponseStatus.NOT_FOUND, "Authority not found");
            }

            ManagedUser managedUser = managedUserRepository.findOneByEmail(email);

            if (managedUser == null) {
                return new ManagedUserModelApi(null,null,ResponseStatus.NOT_FOUND, "User not found");
            }

            List<ManagedUserAuthority> existingManagedUserAuthority = managedUserAuthorityRepository.findAllByManagedUserIdAndAuthorityId(managedUser.getId(), savedAuthority.getId());

            if (!CollectionUtils.isEmpty(existingManagedUserAuthority)) {
                return new ManagedUserModelApi(ResponseStatus.ALREADY_EXIST, "Authority has already been mapped to uer");
            }

            ManagedUserAuthority managedUserAuthority = new ManagedUserAuthority();
            managedUserAuthority.setAuthorityId(savedAuthority.getId());
            managedUserAuthority.setManagedUserId(managedUser.getId());

            managedUserAuthorityRepository.save(managedUserAuthority);

            ManagedUserModelApi managedUserModelApi = new ManagedUserModelApi();
            managedUserModelApi.setResponseStatus(ResponseStatus.SUCCESSFUL);
            managedUserModelApi.setAuthorityModel(savedAuthority);
            managedUserModelApi.setMessage("Authority mapped to user successfully");

            return managedUserModelApi;

        } catch (Exception e) {
            logger.error("Error occurred while mapping authority to user all authorities ", e);
            return new ManagedUserModelApi(null, null,ResponseStatus.SYSTEM_ERROR, "Error occurred while fetching details");
        }
    }

    public ManagedUserModelApi addAuthority(Authority authority) {

        try {
            if (authority == null || StringUtils.isEmpty(authority.getAuthority()) || StringUtils.isEmpty(authority.getDescription()) ) {
                return new ManagedUserModelApi(ResponseStatus.INVALID_REQUEST, "Description and authority name cannot be blank");
            }

            Authority existingAuthority = authorityRepository.findAuthorityByAuthority(authority.getAuthority());

            if (existingAuthority != null) {
                return new ManagedUserModelApi(ResponseStatus.ALREADY_EXIST, "Authority already exist");
            }

            Authority newAuthority = new Authority();
            newAuthority.setAuthority(authority.getAuthority());
            newAuthority.setDescription(authority.getDescription());

            authorityRepository.save(newAuthority);

            ManagedUserModelApi managedUserModelApi = new ManagedUserModelApi();
            managedUserModelApi.setMessage("Successfully added authority");
            managedUserModelApi.setAuthorityModel(newAuthority);
            managedUserModelApi.setResponseStatus(ResponseStatus.SUCCESSFUL);

            return managedUserModelApi;
        } catch (Exception e) {
            logger.error("Error occurred while adding authority ", e);
            return new ManagedUserModelApi(null, null,ResponseStatus.SYSTEM_ERROR, "Error occurred while adding authority");
        }

    }




}
