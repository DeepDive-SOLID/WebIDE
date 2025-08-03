package solid.backend.Directory.service;

import solid.backend.Directory.dto.DirectoryDelDto;
import solid.backend.Directory.dto.DirectoryDto;
import solid.backend.Directory.dto.DirectoryListDto;
import solid.backend.Directory.dto.DirectoryUpdDto;

import java.util.List;

public interface DirectoryService {
    /**
     * 설명: 컨테이너 전체 디렉터리 조회
     * @param directoryListDto
     */
    List<DirectoryDto> getDirectoryList(DirectoryListDto directoryListDto);

    /**
     * 설명: 디렉터리 생성
     * @param directoryDto
     */
    DirectoryDto createDirectory(DirectoryDto directoryDto);

    void updateDirectory(DirectoryUpdDto directoryUpdDto);

    /**
     * 설명: 디렉터리 삭제
     * @param directoryDelDto
     */
    void deleteDirectory(DirectoryDelDto directoryDelDto);
    
    /**
     * 설명: 특정 디렉터리의 하위 디렉터리 조회
     * @param directoryId
     * @return List<DirectoryDto>
     */
    List<DirectoryDto> getChildDirectories(Integer directoryId);
    
    /**
     * 설명: 루트 디렉터리만 조회
     * @param containerId
     * @return List<DirectoryDto>
     */
    List<DirectoryDto> getRootDirectories(Integer containerId);
}
