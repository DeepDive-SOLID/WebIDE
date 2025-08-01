### 도커 컨테이너 환경에서 코드 실행 : Docker
- controller(컨트롤러)
    - DockerController.java
- dto(객체정보)
    - CustomInputDto.java
    - CustomInputResultDto.java
    - DockerResultDto.java
    - DockerRunDto.java
    - ExecutionResultDto.java
    - ExecutionTestDto.java
    - TestCaseResultDto.java
- repository(jpa)
    - CodeFileRepository.java
    - TestCaseRepository.java
    - ResultRepository.java
    - QuestionRepository.java
    - MemberRepository.java
- service(비즈니스 로직)
    - DockerService.java
    - DockerServiceImpl.java

### API 목록
[코드 파일 도커 컨테이너에서 실행]
- HTTP method : POST
- HTTP request URL : docker/run
- param : dockerRunDto
- return : ResponseEntity<ExecutionResultDto>

[코드 파일 내용 조회]
- HTTP method : POST
- HTTP request URL : docker/test
- param dockerRunDto
- return : ResponseEntity<ExecutionResultDto>

[코드 파일 생성]
- HTTP method : POST
- HTTP request URL : docker/custom
- param : customInputDto
- return : ResponseEntity<CustomInputResultDto>
