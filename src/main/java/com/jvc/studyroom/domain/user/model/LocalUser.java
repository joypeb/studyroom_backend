package com.jvc.studyroom.domain.user.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Table;
@Setter
@Getter
@Table("users")
public class LocalUser extends BaseUser{
    private String password;
    private String email;

    public LocalUser() {
        this.provider = LoginProvider.LOCAL;
    }
}
