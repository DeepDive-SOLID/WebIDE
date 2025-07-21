package solid.backend.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * JPA 및 QueryDSL 설정 클래스
 * 
 * JPA 레포지토리와 QueryDSL을 위한 설정을 담당합니다.
 * - JPA 레포지토리 스캔 위치 지정
 * - 트랜잭션 관리 활성화
 * - QueryDSL을 위한 JPAQueryFactory Bean 설정
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = "solid.backend.jpaRepository"
)
public class JpaConfig {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    /**
     * QueryDSL을 위한 JPAQueryFactory Bean 설정
     * 
     * 모든 QueryDSL 구현체에서 주입받아 사용합니다.
     * EntityManager를 사용하여 쿼리를 생성합니다.
     * 
     * @return JPAQueryFactory 인스턴스
     */
    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }
}