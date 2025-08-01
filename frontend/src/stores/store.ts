import {create} from "zustand/react";
import {Client} from "@stomp/stompjs";

// Socket 관련 상태 타입 정의
interface SocketStore {
    stompClient: Client | null;
    setStompClient: (client: Client) => void;
}

export const useStore = create<SocketStore>((set) => ({
    stompClient: null,
    setStompClient: (client) => set({ stompClient: client }),
}));