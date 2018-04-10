package com.zeed.user.repository;

import com.zeed.user.models.OauthClientDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface OauthClientDetailsRepository extends JpaRepository<OauthClientDetails,Long> {

    OauthClientDetails findOneByClientId(String clientId);
}
