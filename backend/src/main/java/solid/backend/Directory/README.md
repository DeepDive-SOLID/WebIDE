### 디렉터리 관리 : directory
- controller(컨트롤러)
    - DirectoryController.java
- dto(객체정보)
    - DirectoryDelDto.java
    - DirectoryDto.java
    - DirectoryListDto.java
    - DirectoryUpdDto.java
- repository(jpa)
    - DirectoryRepository.java
    - ContainerRepository.java
    - TeamRepository.java
- service(비즈니스 로직)
    - DirectoryService.java
    - DirectoryServiceImpl.java

### API 목록
[디렉터리 조회]
- HTTP method : POST
- HTTP request URL : directory/list
- param : directoryListDto
- return : List<DirectoryDto>

[디렉터리 생성]
- HTTP method : POST
- HTTP request URL : directory/create
- param : directoryDto
- return : DirectoryDto

[디렉터리 이름 변경]
- HTTP method : PUT
- HTTP request URL : directory/rename
- param : directoryUpdDto
- return : ResponseEntity<String>

[디렉터리 삭제]
- HTTP method : DELETE
- HTTP request URL : directory/delete
- param : directoryDelDto
- return : ResponseEntity<String>
