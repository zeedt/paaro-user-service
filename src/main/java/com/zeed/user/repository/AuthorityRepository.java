package com.zeed.user.repository;

import com.zeed.user.models.Authority;
import com.zeed.user.models.AuthorityManagedUser;
import com.zeed.user.models.ManagedUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorityRepository extends PagingAndSortingRepository<Authority,Long> {

    Authority findById(Long id);

}
