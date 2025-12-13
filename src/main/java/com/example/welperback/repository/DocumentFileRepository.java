package com.example.welperback.repository;

import com.example.welperback.domain.file.DocumentFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentFileRepository extends JpaRepository<DocumentFile, String> {

    List<DocumentFile> findByClient_CaseNumberAndStatus(String caseNumber, String status);
}

