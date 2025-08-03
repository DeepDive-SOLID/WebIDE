### 컨테이너 관리 : container
- controller(컨트롤러)
    - ContainerController.java
- dto(객체정보)
    - BatchContainerVisibilityDto.java
    - ContainerCreateDto.java
    - ContainerMemberDto.java
    - ContainerResponseDto.java
    - ContainerSearchDto.java
    - ContainerStatisticsDto.java
    - ContainerUpdateDto.java
    - GroupMemberResponseDto.java
    - MemberInviteDto.java
- repository(jpa)
    - ContainerRepository.java
    - ContainerQueryRepository.java
- service(비즈니스 로직)
    - ContainerService.java
    - ContainerServiceImpl.java
- exception(예외처리)
    - ContainerExceptionHandler.java
    - ContainerNotFoundException.java
    - DuplicateMemberException.java
    - InvalidMemberException.java
    - MemberNotFoundException.java
    - UnauthorizedContainerAccessException.java
- constant(상수)
    - ContainerConstants.java
- common(공통)
    - ApiResponse.java

### API 목록
[컨테이너 생성]
- HTTP method : POST
- HTTP request URL : /api/containers
- param : ContainerCreateDto
- return : ApiResponse<ContainerResponseDto>

[컨테이너 조회]
- HTTP method : GET
- HTTP request URL : /api/containers/{containerId}
- return : ApiResponse<ContainerResponseDto>

[컨테이너 수정]
- HTTP method : PUT
- HTTP request URL : /api/containers/{containerId}
- param : ContainerUpdateDto
- return : ApiResponse<ContainerResponseDto>

[컨테이너 삭제]
- HTTP method : DELETE
- HTTP request URL : /api/containers/{containerId}
- return : ApiResponse<Void>
- 설명 : 컨테이너 및 관련된 모든 데이터(Directory, Question, Progress, Result, CodeFile, TestCase) 삭제

[내 컨테이너 목록 조회]
- HTTP method : GET
- HTTP request URL : /api/containers/my-container
- return : ApiResponse<List<ContainerResponseDto>>

[참여중인 컨테이너 목록 조회]
- HTTP method : GET
- HTTP request URL : /api/containers/shared-container
- return : ApiResponse<List<ContainerResponseDto>>

[공개 컨테이너 목록 조회]
- HTTP method : GET
- HTTP request URL : /api/containers/public-container
- return : ApiResponse<List<ContainerResponseDto>>

[접근 가능한 모든 컨테이너 목록 조회]
- HTTP method : GET
- HTTP request URL : /api/containers/all-container
- return : ApiResponse<List<ContainerResponseDto>>

[멤버 초대]
- HTTP method : POST
- HTTP request URL : /api/containers/{containerId}/members
- param : MemberInviteDto
- return : ApiResponse<GroupMemberResponseDto>

[멤버 목록 조회]
- HTTP method : GET
- HTTP request URL : /api/containers/{containerId}/members
- return : ApiResponse<List<GroupMemberResponseDto>>

[멤버 제거]
- HTTP method : DELETE
- HTTP request URL : /api/containers/{containerId}/members/{targetMemberId}
- return : ApiResponse<Void>

[컨테이너 탈퇴]
- HTTP method : DELETE
- HTTP request URL : /api/containers/{containerId}/members/me
- return : ApiResponse<Void>

[멤버 활동 시간 업데이트]
- HTTP method : PUT
- HTTP request URL : /api/containers/{containerId}/members/me/activity
- return : ApiResponse<Void>

[공개 컨테이너 참여]
- HTTP method : POST
- HTTP request URL : /api/containers/{containerId}/join
- return : ApiResponse<GroupMemberResponseDto>


### 주요 기능

#### 기본 기능 (MVP)
- 컨테이너 생성/수정/삭제 (CRUD)
- 멤버 초대 및 제거
- 공개/비공개 설정
- 내 컨테이너 목록 조회
- 비활성 멤버 자동 제거 : 6개월 이상 미활동 시 자동 탈퇴 (스케줄러)
- 활동 시간 추적 : 컨테이너 접근 시 자동으로 lastActivityDate 업데이트
- **컨테이너 삭제 시 관련 데이터 자동 삭제** : Directory, Question, Progress, Result, CodeFile, TestCase 등 모든 관련 데이터 cascade 삭제

#### 추가 구현 기능
- **공개 컨테이너 참여** : 공개 컨테이너에 초대 없이 자유롭게 참여 가능
- **참여 컨테이너 필터링** : 이미 참여중인 컨테이너는 공개 목록에서 자동 제외
- **다양한 목록 조회** : 내 컨테이너, 참여중인 컨테이너, 공개 컨테이너, 전체 접근 가능 컨테이너
- **멤버 관리** : 초대, 제거, 탈퇴, 활동 시간 추적

### 권한 시스템
- **ROOT (소유자 권한)**
  - 컨테이너 정보 수정/삭제
  - 멤버 초대/제거
  - 공개 상태 변경
  - 모든 파일 및 코드 접근
  
- **USER (일반 멤버 권한)**
  - 컨테이너 조회
  - 모든 파일 및 코드 접근
  - 코드 실행
  - 자신의 탈퇴
  - 활동 시간 업데이트

### 엔티티 관계
- Container 1:1 Team (컨테이너별 팀 자동 생성)
- Team 1:N TeamUser (팀 멤버 관리)
- TeamUser N:1 Member (사용자 정보)
- TeamUser N:1 Auth (권한 정보)
