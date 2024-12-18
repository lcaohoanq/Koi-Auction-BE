package com.swp391.koibe.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.swp391.koibe.enums.UserStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@Entity
@Builder
public class User extends BaseEntity implements UserDetails {

    @Id
    @SequenceGenerator(name = "users_seq", sequenceName = "users_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_seq")
    @Column(name="id", unique=true, nullable=false)
    @JsonProperty("id")
    private Long id;

    @Column(name="first_name", length = 100)
    @JsonProperty("first_name")
    private String firstName;

    @Column(name="last_name", length = 100)
    @JsonProperty("last_name")
    private String lastName;

    @Column(name="phone_number", length = 20)
    @JsonProperty("phone_number")
    private String phoneNumber;

    @Column(name="email",nullable = false, length = 100)
    @JsonProperty("email")
    private String email;

    @Column(name="address", length = 200)
    @JsonProperty("address")
    private String address;

    @Column(name="password", length = 200)
    @JsonProperty("password")
    private String password;

    @Transient //do not save to database, check if needed
    @JsonIgnore
    private String confirmPassword;

    @Column(name="is_active", columnDefinition = "TINYINT(1) DEFAULT 1")
    @JsonProperty("is_active")
    private boolean isActive;

    @Column(name="is_subscription", columnDefinition = "TINYINT(1) DEFAULT 0")
    @JsonProperty("is_subscription")
    private boolean isSubscription;

    @Enumerated(EnumType.STRING)
    @Column(name="status")
    @JsonProperty("status")
    private UserStatus status;

    @Column(name="date_of_birth")
    @JsonProperty("date_of_birth")
    private Date dob;

    @Column(name="avatar_url", length=1024)
    @JsonProperty("avatar_url")
    private String avatarUrl;

    @Column(name="google_account_id", columnDefinition = "INT(11)")
    @JsonProperty("google_account_id")
    private int googleAccountId;

    @ManyToOne
    @JoinColumn(name="role_id")
    private Role role;

    @Column(name="account_balance", columnDefinition = "BIGINT(20) DEFAULT 0")
    @JsonProperty("account_balance")
    private long accountBalance;

//    @ManyToOne
//    @JoinColumn(name = "department_id")
//    private Department department;

    //Spring Security
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();
        authorityList.add(new SimpleGrantedAuthority("ROLE_"+getRole().getUserRole().name()));
        //authorityList.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

        return authorityList;
    }

    //why getUserName() is return email
    //because in the UserDetailsService, we use email to find user
    @Override
    public String getUsername() {
        return email;
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
