package com.proyecto.matricula.config.security;

import com.proyecto.matricula.entity.Usuario;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
public class CustomUserDetails implements UserDetails {

    private final Integer idUsuario;
    private final String username;
    private final String password;
    private final String rolNombre;
    private final String nombreCompleto;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(Usuario usuario) {
        this.idUsuario = usuario.getIdUsuario();
        this.username = usuario.getUsername();
        this.password = usuario.getPassword();
        this.rolNombre = usuario.getRol().getNombreRol();
        this.nombreCompleto = usuario.getPersona().getNombres() + " " + usuario.getPersona().getApellidoPaterno();
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + rolNombre));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
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
