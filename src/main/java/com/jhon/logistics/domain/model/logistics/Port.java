package com.jhon.logistics.domain.model.logistics;

import lombok.*;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Port {
    private Long   id;
    private String name;
    private String city;
    private String country;
    private String portType; // N = national, I = international
    private String status;
}
