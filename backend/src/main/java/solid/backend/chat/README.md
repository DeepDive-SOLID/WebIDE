### 채팅 관리
- controller(컨트롤러)
    - ChatController.java
- dto(객체정보)
    - ChatDto.java
    - ChatListDto.java
- repository(jpa)
    - ChatQueryRepository.java
- service(비즈니스 로직)
    - ChatService.java
    - ChatServiceImpl.java

### API 목록
[메시지 저장 및 전송]  
HTTP method : MESSAGE  
HTTP request URL : /chatRooms/{chatRoomId}  
param : content(String)  
param : accessor(SimpMessageHeaderAccessor)  
param : chatRoomId(Integer)  
return : ResponseEntity<ApiResponse<Void>>(void)

[메시지 리스트 조회]  
HTTP method : GET  
HTTP request URL : /api/chatRooms/{chatRoomId}/chatList  
param : chatRoomId(Integer)
return : ResponseEntity<ApiResponse<List<MessageListDto>>>(ChatListDto)

[채팅방 입장 시 메세지 전송 및 참여자 데이터 추가]  
HTTP method : POST  
HTTP request URL : /api/chatRooms/{chatRoomId}/join  
param : chatRoomId(Integer)
return : ResponseEntity<ApiResponse<Void>>(void)
---