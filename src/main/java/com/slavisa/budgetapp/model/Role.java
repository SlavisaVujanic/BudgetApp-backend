package com.slavisa.budgetapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Role implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer roleID;

    @NotBlank
    @Size(max = 10, message = "Maximum is 10 characters.")
    private String roleName;

    @OneToMany(mappedBy = "role")
    @JsonIgnore
    private List<Account> users;
}
