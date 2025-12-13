package com.example.welperback.domain.assessment;

import com.example.welperback.domain.client.Client;
import com.example.welperback.domain.file.DocumentFile;
import com.example.welperback.global.jpa.JsonMapConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.Map;

@Getter
@Setter
@Entity
@Table(name = "assessment_record")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssessmentRecord {

    @Id
    @Column(name = "record_id")
    private String recordId;

    @ManyToOne
    @JoinColumn(name = "case_number")
    private Client client;

    private LocalDate assessmentDate;

    private String type; // NEW, REASSESSMENT

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genogram_file_id")
    private DocumentFile genogramFile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ecomap_file_id")
    private DocumentFile ecomapFile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voice_record_file_id")
    private DocumentFile voiceRecordFile;

    // ✅ 여기: Converter 제거하고 JSON 타입으로 매핑
    @JdbcTypeCode(SqlTypes.JSON)              // Hibernate에게 "이 필드는 JSON이야"라고 알려줌
    @Column(name = "checklist_data",
            columnDefinition = "jsonb")       // 실제 DB 컬럼은 jsonb 유지
    private Map<String, Object> checklistData;

    private String produceStatus; // PRODUCING, COMPLETE

    @Column(columnDefinition = "text")
    private String strengthsAndResources;

    @Column(columnDefinition = "text")
    private String comprehensiveOpinion;

    public void setGenogramFilePath(String s) {
    }

    public void setEcomapFilePath(String s) {
    }

    public void setVoiceRecordFilePath(String s) {
    }
}
