### 진행률 계산 : Progress
- controller(컨트롤러)
    - ProgressController.java
- dto(객체정보)
    - ProgressDto.java - 진행률 업데이트용 DTO (언어별 진행률 지원)
    - ProgressListDto.java - 진행률 목록 조회용 DTO
    - DirectoryProgressDto.java - 디렉토리별 진행률 DTO
    - QuestionProgressDto.java - 문제별 진행률 DTO
- repository(jpa)
    - ProgressRepository.java - 언어별 진행률 조회/저장 (findByDirectoryAndTeamUserAndLanguage 추가)
    - DirectoryRepository.java
    - TeamUserRepository.java
    - QuestionRepository.java
    - ResultRepository.java
    - MemberRepository.java
    - CodeFileRepository.java
- repository(Query Dsl)
    - ProgressQueryRepository.java - 배치 조회 최적화 쿼리
- service(비즈니스 로직)
    - ProgressService.java
    - ProgressServiceImpl.java - 언어별 진행률 관리 및 배치 처리 최적화

### API 목록
[디렉터리별 진행률 조회]
- HTTP method : GET
- HTTP request URL : progress/directory/{directoryId}
- param : directoryId
- return : List<ProgressListDto>
- 설명 : 디렉토리 내 모든 멤버의 언어별 진행률 조회

[진행률 업데이트]
- HTTP method : POST
- HTTP request URL : progress/update
- param : progressDto (directoryId, teamUserId, progressComplete, language)
- return : ResponseEntity<String>
- 설명 : 특정 언어의 진행률 업데이트

[컨테이너 내 모든 멤버의 진행률 조회]
- HTTP method : GET
- HTTP request URL : progress/container/{containerId}
- param : containerId
- return : List<ProgressListDto>
- 설명 : 컨테이너 내 모든 멤버의 언어별 진행률 조회 (문제가 없는 경우 멤버 정보만 반환)

[컨테이너의 모든 문제별 진행률 조회]
- HTTP method : GET
- HTTP request URL : progress/container/{containerId}/member/{memberId}/questions
- param : containerId, memberId
- return : List<QuestionProgressDto>
- 설명 : 특정 멤버의 문제별 진행률 조회 (모든 언어 중 최고 진행률 표시)

[특정 디렉터리의 멤버 진행률 조회]
- HTTP method : GET
- HTTP request URL : progress/directory/{directoryId}/member/{memberId}
- param : directoryId, memberId
- return : Map<String, Object>
- 설명 : 특정 멤버의 디렉토리 진행률 조회 (모든 언어 중 최고 진행률 반환)