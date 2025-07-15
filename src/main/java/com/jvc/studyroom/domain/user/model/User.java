package com.jvc.studyroom.domain.user.model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter // Setter 안 쓰고 생성자 기법 쓸까 하고 있음
public class User {
    //@Id
    private UUID userId;
    private String name;
    private String email;
}
