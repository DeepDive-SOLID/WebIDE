package solid.backend.Question.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import solid.backend.CodeFile.dto.CodeFileListDto;
import solid.backend.Question.dto.*;
import solid.backend.Question.repository.QuestionQueryRepository;
import solid.backend.entity.Container;
import solid.backend.entity.Directory;
import solid.backend.entity.Question;
import solid.backend.entity.TestCase;
import solid.backend.jpaRepository.ContainerRepository;
import solid.backend.jpaRepository.DirectoryRepository;
import solid.backend.jpaRepository.QuestionRepository;
import solid.backend.jpaRepository.TeamRepository;
import solid.backend.jpaRepository.TestCaseRepository;
import solid.backend.jpaRepository.ResultRepository;

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
    private final DirectoryRepository directoryRepository;
    private final ResultRepository resultRepository;

    /**
     * 설명: 전체 문제 조회
     * @return List<QuestionListDto>
     */
    @Override
    @Transactional(readOnly = true)
    public List<QuestionListDto> getQuestionList() {
        return questionRepository.findAll().stream()
                .map(question -> new QuestionListDto(
                        question.getQuestionId(),
                        question.getContainer().getContainerId(),
                        question.getTeam().getTeamId(),
                        question.getDirectory() != null ? question.getDirectory().getDirectoryId() : null,
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
    @Transactional(readOnly = true)
    public List<QuestionListDto> containerInQuestionList(Integer containerId) {
        return questionQueryRepository.getQuestionListByContainerId(containerId);
    }

    /**
     * 설명: 등록된 문제의 공개된 테스트케이스 리스트 반환
     * @param questionId
     * @return List<TestCaseListDto>
     */
    @Override
    @Transactional(readOnly = true)
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
        // Container 조회
        Container container = containerRepository.findById(questionCreateDto.getContainerId())
                .orElseThrow(() -> new IllegalArgumentException("컨테이너를 찾을 수 없습니다."));
        
        // Directory 조회 (directoryId가 제공된 경우)
        final Directory directory;
        if (questionCreateDto.getDirectoryId() != null) {
            directory = directoryRepository.findById(questionCreateDto.getDirectoryId()).orElse(null);
        } else {
            directory = null;
        }
        
        // 같은 컨테이너 내에서 중복된 문제 제목 체크 (디렉토리 무관)
        List<Question> existingQuestions = questionRepository.findAll().stream()
                .filter(q -> q.getContainer().getContainerId().equals(questionCreateDto.getContainerId()))
                .filter(q -> q.getQuestionTitle().equals(questionCreateDto.getQuestionTitle()))
                .toList();
        
        if (!existingQuestions.isEmpty()) {
            throw new IllegalArgumentException(
                    "이미 동일한 제목의 문제가 존재합니다: " + questionCreateDto.getQuestionTitle()
            );
        }
        
        // Question 테이블 데이터 저장
        Question question = new Question();

        question.setContainer(container);
        question.setTeam(teamRepository.findById(questionCreateDto.getTeamId()).orElseThrow());
        question.setQuestionTitle(questionCreateDto.getQuestionTitle());
        question.setQuestionDescription(questionCreateDto.getQuestionDescription());
        question.setQuestion(questionCreateDto.getQuestion());
        question.setQuestionInput(questionCreateDto.getQuestionInput());
        question.setQuestionOutput(questionCreateDto.getQuestionOutput());
        question.setQuestionTime(questionCreateDto.getQuestionTime());
        question.setQuestionMem(questionCreateDto.getQuestionMem());
        question.setDirectory(directory);

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

        // 먼저 이 문제와 관련된 모든 Result 삭제
        resultRepository.deleteByQuestion(question);
        
        // 그 다음 Question 삭제 (TestCase는 cascade로 자동 삭제됨)
        questionRepository.delete(question);
    }
}
