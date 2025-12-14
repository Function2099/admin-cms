package com.openticket.admin.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.openticket.admin.entity.Role;
import com.openticket.admin.entity.User;

public class AccountPrincipal implements UserDetails {

    private final User user;

    public AccountPrincipal(User user) {
        this.user = user;
    }

    public AccountPrincipal(Long companyId, String role) {
        User tempUser = new User();
        tempUser.setId(companyId);
        tempUser.setRoleEnum(Role.valueOf(role)); // 假設 role 是 "ADMIN" / "ORGANIZER"
        this.user = tempUser;
    }

    public Long getCompanyId() {
        return user.getId(); // 或者你有 companyId 欄位就用那個
    }

    public Role getRoleEnum() {
        return user.getRoleEnum();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Role role = user.getRoleEnum();
        return role != null ? List.of(new SimpleGrantedAuthority("ROLE_" + role.name())) : List.of();
    }

    @Override
    public String getPassword() {
        return user.getPasswd();
    }

    @Override
    public String getUsername() {
        return user.getAccount();
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
        return user.getIsActive() != null ? user.getIsActive() : true;
    }
}
