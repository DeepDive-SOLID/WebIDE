package solid.backend.Question.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import solid.backend.CodeFile.dto.CodeFileListDto;
import solid.backend.Question.dto.*;
import solid.backend.Question.repository.QuestionQueryRepository;
import solid.backend.entity.Question;
import solid.backend.entity.TestCase;
import solid.backend.jpaRepository.ContainerRepository;
import solid.backend.jpaRepository.QuestionRepository;
import solid.backend.jpaRepository.TeamRepository;
import solid.backend.jpaRepository.TestCaseRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService{
    private final QuestionRepository questionRepository;
    private final TestCaseRepository testCaseRepository;
    private final ContainerRepository containerRepository;
    private final TeamRepository teamRepository;
    private final QuestionQueryRepository questionQueryRepository;

    /**
     * 설명: 전체 문제 조회
     * @return List<QuestionListDto>
     */
    @Override
    @Transactional
    public List<QuestionListDto> getQuestionList() {
        return questionRepository.findAll().stream()
                .map(question -> new QuestionListDto(
                        question.getQuestionId(),
                        question.getContainer().getContainerId(),
                        question.getTeam().getTeamId(),
                        question.getQuestionTitle(),
                        question.getQuestionDescription(),
                        question.getQuestion(),
                        question.getQuestionInput(),
                        question.getQuestionOutput(),
                        question.getQuestionTime(),
                        question.getQuestionMem()
                )).collect(Collectors.toList());
    }

    /**
     * 설명: 컨테이너 내부에 등록된 문제 조회
     * @param containerId
     * @return List<QuestionListDto>
     */
    @Override
    @Transactional
    public List<QuestionListDto> containerInQuestionList(Integer containerId) {
        return questionQueryRepository.getQuestionListByContainerId(containerId);
    }

    /**
     * 설명: 등록된 문제의 공개된 테스트케이스 리스트 반환
     * @param questionId
     * @return List<TestCaseListDto>
     */
    @Override
    @Transactional
    public List<TestCaseListDto> trueQuestionList(Integer questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("해당 문제가 존재하지 않습니다."));

        List<TestCase> allCases = testCaseRepository.findByQuestion_QuestionId(questionId);

        return allCases.stream()
                .filter(TestCase::getCaseCheck)
                .map(tc -> new TestCaseListDto(tc.getCaseId(), tc.getCaseEx(), tc.getCaseAnswer()))
                .toList();
    }

    /**
     * 설명: 문제 등록 및 테스트 케이스 추가
     * @param questionCreateDto
     */
    @Override
    @Transactional
    public void createQuestion(QuestionCreateDto questionCreateDto) {
        // Question 테이블 데이터 저장
        Question question = new Question();

        question.setContainer(containerRepository.findById(questionCreateDto.getContainerId()).orElseThrow());
        question.setTeam(teamRepository.findById(questionCreateDto.getTeamId()).orElseThrow());
        question.setQuestionTitle(questionCreateDto.getQuestionTitle());
        question.setQuestionDescription(questionCreateDto.getQuestionDescription());
        question.setQuestion(questionCreateDto.getQuestion());
        question.setQuestionInput(questionCreateDto.getQuestionInput());
        question.setQuestionOutput(questionCreateDto.getQuestionOutput());
        question.setQuestionTime(questionCreateDto.getQuestionTime());
        question.setQuestionMem(questionCreateDto.getQuestionMem());

        questionRepository.save(question);

        for(TestCaseDto testCaseDto : questionCreateDto.getTestcases()) {
            TestCase testcase = new TestCase();

            testcase.setQuestion(question);
            testcase.setCaseEx(testCaseDto.getCaseEx());
            testcase.setCaseAnswer(testCaseDto.getCaseAnswer());
            testcase.setCaseCheck(testCaseDto.getCaseCheck());

            testCaseRepository.save(testcase);
        }
    }

    /**
     * 설명: 문제 및 테스트 케이스 수정
     * @param questionUpdDto
     */
    @Override
    @Transactional
    public void updateQuestion(QuestionUpdDto questionUpdDto) {
        System.out.println(questionUpdDto.getQuestionId());
        Question question = questionRepository.findById(questionUpdDto.getQuestionId())
                .orElseThrow(() -> new IllegalArgumentException("해당 문제가 존재하지 않습니다."));

        // 문제 정보 수정
        question.setQuestionTitle(questionUpdDto.getQuestionTitle());
        question.setQuestionDescription(questionUpdDto.getQuestionDescription());
        question.setQuestion(questionUpdDto.getQuestion());
        question.setQuestionInput(questionUpdDto.getQuestionInput());
        question.setQuestionOutput(questionUpdDto.getQuestionOutput());
        question.setQuestionTime(questionUpdDto.getQuestionTime());
        question.setQuestionMem(questionUpdDto.getQuestionMem());

        // 테스트 케이스 수정
        for (TestCaseUpdDto testCaseUpdDto : questionUpdDto.getTestcases()) {
            TestCase testcase = testCaseRepository.findById(testCaseUpdDto.getCaseId())
                    .orElseThrow(() -> new EntityNotFoundException("해당 테스트케이스가 존재하지 않습니다."));

            testcase.setCaseEx(testCaseUpdDto.getCaseEx());
            testcase.setCaseAnswer(testCaseUpdDto.getCaseAnswer());
            testcase.setCaseCheck(testCaseUpdDto.getCaseCheck());
        }
    }

    /**
     * 설명: 문제 및 테스트 케이스 삭제
     * @param questionId
     */
    @Override
    @Transactional
    public void deleteQuestion(Integer questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("해당 문제가 존재하지 않습니다."));

        questionRepository.delete(question);
    }
}
