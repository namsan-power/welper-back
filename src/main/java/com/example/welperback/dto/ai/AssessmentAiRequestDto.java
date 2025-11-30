package com.example.welperback.dto.ai;

import java.util.List;

public record AssessmentAiRequestDto(
        String caseNumber,
        String socialWorkerId,
        String voiceFileUrl,
        IntakeData intakeData
) {
    public record IntakeData(
            String assignedSocialWorkerName,
            String interviewDate,
            int sessionNumber,
            String referralSource,
            String clientName,
            String gender,
            List<FamilyMember> familyMembers,
            String needsDescription,
            String intakeResult
    ) {}

    public record FamilyMember(
            String relationship,
            String name
    ) {}

}
