package com.ticketing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TicketingSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(TicketingSystemApplication.class, args);
        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║   ระบบจองตั๋วและกำหนดที่นั่ง                           ║");
        System.out.println("║   Ticketing System with OOP Principles                ║");
        System.out.println("╠════════════════════════════════════════════════════════╣");
        System.out.println("║   Server running on: http://localhost:8080            ║");
        System.out.println("║   API Base URL: http://localhost:8080/api             ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
    }
}