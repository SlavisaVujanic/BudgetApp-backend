package com.slavisa.BudgetApp.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Account implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer accountID;

    @NotBlank
    @Size(max = 100, message = "Maximum is 100 characters.")
    private String firstName;

    @NotBlank
    @Size(max = 100, message = "Maximum is 100 characters.")
    private String lastName;

    @NotBlank
    @Column(unique = true)
    @Size(max = 100, message = "Maximum is 50 characters.")
    private String username;

    @Email
    @Column(unique = true)
    @Size(max = 100, message = "Maximum is 100 characters.")
    private String email;

    @NotBlank
    @Size(min = 5, message = "Minimum is 5 characters.")
    private String password;

    @ManyToOne
    @JoinColumn(name = "RoleID")
    private Role role;

    @CreationTimestamp
    @Column(name = "CreatedAt", updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate createdAt;

    @OneToMany(mappedBy = "account")
    @JsonIgnore
    private List<Transaction> transactions = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (role != null) {
            return List.of(new SimpleGrantedAuthority(role.getRoleName()));
        }
        return List.of();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
