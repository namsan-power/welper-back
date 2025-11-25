package com.example.welperback.domain.report;

import com.example.welperback.domain.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long id;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "voice_record", length = 255)
    private String voiceRecord;

    @Column(name = "report_file", length = 255)
    private String reportFile;

    @Column(name = "preview_html")
    private String previewHtml;

    @OneToMany(mappedBy = "report")
    private List<ReportClient> reportClients = new ArrayList<>();

    @OneToMany(mappedBy = "report")
    private List<ReportParticipant> participants = new ArrayList<>();

    protected Report() {
    }

    public Report(String title, User author) {
        this.title = title;
        this.author = author;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getVoiceRecord() {
        return voiceRecord;
    }

    public void setVoiceRecord(String voiceRecord) {
        this.voiceRecord = voiceRecord;
    }

    public String getReportFile() {
        return reportFile;
    }

    public void setReportFile(String reportFile) {
        this.reportFile = reportFile;
    }

    public String getPreviewHtml() {
        return previewHtml;
    }

    public void setPreviewHtml(String previewHtml) {
        this.previewHtml = previewHtml;
    }

    public List<ReportClient> getReportClients() {
        return reportClients;
    }

    public List<ReportParticipant> getParticipants() {
        return participants;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Report report)) {
            return false;
        }
        return id != null && id.equals(report.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
