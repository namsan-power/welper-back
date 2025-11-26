package com.example.welperback.domain.assessment;

import com.example.welperback.domain.client.Client;
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
    private String recordId;

    @ManyToOne
    @JoinColumn(name = "caseNumber")
    private Client client;

    private LocalDate assessmentDate;

    private String type; // NEW, REASSESSMENT

    private String genogramFilePath;
    private String ecomapFilePath;
    private String voiceRecordFilePath;

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
}
