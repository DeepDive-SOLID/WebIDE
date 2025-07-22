package solid.backend.Question.service;

import solid.backend.Question.dto.QuestionCreateDto;
import solid.backend.Question.dto.QuestionListDto;
import solid.backend.Question.dto.QuestionUpdDto;

import java.util.List;

public interface QuestionService {

    List<QuestionListDto> getQuestionList();

    List<QuestionListDto> containerInQuestionList(Integer containerId);
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
