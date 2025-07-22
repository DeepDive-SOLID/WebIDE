package solid.backend.CodeFile.service;

import solid.backend.CodeFile.dto.CodeFileDelDto;
import solid.backend.CodeFile.dto.CodeFileListDto;
import solid.backend.CodeFile.dto.CodeFileSaveDto;
import solid.backend.CodeFile.dto.CodeFileUpdDto;

import java.util.List;

public interface CodeFileService {
    /**
     * 설명: 코드 파일 전체 조회
     */
    List<CodeFileListDto> getCodeFileList();

    /**
     * 설명: 코드 파일 내용 조회
     * @param codeFileId
     */
    String codeContent(Integer codeFileId);

    /**
     * 설명: 코드 파일 생성
     * @param codeFileSaveDto
     */
    void createCodeFile(CodeFileSaveDto codeFileSaveDto);

    /**
     * 설명: 코드 파일 내용 변경
     * @param codeFileUpdDto
     */
    void updateCodeFile(CodeFileUpdDto codeFileUpdDto);

    /**
     * 설명: 코드 파일 삭제
     * @param codeFileDelDto
     */
    void deleteCodeFile(CodeFileDelDto codeFileDelDto);
}
