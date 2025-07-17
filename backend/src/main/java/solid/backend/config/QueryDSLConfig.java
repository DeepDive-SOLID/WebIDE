package solid.backend.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * QueryDSL 설정 클래스
 * JPAQueryFactory를 Bean으로 등록
 */
@Configuration
public class QueryDslConfig {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    /**
     * JPAQueryFactory Bean 생성
     * QueryDSL을 사용하는 Repository에서 주입받아 사용
     */
    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }
}