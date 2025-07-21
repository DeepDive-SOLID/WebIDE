package solid.backend.jpaRepository;

import solid.backend.docker.entity.DockerExecution;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 도커 실행 레포지토리 커스텀 인터페이스
 * 
 * QueryDSL을 사용하여 복잡한 도커 실행 관련 쿼리를 정의하는 인터페이스입니다.
 * 대량 데이터 처리, 통계 조회, 복잡한 조건 검색 등을 위해 사용됩니다.
 * 
 * 구현 패턴:
 * - 이 인터페이스는 DockerExecutionJpaRepository가 상속합니다
 * - 실제 구현은 DockerExecutionJpaRepositoryImpl 클래스에서 수행됩니다
 * 
 * 주요 기능:
 * - 실행 중인 도커 컨테이너 모니터링
 * - 오래된 실행 기록 정리
 * - 사용 통계 수집 및 분석
 * 
 * @see DockerExecutionJpaRepositoryImpl - 이 인터페이스의 QueryDSL 구현체
 */
public interface DockerExecutionRepositoryCustom {
    
    /**
     * 현재 실행 중인 모든 도커 컨테이너를 조회합니다.
     * 
     * 실행 중 판단 기준:
     * - status = 'RUNNING' 또는
     * - endedAt이 null인 경우
     * 
     * 사용 목적:
     * - 시스템 모니터링
     * - 리소스 사용량 확인
     * - 비정상 종료된 컨테이너 정리
     * 
     * @return 실행 중인 DockerExecution 목록
     */
    List<DockerExecution> findRunningExecutions();
    
    /**
     * 특정 날짜 이전에 생성된 오래된 실행 기록을 삭제합니다.
     * 
     * 대량 삭제 주의사항:
     * - 트랜잭션 크기를 고려하여 배치 처리 권장
     * - JPA 영속성 컨텍스트와 동기화되지 않음
     * 
     * 사용 예시:
     * - 일일/주기적 로그 정리 작업
     * - 스토리지 공간 관리
     * 
     * @param date 삭제 기준 날짜 (이 날짜 이전 데이터 삭제)
     * @return 삭제된 레코드 수
     */
    long deleteByCreatedAtBefore(LocalDateTime date);
    
    /**
     * 특정 컨테이너와 사용자에 대한 실행 통계를 조회합니다.
     * 
     * 통계 내용:
     * - 실행 횟수
     * - 평균 실행 시간
     * - 성공/실패 비율
     * - 사용 언어별 분포
     * 
     * 매개변수 조합:
     * - containerId만: 해당 컨테이너의 전체 통계
     * - memberId만: 해당 사용자의 전체 통계
     * - 둘 다: 특정 컨테이너에서 특정 사용자의 통계
     * 
     * @param containerId 컨테이너 ID (null 가능)
     * @param memberId 사용자 ID (null 가능)
     * @return 통계 정보가 포함된 DockerExecution 목록
     */
    List<DockerExecution> findExecutionStatistics(Long containerId, String memberId);
    
    /**
     * 특정 기간 동안 특정 프로그래밍 언어의 실행 횟수를 조회합니다.
     * 
     * 사용 예시:
     * - 인기 언어 통계
     * - 사용량 분석
     * - 리소스 할당 계획
     * 
     * @param language 프로그래밍 언어 (java, python, javascript 등)
     * @param startDate 통계 시작 날짜
     * @param endDate 통계 종료 날짜
     * @return 해당 기간의 실행 횟수
     */
    long countByLanguageAndDateRange(String language, LocalDateTime startDate, LocalDateTime endDate);
}