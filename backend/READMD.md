# WebIDE


# 프로젝트 디렉토리 구조
src/main/java/solid/backend

- chat : 채팅 관리
- mypage : 마이페이지 관리
- sign : 회원 관리
- common : 공통 기능
  - FileManager : 파일 관리 모듈
  - ApiResponse : API 통신 모듈
- config: 설정파일
  - QueryDSLConfig: QueryDSL 빈 등록
  - FileStorageConfig : 파일 설정 값 관리
  - FileStorageHandlerConfig : 업로드 파일에 대한 정적 리소스 핸들링 설정
  - SecurityConfig : 권한 관리
- entity : 엔티티
  - Auth : 권한
  - Chat : 채팅
  - Code : 코드 텍스트
  - CodeFile : 코드 파일 경로
  - Container : 컨테이너
  - Directory : 디렉토리
  - member : 회원
  - Progress : 진행률
  - Question : 문제
  - Result : 결과
  - Team : 그룹
  - TeamUser : 그룹 회원
  - TestCase : 테스트 케이스
- jpaRepository : JPA Repository
  - AuthRepository : 권한
  - ChatRepository : 채팅
  - ContainerRepository : 컨테이너
  - MemberRepository : 회원
  - TeamRepository : 그룹
  - TeamUserRepository : 그룹 회원
- Jwt
- websocket : 소켓
  - MyChannelInterceptor : 채팅 인터셉터
  - WebSocketHandler : 채팅 핸들러
  - WebSocketConfig : 채팅 엔드포인트