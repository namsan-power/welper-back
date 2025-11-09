package com.example.welperback.domain.user;

import com.example.welperback.domain.client.Client;
import com.example.welperback.domain.report.Report;
import com.example.welperback.domain.report.ReportParticipant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Setter;

@Entity
@Setter
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "email", nullable = false, length = 100, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "receivedBy")
    private List<Client> receivedClients = new ArrayList<>();

    @OneToMany(mappedBy = "author")
    private List<Report> authoredReports = new ArrayList<>();

    @OneToMany(mappedBy = "participant")
    private List<ReportParticipant> reportParticipations = new ArrayList<>();

    protected User() {
    }

    public User(String email, String passwordHash, String name, String phoneNumber, LocalDateTime createdAt) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void updatePassword(String encodedPassword) {
        this.passwordHash = encodedPassword;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<Client> getReceivedClients() {
        return receivedClients;
    }

    public List<Report> getAuthoredReports() {
        return authoredReports;
    }

    public List<ReportParticipant> getReportParticipations() {
        return reportParticipations;
    }

    //JPA 엔티티가 DB 식별자(id)를 기준으로
    //"논리적으로 같은 객체인지" 안전하게 비교할 수 있게 해주는 코드입니다.
    //특히 컬렉션(Set, Map)이나 영속성 컨텍스트 내부에서 엔티티 중복을 방지하는 핵심 역할을 합니다.
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User user)) {
            return false;
        }
        return id != null && id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
