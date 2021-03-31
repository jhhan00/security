package com.example.security.Entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name="userTable")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    @Column(length = 4096)
    private String password;

    private Integer enabled;
    private String realName;

    @Column(length = 32)
    private String role;
}
