package com.example.welperback.dto.report.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

/**
* AI 보고서 통신 DTO (AI가 분석한 JSON 결과를 받는 DTO)
*/
@Getter
@NoArgsConstructor
public class AIAnalysisResponseDto {
    // 욕구 영역 14개 항목
    private NeedsAssessmentDto needsAssessment;
    // 척도 이용 사정
    private List<ScaleAssessmentDto> scaleAssessment;
    // 우선순위 요약
    private List<PrioritySummaryDto> prioritySummary;
    // 사정 종합의견
    private String comprehensiveOpinion;
    // 사례관리 수준
    private String caseManagementLevel;

    
    // --- 1. NeedsAssessmentDto (14가지 욕구 사정) ---
    @Getter
    @NoArgsConstructor
    public static class NeedsAssessmentDto {
        private NeedClothingDto clothing; // 의생활       private NeedFoodDto food; //식생활       private NeedDailyLivingDto dailyLiving; //일상생활
        private NeedHealthDto health; //건강
        private NeedPsychosocialDto psychosocial; // 심리정서
        private NeedJobDto job; // 직업
        private NeedEconomicDto economic; // 경제
        private NeedHousingDto housing; // 주거
        private NeedEducationDto education; // 교육(학습),진로
        private NeedCaregivingDto caregiving; // 가족관계
        private NeedRelationshipsDto relationships; // 사회적관계         private NeedFamilyFunctionDto familyFunction; // 가족기능 가족역량        private NeedSafetyDto safety; // 안전
        private NeedLegalDto legal; // 권익보장 및 법률
    }

    // --- 2. 각 욕구 영역의 세부 DTO ---

    // 2-1. 의생활
    @Getter @NoArgsConstructor
    public static class NeedClothingDto {
        private String description; private int level; private int priority;
        private String provision; private String management;
    }

    // 2-2. 식생활
    @Getter @NoArgsConstructor
    public static class NeedFoodDto {
        private String description; private int level; private int priority;
        private String mealsPerDay; private String reasonForSkipping;
        private String nutritionStatus; private String mealPreparation;
    }

    // 2-3. 일상생활
    @Getter @NoArgsConstructor
    public static class NeedDailyLivingDto {
        private String description; private int level; private int priority;
        private List<String> adl; private List<String> iadl;
        private String emergencyResponse;
    }
    
    // 2-3-Helper. 질병 DTO
    @Getter @NoArgsConstructor
    public static class DiseaseDto {
        private String name; private String hospitalized; private String medication;
    }

    // 2-4. 嫄닿컯
    @Getter @NoArgsConstructor
    public static class NeedHealthDto {
        private String description; private int level; private int priority;
        private List<DiseaseDto> diseases; private String smoking; private String drinking;
        private List<DiseaseDto> mentalDiseases; private List<String> mentalSymptoms;
    }

    // 2-5. 건강
    @Getter @NoArgsConstructor
    public static class NeedPsychosocialDto {
        private String description; private int level; private int priority;
        private List<String> psycheStatus; private List<String> emotionStatus;
        private List<String> negativeBehaviors;
    }

    // 2-6. 심리정서
    @Getter @NoArgsConstructor
    public static class NeedJobDto {
        private String description; private int level; private int priority;
        private String status; private boolean workNeed;
        private List<String> difficulties; private String satisfaction;
    }
    
    // 2-7 경제 DTO들
    @Getter @NoArgsConstructor
    public static class IncomeDto {
        private int total; private int labor; private int govSupport; private int donation; private int other;
    }
    @Getter @NoArgsConstructor
    public static class ExpenseDto {
        private int total; private int housing; private int food; private int medical; private int utilities;
    }
    @Getter @NoArgsConstructor
    public static class AssetDto {
        private int total; private int deposit; private int savings;
    }
    @Getter @NoArgsConstructor
    public static class DebtDto {
        private int total;
        private int financial;
        private int privateDebt;
        private int overdue;
        private String reason;
    }

// 2-7. 경제
@Getter @NoArgsConstructor
public static class NeedEconomicDto {
    private String description;
    private int level;
    private int priority;
    private IncomeDto income;
    private ExpenseDto expenses;
    private AssetDto assets;
    private DebtDto debt; // DebtDto瑜??ъ슜
}

    // 2-8. 주거
    @Getter @NoArgsConstructor
    public static class NeedHousingDto {
        private String description; private int level; private int priority;
        private List<String> internalEnvironment; private List<String> externalEnvironment;
    }

    // 2-9. 교육
    @Getter @NoArgsConstructor
    public static class NeedEducationDto {
        private String description; private int level; private int priority;
        private List<String> academicAbility; private List<String> educationConditions;
    }

    // 2-10. 돌봄
    @Getter @NoArgsConstructor
    public static class NeedCaregivingDto {
        private String description; private int level; private int priority;
        private String target; private String relationship; private String needs;
    }

    // 2-11. 관계
    @Getter @NoArgsConstructor
    public static class NeedRelationshipsDto {
        private String description; private int level; private int priority;
        private String familyConflict; private String familyRelations; private String neighbors; private String socialSupport;
    }

    // 2-12. 가족기능
    @Getter @NoArgsConstructor
    public static class NeedFamilyFunctionDto {
        private String description; private int level; private int priority;
        private String rolePerformance; private int familyCompetenceScore;
    }

    // 2-13. 안전
    @Getter @NoArgsConstructor
    public static class NeedSafetyDto {
        private String description; private int level; private int priority;
        private List<String> internalSafety; private List<String> externalSafety;
        private String emergencyResource;
    }

    // 2-14. 법률
    @Getter @NoArgsConstructor
    public static class NeedLegalDto {
        private String description; private int level; private int priority;
        private List<String> legalDispute; private List<String> rightsIssue;
    }

    // --- 3. 척도 이용 사정 (scaleAssessment) ---
    @Getter @NoArgsConstructor
    public static class ScaleAssessmentDto {
        private String name; private String result; private String notes;
    }

    // --- 4. 우선순위 요약 (prioritySummary) ---
    @Getter @NoArgsConstructor
    public static class PrioritySummaryDto {
        private int priority; private String area; private String presentedNeed;
        private String actualNeed; private String strengths; private String limitations;
    }
}
