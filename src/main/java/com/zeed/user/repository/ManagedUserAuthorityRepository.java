package com.zeed.user.repository;

import com.zeed.usermanagement.models.ManagedUserAuthority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ManagedUserAuthorityRepository extends JpaRepository<ManagedUserAuthority,Long> {

    List<Long> findAllByManagedUserId(Long id);

}
