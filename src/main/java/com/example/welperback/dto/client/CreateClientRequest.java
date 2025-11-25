package com.example.welperback.dto.client;

import java.time.LocalDate;

public class CreateClientRequest {
	// 접수일(관리 시작일). 미전달 시 서버가 오늘 날짜로 세팅
	public LocalDate registrationDate;
	// 성명 (필수)
	public String name;
	// 생년월일
	public LocalDate birthDate;
	// 연락처
	public String phoneNumber;
	// 주소
	public String address;
	// 성별: "남"/"여" 또는 "MALE"/"FEMALE"
	public String sex;
	// 접수 경로: "온라인"|"전화"|"방문"|"기타" 또는 "ONLINE"|"CALL"|"VISIT"|"OTHER"
	public String referralSource;
	// 접수 요청 내용
	public String requestContent;
}


