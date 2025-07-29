package solid.backend.container.dto;

import lombok.*;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

/**
 * 컨테이너 검색 조건 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContainerSearchDto {
    
    private String name; // 컨테이너 이름 검색
    private String keyword; // 이름, 설명에서 통합 검색
    private Boolean isPublic; // 공개 여부 필터
    private String ownerId; // 소유자 ID
    private String memberId; // 특정 멤버 ID
    
    @Size(max = 100, message = "최대 100개의 멤버 ID만 허용됩니다")
    private List<String> memberIds; // 특정 멤버들이 속한 컨테이너
    private LocalDate createdAfter; // 특정 날짜 이후 생성
    private LocalDate createdBefore; // 특정 날짜 이전 생성
    private Integer minMemberCount; // 최소 멤버 수
    private Integer maxMemberCount; // 최대 멤버 수
    
    @Builder.Default
    private SortBy sortBy = SortBy.CREATED_DATE; // 정렬 기준
    
    @Builder.Default
    private SortDirection sortDirection = SortDirection.DESC; // 정렬 방향
    
    @Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다")
    @Builder.Default
    private int page = 0; // 페이지 번호
    
    @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다")
    @Max(value = 100, message = "페이지 크기는 100 이하여야 합니다")
    @Builder.Default
    private int size = 20; // 페이지 크기
    
    public enum SortBy {
        NAME, CREATED_DATE, MEMBER_COUNT, LAST_ACTIVITY
    }
    
    public enum SortDirection {
        ASC, DESC
    }
    
    /**
     * 날짜 범위 유효성 검사
     * createdAfter가 createdBefore보다 이후 날짜인지 확인
     */
    @AssertTrue(message = "createdAfter는 createdBefore보다 이전 날짜여야 합니다")
    private boolean isValidDateRange() {
        if (createdAfter != null && createdBefore != null) {
            return !createdAfter.isAfter(createdBefore);
        }
        return true;
    }
}