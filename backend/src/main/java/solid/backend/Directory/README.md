### 디렉터리 관리 : directory
- controller(컨트롤러)
    - DirectoryController.java
- dto(객체정보)
    - DirectoryDelDto.java - 디렉터리 삭제용 DTO
    - DirectoryDto.java - 디렉터리 기본 DTO (hasQuestion 필드 추가)
    - DirectoryListDto.java - 디렉터리 목록 조회용 DTO
    - DirectoryUpdDto.java - 디렉터리 수정용 DTO
- repository(jpa)
    - DirectoryRepository.java - Question과의 관계 매핑 추가
    - ContainerRepository.java
    - TeamRepository.java
    - QuestionRepository.java - 디렉터리 내 문제 존재 여부 확인
- service(비즈니스 로직)
    - DirectoryService.java
    - DirectoryServiceImpl.java - 경로 정규화 및 트랜잭션 최적화

### API 목록
[디렉터리 조회]
- HTTP method : POST
- HTTP request URL : directory/list
- param : directoryListDto
- return : List<DirectoryDto>
- 설명 : 컨테이너 내 디렉터리 목록 조회 (hasQuestion 필드로 문제 존재 여부 확인 가능)

[디렉터리 생성]
- HTTP method : POST
- HTTP request URL : directory/create
- param : directoryDto
- return : DirectoryDto
- 설명 : 새 디렉터리 생성 (경로 정규화 적용, 중복 슬래시 자동 제거)

[디렉터리 이름 변경]
- HTTP method : PUT
- HTTP request URL : directory/rename
- param : directoryUpdDto
- return : ResponseEntity<String>
- 설명 : 디렉터리 이름 변경

[디렉터리 삭제]
- HTTP method : DELETE
- HTTP request URL : directory/delete
- param : directoryDelDto
- return : ResponseEntity<String>
- 설명 : 디렉터리 삭제 (하위 디렉터리 포함)
