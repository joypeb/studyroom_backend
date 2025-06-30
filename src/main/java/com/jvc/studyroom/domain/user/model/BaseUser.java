package com.jvc.studyroom.domain.user.model;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Setter
@Getter
@Table("users")
public abstract class BaseUser {
    @Id
    protected Long id;
    protected String userId; // 유저 ID
    protected LoginProvider provider; //  로그인 서비스 제공자
    protected UserRole role;
}
