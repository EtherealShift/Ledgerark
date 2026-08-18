package org.ledgerark.admin;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("org.ledgerark.system.mapper")
@SpringBootApplication(scanBasePackages = "org.ledgerark")
public class LedgerarkAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(LedgerarkAdminApplication.class, args);
    }

}
