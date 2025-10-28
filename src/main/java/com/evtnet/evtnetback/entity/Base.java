package com.evtnet.evtnetback.entity;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Base implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
}
