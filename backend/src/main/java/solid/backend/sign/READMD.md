### 로그인, 회원가입 기능 : sign
- controller(컨트롤러)
    - SignController.java
- dto(객체정보)
    - SignCheckIdEmailDto.java
    - SignFindIdDto.java
    - SignInDto.java
    - SignUpCheckEmailDto.java
    - SignUpCheckIdDto.java
    - SignUpdPwDto.java
    - SignUpDto.java
    - SignApiDto.java
- repository(jpa)
    - SignRepository.java
- service(비즈니스 로직)
    - SignService.java
    - SignServiceImpl.java

### API 목록
[회원가입]
- HTTP method : POST
- HTTP request URL : /sign/signUp
- param : signUpDto
- return : ResponseEntity<String>

[회원가입 아이디 중복 확인]
- HTTP method : POST
- HTTP request URL : /sign/checkId
- param : signInCheckIdDto
- return : ResponseEntity<Boolean> (아이디가 있으면 true, 없으면 false)

[회원가입 이메일 중복 확인]
- HTTP method : POST
- HTTP request URL : /sign/checkEmail
- param : signInCheckEmailDto
- return : ResponseEntity<Boolean> (아이디가 있으면 true, 없으면 false)

[로그인]
- HTTP method : POST
- HTTP request URL : /sign/login
- param : signInDto
- return : ResponseEntity<String>

[아이디 찾기]
- HTTP method : POST
- HTTP request URL : /sign/findId
- param : signFindIdDto
- return : String (memberId)

[비밀번호 재설정 - 아이디, 이메일 확인]
- HTTP method : POST
- HTTP request URL : /sign/checkIdEmail
- param : signCheckIdEmailDto
- return : ResponseEntity<String>

[비밀번호 재설정]
- HTTP method : POST
- HTTP request URL : /sign/updPw
- param : signUpdPwDto
- return : ResponseEntity<String>

[로그아웃]
- HTTP method : POST
- HTTP request URL : /sign/logout
- param : request
- return : ResponseEntity<String>

[카카오 로그인]
- HTTP method : GET
- HTTP request URL : /sign/kakao
- param : code
- param : request
- return : ResponseEntity<String>

[구글 로그인]
- HTTP method : GET
- HTTP request URL : /sign/google
- param : code
- param : request
- return : ResponseEntity<String>