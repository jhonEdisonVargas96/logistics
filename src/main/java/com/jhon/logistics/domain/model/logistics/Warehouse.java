package com.jhon.logistics.domain.model.logistics;

import lombok.*;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Warehouse {
    private Long   id;
    private String name;
    private String address;
    private String city;
    private String status;
}
