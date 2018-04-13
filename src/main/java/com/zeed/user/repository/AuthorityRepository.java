package com.zeed.user.repository;

import com.zeed.usermanagement.models.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorityRepository extends PagingAndSortingRepository<Authority,Long> {

    Authority findById(Long id);

}
