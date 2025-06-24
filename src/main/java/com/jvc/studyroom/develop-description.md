- 해당 파일은 개발자 규칙을 적을 메모장입니다.
- 개발 진행하며 같이 정해진 사항을 적어 규칙으로 지키며 개발을 진행합니다.
## 고민점
- Model 계층에서 Setter 안 쓰고 생성자 기법으로 갈까요? 
- 
## package-description
```
- config
- domain
    - user
        - controller 
            - dto
        - service : 인터페이스, 구현체 
        - repository : 인터페이스, 구현체 
        - model : Entity, VO 
        - converter : 고민중인데 DTO <-> Entity 하는 공간 
    - studytime
- global : 아래 하위 경로는 실제 만들진 않고 이런 내용이 있을 것으로 예상되어 기재
    - exception
    - filter
    - log (aop)
    - response
```

## structure-description
- service, repository 계층은 인터페이스랑 구현체 나눠주세요 (아래는 네이밍 추천)
  - LoginService (인터페이스) - LoginServiceImpl (구현체)
  - LoginService (인터페이스) - KaKaoLoginService (구현체)