### 코드 파일 관리 : codeFile
- controller(컨트롤러)
    - CodeFileController.java
- dto(객체정보)
    - CodeFileDelDto.java
    - CodeFileListDto.java
    - CodeFileSaveDto.java
    - CodeFileUpdDto.java
- repository(jpa)
    - CodeFileRepository.java
    - DirectoryRepository.java
- service(비즈니스 로직)
    - CodeFileService.java
    - CodeFileServiceImpl.java

### API 목록
[코드 파일 조회]
- HTTP method : GET
- HTTP request URL : CodeFile/list
- return : List<CodeFileListDto>

[코드 파일 내용 조회]
- HTTP method : POST
- HTTP request URL : CodeFile/content
- return : String

[코드 파일 생성]
- HTTP method : POST
- HTTP request URL : CodeFile/create
- param : CodeFileSaveDto
- return : ResponseEntity<String>

[코드 파일 내용 변경]
- HTTP method : PUT
- HTTP request URL : CodeFile/update
- param : CodeFileUpdDto
- return : ResponseEntity<String>

[코드 파일 삭제]
- HTTP method : DELETE
- HTTP request URL : CodeFile/delete
- param : CodeFileDelDto
- return : ResponseEntity<String>
