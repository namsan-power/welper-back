package com.example.welperback.repository.client;

import com.example.welperback.domain.client.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    List<Client> findByReceivedBy_Id(Long userId);
}
