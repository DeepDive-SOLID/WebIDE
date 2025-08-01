import api from "./axios";
import type {ApiResponse, ChatDto} from "../types/chatDto";

const chatApi = {

    // 채팅방 입장 요청
    joinChatRoom: async (chatRoomId: string | number, accessToken: string) => {
        try {
            const response = await api.post(`/chatRooms/${chatRoomId}/join`, { accessToken });
            return response.status == 200;
        } catch {
            throw new Error("채팅방 입장중 오류가 발생하였습니다.");
        }
    },

    // 채팅 메시지 리스트 조회
    getChatList: async (chatRoomId: string | number) => {
        try {
            const response = await api.get<ApiResponse<ChatDto[]>>(`/chatRooms/${chatRoomId}/chatList`);
            return response.data.data;
        } catch {
            throw new Error("채팅 리스트 조회중 오류가 발생하였습니다.");
        }
    },
};

export default chatApi;