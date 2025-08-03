import {useCallback} from "react";
import {useStore} from "../stores/store";
import SockJS from "sockjs-client";
import {Client} from "@stomp/stompjs";
import {getToken} from "../utils/auth";

// STOMP 프레임 구조 타입 정의
interface IFrame {
    command: string;
    headers: Record<string, string>;
    body: string;
}

// connect 함수에 전달되는 콜백 함수들 타입 정의
interface ConnectOptions {
    // 연결 성공 시 호출되는 콜백
    onConnect: () => void;
    // 연결 오류 발생 시 호출되는 콜백
    onError: (e: IFrame) => void;
    // 연결 종료 시 호출되는 콜백
    onDisconnect: () => void;
}

export function useStomp() {
    // store 에서 STOMP 클라이언트 설정 함수 가져오기
    const {setStompClient} = useStore();

    // connect 함수 생성, 의존성으로 setStompClient 포함
    const connect = useCallback(
        ({onConnect, onError, onDisconnect}: ConnectOptions) => {
            // 인증 토큰 가져오기
            const accessToken = getToken();
            if (!accessToken) {
                console.error("[STOMP] Access token is missing.");
                // 토큰 없으면 연결 시도 중단
                return;
            }

            // SockJS 소켓 생성
            const socket = new SockJS("http://15.164.250.218:8080/ws");
            // STOMP 클라이언트 생성 및 설정
            const stompClient = new Client({
                // SockJS 소켓을 WebSocket 팩토리로 지정
                webSocketFactory: () => socket,
                // 디버그 메시지를 콘솔에 출력
                debug: (msg: string) => console.log("[STOMP]:", msg),
                // 연결 시 헤더에 인증 토큰 포함
                connectHeaders: {
                    Authorization: `Bearer ${accessToken}`,
                },
                // 연결 성공
                onConnect: () => {
                    console.log("WebSocket 연결 성공");
                    // 전역 상태에 stompClient 저장
                    setStompClient(stompClient);
                    // 외부에서 전달받은 onConnect 콜백 호출
                    onConnect();

                    // 연결 끊기 TEST 코드
                    // setTimeout(() => {
                    //     stompClient.deactivate();
                    // }, 5000);
                },
                // STOMP 에러 발생
                onStompError: (e) => {
                    console.error("[STOMP] 연결 실패: ", e);
                    onError(e);
                },
                // 연결 종료
                onDisconnect: () => onDisconnect(),
                // 자동 재연결 지연 시간 (5초)
                reconnectDelay: 5000,
                // 서버에서 보낼 하트비트 간격 (ms)
                heartbeatIncoming: 4000,
                // 클라이언트가 보낼 하트비트 간격 (ms)
                heartbeatOutgoing: 4000,
            });

            // STOMP 클라이언트 활성화, 연결 시작
            stompClient.activate();
        }, [setStompClient]
    );

    // connect 함수 반환
    return {
        connect
    };
}