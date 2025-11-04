package com.example.welperback.dto.report.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

/**
* AI 紐⑤뜽??JSON ?묐떟??1:1濡?留ㅽ븨?섎뒗 DTO
*/
@Getter
@NoArgsConstructor
public class AIAnalysisResponseDto {
    private NeedsAssessmentDto needsAssessment;
    private List<ScaleAssessmentDto> scaleAssessment;
    private List<PrioritySummaryDto> prioritySummary;
    private String comprehensiveOpinion;
    private String caseManagementLevel;

    
    // --- 1. NeedsAssessmentDto (14媛??뺢뎄 ?곸뿭) ---
    @Getter
    @NoArgsConstructor
    public static class NeedsAssessmentDto {
        private NeedClothingDto clothing; // ?섏깮??        private NeedFoodDto food; // ?앹깮??        private NeedDailyLivingDto dailyLiving; // ?쇱긽?앺솢
        private NeedHealthDto health; // 嫄닿컯
        private NeedPsychosocialDto psychosocial; // ?щ━?뺤꽌
        private NeedJobDto job; // 吏곸뾽
        private NeedEconomicDto economic; // 寃쎌젣
        private NeedHousingDto housing; // 二쇨굅
        private NeedEducationDto education; // 援먯쑁
        private NeedCaregivingDto caregiving; // ?뚮큵
        private NeedRelationshipsDto relationships; // 媛議?諛??ы쉶??愿怨?        private NeedFamilyFunctionDto familyFunction; // 媛議깃린??        private NeedSafetyDto safety; // ?덉쟾
        private NeedLegalDto legal; // 沅뚯씡蹂댁옣 諛?踰뺣쪧
    }

    // --- 2. 媛??뺢뎄 ?곸뿭???몃? DTO (泥댄겕由ъ뒪???ы븿) ---

    // 2-1. ?섏깮??    @Getter @NoArgsConstructor
    public static class NeedClothingDto {
        private String description; private int level; private int priority;
        private String provision; private String management;
    }

    // 2-2. ?앹깮??    @Getter @NoArgsConstructor
    public static class NeedFoodDto {
        private String description; private int level; private int priority;
        private String mealsPerDay; private String reasonForSkipping;
        private String nutritionStatus; private String mealPreparation;
    }

    // 2-3. ?쇱긽?앺솢
    @Getter @NoArgsConstructor
    public static class NeedDailyLivingDto {
        private String description; private int level; private int priority;
        private List<String> adl; private List<String> iadl;
        private String emergencyResponse;
    }
    
    // 2-3-Helper. 吏덈퀝 DTO
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

    // 2-5. ?щ━?뺤꽌
    @Getter @NoArgsConstructor
    public static class NeedPsychosocialDto {
        private String description; private int level; private int priority;
        private List<String> psycheStatus; private List<String> emotionStatus;
        private List<String> negativeBehaviors;
    }

    // 2-6. 吏곸뾽
    @Getter @NoArgsConstructor
    public static class NeedJobDto {
        private String description; private int level; private int priority;
        private String status; private boolean workNeed;
        private List<String> difficulties; private String satisfaction;
    }
    
    // 2-7-Helper. 寃쎌젣 DTO??    @Getter @NoArgsConstructor
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

// 2-7. 寃쎌젣
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

    // 2-8. 二쇨굅
    @Getter @NoArgsConstructor
    public static class NeedHousingDto {
        private String description; private int level; private int priority;
        private List<String> internalEnvironment; private List<String> externalEnvironment;
    }

    // 2-9. 援먯쑁
    @Getter @NoArgsConstructor
    public static class NeedEducationDto {
        private String description; private int level; private int priority;
        private List<String> academicAbility; private List<String> educationConditions;
    }

    // 2-10. ?뚮큵
    @Getter @NoArgsConstructor
    public static class NeedCaregivingDto {
        private String description; private int level; private int priority;
        private String target; private String relationship; private String needs;
    }

    // 2-11. 愿怨?    @Getter @NoArgsConstructor
    public static class NeedRelationshipsDto {
        private String description; private int level; private int priority;
        private String familyConflict; private String familyRelations; private String neighbors; private String socialSupport;
    }

    // 2-12. 媛議깃린??    @Getter @NoArgsConstructor
    public static class NeedFamilyFunctionDto {
        private String description; private int level; private int priority;
        private String rolePerformance; private int familyCompetenceScore;
    }

    // 2-13. ?덉쟾
    @Getter @NoArgsConstructor
    public static class NeedSafetyDto {
        private String description; private int level; private int priority;
        private List<String> internalSafety; private List<String> externalSafety;
        private String emergencyResource;
    }

    // 2-14. 踰뺣쪧
    @Getter @NoArgsConstructor
    public static class NeedLegalDto {
        private String description; private int level; private int priority;
        private List<String> legalDispute; private List<String> rightsIssue;
    }

    // --- 3. 泥숇룄 ?댁슜 ?ъ젙 (scaleAssessment) ---
    @Getter @NoArgsConstructor
    public static class ScaleAssessmentDto {
        private String name; private String result; private String notes;
    }

    // --- 4. ?곗꽑?쒖쐞 ?붿빟 (prioritySummary) ---
    @Getter @NoArgsConstructor
    public static class PrioritySummaryDto {
        private int priority; private String area; private String presentedNeed;
        private String actualNeed; private String strengths; private String limitations;
    }
}
