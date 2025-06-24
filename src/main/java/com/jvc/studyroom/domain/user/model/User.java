package com.jvc.studyroom.domain.user.model;

//@Table("users") // table 네이밍 기준은 복수형임

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter // Setter 안 쓰고 생성자 기법 쓸까 하고 있음
public class User {
    //@Id
    private Long id;
    private String name;
    private String email;
}
