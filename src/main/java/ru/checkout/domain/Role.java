package ru.checkout.domain;

import javax.persistence.*;

@Entity
@Table(name = "sec_role")
public class Role {

    public static final String ROLE_VIEW_ORDERS = "ROLE_VIEW_ORDERS";
    public static final String ROLE_SEARCH_ORDERS = "ROLE_SEARCH_ORDERS";
    public static final String ROLE_AUTHORISED_USER = "ROLE_AUTHORISED_USER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(unique = true, nullable = false, length = 64)
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
