package com.example.coffe_shop.auth.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

@Getter
@AllArgsConstructor
public class UserPrincipal  implements UserDetails {

    private final String id;       // ID người dùng (từ database)
    private final String email;    // Email
    private final String password; // Password (đã mã hóa)
    private final String role;     // Vai trò (USER, ADMIN)
    private final boolean active;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Trả về role của user để Spring phân quyền
        return Collections.singleton(() -> role);
    }

    @Override
    public String getUsername() {
        // Spring sẽ dùng username để login - ở đây là email
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        // true: tài khoản không bị hết hạn
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // true: tài khoản không bị khóa
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // true: mật khẩu không bị hết hạn
        return true;
    }

    @Override
    public boolean isEnabled() {
        // kiểm tra user active hay không
        return active;
    }

}
