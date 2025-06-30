package com.jvc.studyroom.domain.user.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Table;

@Setter
@Getter
@Table("users")
public class KaKaoUser extends BaseUser {
    private String kakaoId;

    public KaKaoUser() {
        this.provider = LoginProvider.KAKAO;
    }
}
