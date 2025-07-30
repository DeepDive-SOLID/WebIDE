package solid.backend.Question.service;

import solid.backend.Question.dto.QuestionCreateDto;
import solid.backend.Question.dto.QuestionListDto;
import solid.backend.Question.dto.QuestionUpdDto;
import solid.backend.Question.dto.TestCaseListDto;

import java.util.List;

public interface QuestionService {

    /**
     * 설명: 전체 문제 조회
     */
    List<QuestionListDto> getQuestionList();

    /**
     * 설명: 컨테이너 내부에 등록된 문제 조회
     * @param containerId
     */
    List<QuestionListDto> containerInQuestionList(Integer containerId);

    /**
     * 설명: 공개된 테스트케이스 조회
     * @param questionId
     */
    List<TestCaseListDto> trueQuestionList(Integer questionId);

    /**
     * 설명: 문제 등록 및 테스트 케이스 등록
     * @param questionCreateDto
     */
    void createQuestion(QuestionCreateDto questionCreateDto);

    /**
     * 설명: 문제 수정 및 테스크 케이스 수정
     * @param questionUpdDto
     */
    void updateQuestion(QuestionUpdDto questionUpdDto);

    /**
     * 설명: 문제 및 테스트 케이스 삭제
     * @param questionId
     */
    void deleteQuestion(Integer questionId);
}
