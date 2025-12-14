package com.openticket.admin.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.openticket.admin.entity.Role;

@Component
public class LoginCompanyProvider {

    public Long getCompanyId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof AccountPrincipal principal) {
            return principal.getCompanyId();
        }
        return null;
    }

    public Role getRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof AccountPrincipal principal) {
            return principal.getRoleEnum();
        }
        return null;
    }
}
