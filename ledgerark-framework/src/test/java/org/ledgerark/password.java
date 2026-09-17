package org.ledgerark;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

public class password {




    @Resource
    private PasswordEncoder passwordEncoder;


    @Test
    public void testPasswordEncoder() {
        String password = "123456";
        String encodedPassword = passwordEncoder.encode(password);
        System.out.println(encodedPassword);
        System.out.println(passwordEncoder.matches(password, encodedPassword));
    }
}
