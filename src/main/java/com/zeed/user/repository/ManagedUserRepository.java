package com.zeed.user.repository;

import com.zeed.usermanagement.models.ManagedUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ManagedUserRepository extends JpaRepository<ManagedUser,Long> {

    ManagedUser findOneByUserNameAndPassword(String username, String password);

    ManagedUser findOneByUserName(String username);

}
