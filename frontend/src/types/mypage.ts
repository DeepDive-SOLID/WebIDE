export interface MypageDto {
  memberName: string;
  memberPassword: string;
  memberEmail: string;
  memberPhone: string;
  memberBirth: string;
  memberImg: string;
}

export interface MypageProfileDto {
  memberId: string;
  memberName: string;
  memberImg: string;
}

export interface MypageUpdDto {
  memberId: string;
  memberName: string;
  memberPassword: string;
  memberEmail: string;
  memberPhone: string;
  memberBirth: string;
  memberImg: string;
}
