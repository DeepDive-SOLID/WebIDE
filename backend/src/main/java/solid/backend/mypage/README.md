### 채팅 관리 : mypage/member
- controller(컨트롤러)
    - MypageController.java
- dto(객체정보)
    - MypageDto.java
    - MypageUpdDto.java
    - MypageProfileDto.java
- service(비즈니스 로직)
    - MypageService.java
    - MypageServiceImpl.java

### API 목록
[회원 정보 조회(프로필)]  
HTTP method : POST  
HTTP request URL : /mypage/member/getProfileDto  
param : memberId(String)  
return : MypageProfileDto  

[회원 정보 조회]  
HTTP method : POST  
HTTP request URL : /mypage/member/getMemberDto  
param : memberId(String)  
return : MypageDto

[회원 정보 수정]  
HTTP method : PUT  
HTTP request URL : /mypage/member/updateMemberDto  
param : memberDto(MypageUpdDto)  
return : ResponseEntity(String)  

[이메일 중복 체크]  
HTTP method : POST  
HTTP request URL : /mypage/member/checkEmail  
param : memberEmail(String)  
return : ResponseEntity(Boolean)  

[회원 정보 삭제]  
HTTP method : DELETE  
HTTP request URL : /mypage/member/deleteMemberDto  
param : memberId(String)  
return : ResponseEntity(String)  