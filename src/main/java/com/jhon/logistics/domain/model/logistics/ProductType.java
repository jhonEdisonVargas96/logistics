package com.jhon.logistics.domain.model.logistics;

import lombok.*;

@Getter
@Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class ProductType {
    private Long   id;
    private String name;
    private String description;
    private String status;
}
