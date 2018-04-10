package com.zeed.user.models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity (name = "managed_user_authority")
public class ManagedUserAuthority implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "managed_user_id")
    private Long managedUserId;

    @NotNull
    @Column(name = "authority_id")
    private Long authorityId;

}
