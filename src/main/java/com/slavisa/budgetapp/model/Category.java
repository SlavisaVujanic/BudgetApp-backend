package com.slavisa.budgetapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Category implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer categoryID;

    @NotBlank
    @Size(max = 30,message = "Maximum is 30 characters.")
    private String title;

    @OneToMany(mappedBy = "category")
    @JsonIgnore
    private List<Transaction> transactions = new ArrayList<>();
}
