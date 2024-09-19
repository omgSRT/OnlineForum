package com.FA24SE088.OnlineForum.configuration;

import com.FA24SE088.OnlineForum.entities.Account;
import com.FA24SE088.OnlineForum.entities.Role;
import com.FA24SE088.OnlineForum.enums.AccountStatus;
import com.FA24SE088.OnlineForum.repositories.AccountRepository;
import com.FA24SE088.OnlineForum.repositories.RoleRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfiguration {

    PasswordEncoder passwordEncoder;
    AccountRepository accountRepository;
    RoleRepository roleRepository;

    @Autowired
    public ApplicationInitConfiguration(AccountRepository accountRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    private boolean checkRole() {
        if (roleRepository.findByName("ADMIN") != null &&
                roleRepository.findByName("STAFF") != null &&
                roleRepository.findByName("GUEST") != null &&
                roleRepository.findByName("USER") != null) {
            return true;
        }
        return false;
    }

    @Bean
    @Order(1)
    @Transactional
        // Đảm bảo dữ liệu được lưu ngay lập tức
    ApplicationRunner createAutoRole() {
        return args -> {
            if(!checkRole()){
                Role admin = new Role();
                admin.setName("ADMIN");
                roleRepository.save(admin);

                Role staff = new Role();
                staff.setName("STAFF");
                roleRepository.save(staff);

                Role user = new Role();
                user.setName("USER");
                roleRepository.save(user);

                Role guest = new Role();
                guest.setName("GUEST");
                roleRepository.save(guest);

            }
        };
    }

    @Bean
    @Order(2)
    ApplicationRunner applicationRunner() {
        return args -> {
            Role role1 = roleRepository.findByName("ADMIN");
            if (role1 == null) throw new RuntimeException("Chưa có role admin");

            if (accountRepository.findByUsername("admin").isEmpty()) {
                Account user = Account.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin"))
                        .role(role1)
                        .createdDate(new Date())
                        .status(AccountStatus.ACTIVE.name())
                        .build();
                accountRepository.save(user);
                log.warn("admin user has been created with default password: admin");
            }
        };
    }
}