export interface ChatDto {
    chatId: number;
    chatText: string;
    chatType: string;
    memberId: string;
    teamId: number;
}

export interface ApiResponse<T> {
    success: boolean;
    data: T;
    timestamp: string;
}