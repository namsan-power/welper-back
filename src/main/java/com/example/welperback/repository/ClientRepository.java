package com.example.welperback.repository;

import com.example.welperback.domain.client.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, String> {
    // caseNumber가 PK인 경우 이대로 사용
}
