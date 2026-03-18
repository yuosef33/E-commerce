package com.yuosef.e_commerce.config;

import com.yuosef.e_commerce.repository.AuthorityDao;
import com.yuosef.e_commerce.models.Authority;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
@Configuration
public class AuthorityConfig {
    /// this class works at runtime ro create USER & ADMIN roles
    @Bean
    CommandLineRunner initRoles(AuthorityDao repo) {
        return args -> {

            if (!repo.existsByUserRole("USER")) {
                repo.save(new Authority(null, "USER", new ArrayList<>()));
            }

            if (!repo.existsByUserRole("ADMIN")) {
                repo.save(new Authority(null, "ADMIN", new ArrayList<>()));
            }
        };
    }
}
