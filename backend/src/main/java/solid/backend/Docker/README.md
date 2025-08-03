### 도커 컨테이너 환경에서 코드 실행 : Docker
- controller(컨트롤러)
    - DockerController.java
- dto(객체정보)
    - CustomInputDto.java - 사용자 입력 실행 DTO
    - CustomInputResultDto.java - 사용자 입력 실행 결과 DTO
    - DockerResultDto.java - 도커 실행 결과 DTO
    - DockerRunDto.java - 도커 실행 요청 DTO
    - ExecutionResultDto.java - 코드 실행 결과 DTO (진행률 포함)
    - ExecutionTestDto.java - 테스트 실행 결과 DTO
    - TestCaseResultDto.java - 테스트케이스 결과 DTO
- repository(jpa)
    - CodeFileRepository.java
    - TestCaseRepository.java
    - ResultRepository.java
    - QuestionRepository.java
    - MemberRepository.java
    - ProgressRepository.java - 언어별 진행률 저장
    - TeamUserRepository.java
- service(비즈니스 로직)
    - DockerService.java
    - DockerServiceImpl.java - 코드 실행 및 언어별 진행률 자동 업데이트

### API 목록
[코드 파일 도커 컨테이너에서 실행]
- HTTP method : POST
- HTTP request URL : docker/run
- param : dockerRunDto (memberId, codeFileId, questionId)
- return : ResponseEntity<ExecutionResultDto>
- 설명 : 모든 테스트케이스 실행 및 언어별 진행률 자동 업데이트

[테스트케이스 실행]
- HTTP method : POST
- HTTP request URL : docker/test
- param : dockerRunDto (memberId, codeFileId, questionId)
- return : ResponseEntity<ExecutionTestDto>
- 설명 : 공개된 테스트케이스만 실행 및 진행률 업데이트

[사용자 입력으로 코드 실행]
- HTTP method : POST
- HTTP request URL : docker/custom
- param : customInputDto (codeFileId, questionId, input)
- return : ResponseEntity<CustomInputResultDto>
- 설명 : 사용자가 제공한 입력값으로 코드 실행 (진행률 업데이트 없음)
