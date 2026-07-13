package com.proyecto.matricula;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestBCrypt {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "admin123";
        String encoded = encoder.encode(rawPassword);
        System.out.println("NUEVO_HASH:" + encoded);
        System.out.println("MATCH_OLD:" + encoder.matches(rawPassword, "$2a$10$8.UnVuG9HHgffUDAlk8GP.3n.K15C5YnFh/w7oSPPphc80p6n/Xyq"));
    }
}
