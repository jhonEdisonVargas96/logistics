package com.jhon.logistics.domain.model.logistics;

import lombok.*;

@Getter
@Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Client {
    private Long   id;
    private String name;
    private String email;
    private String phone;
    private String status;
}
