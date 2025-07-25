### 컨테이너 관리 : container
- controller(컨트롤러)
    - ContainerController.java
- dto(객체정보)
    - BatchContainerVisibilityDto.java
    - ContainerCreateDto.java
    - ContainerMemberDto.java
    - ContainerResponseDto.java
    - ContainerSearchDto.java
    - ContainerSearchResponseDto.java
    - ContainerStatisticsDto.java
    - ContainerUpdateDto.java
    - GroupMemberResponseDto.java
    - MemberInviteDto.java
- repository(jpa)
    - ContainerJpaRepository.java
    - ContainerQueryRepository.java
- service(비즈니스 로직)
    - ContainerService.java
    - ContainerServiceImpl.java
- exception(예외처리)
    - ContainerException.java
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

[내 컨테이너 목록 조회]
- HTTP method : GET
- HTTP request URL : /api/containers/my
- return : ApiResponse<List<ContainerResponseDto>>

[컨테이너 검색]
- HTTP method : GET
- HTTP request URL : /api/containers/search
- param : containerName, isPublic, ownerId (Query Params)
- return : ApiResponse<List<ContainerSearchResponseDto>>

[컨테이너 고급 검색]
- HTTP method : POST
- HTTP request URL : /api/containers/search/advanced
- param : ContainerSearchDto
- return : ApiResponse<List<ContainerSearchResponseDto>>

[컨테이너 통계 조회]
- HTTP method : GET
- HTTP request URL : /api/containers/{containerId}/statistics
- return : ApiResponse<ContainerStatisticsDto>

[권한별 컨테이너 수 통계]
- HTTP method : GET
- HTTP request URL : /api/containers/stats/authority
- return : ApiResponse<Map<String, Integer>>

[멤버 초대]
- HTTP method : POST
- HTTP request URL : /api/containers/{containerId}/members
- param : MemberInviteDto
- return : ApiResponse<List<GroupMemberResponseDto>>

[멤버 제거]
- HTTP method : DELETE
- HTTP request URL : /api/containers/{containerId}/members/{memberId}
- return : ApiResponse<Void>

[멤버 권한 변경] - 미구현
- HTTP method : PUT
- HTTP request URL : /api/containers/{containerId}/members/{memberId}/authority
- param : authority (Query Param)
- return : ApiResponse<GroupMemberResponseDto>
- 현재 구현되지 않음

[컨테이너 공개 상태 일괄 변경]
- HTTP method : PUT
- HTTP request URL : /api/containers/batch/visibility
- param : BatchContainerVisibilityDto
- return : ApiResponse<List<ContainerResponseDto>>

### 주요 기능

#### 기본 기능 (MVP)
- 컨테이너 생성/수정/삭제 (CRUD)
- 멤버 초대 및 제거
- 공개/비공개 설정
- 내 컨테이너 목록 조회
- 비활성 멤버 자동 제거 : 6개월 이상 미활동 시 자동 탈퇴 (스케줄러)
- 활동 시간 추적 : 컨테이너 접근 시 자동으로 lastActivityDate 업데이트

#### 추가 구현 기능
- **컨테이너 검색** : 이름, 공개여부, 소유자로 검색
- **고급 검색** : 멤버 수, 생성일 범위, 정렬 옵션 포함
- **통계 조회** : 멤버 목록, 접속 상태, 가입일, 활동 시간 표시
- **권한별 통계** : 사용자가 ROOT/USER 권한을 가진 컨테이너 수 집계
- **배치 공개 상태 변경** : 소유한 여러 컨테이너를 선택하여 한 번에 공개/비공개로 변경

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
