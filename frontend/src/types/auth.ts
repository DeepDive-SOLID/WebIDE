export interface SignUpDto {
  memberId: string;
  memberName: string;
  memberPw: string;
  memberEmail: string;
  memberPhone: string;
  memberBirth: string;
}

export interface SignInCheckIdDto {
  memberId: string;
}

export interface SignInCheckEmailDto {
  memberEmail: string;
}

export interface SignInDto {
  memberId: string;
  memberPw: string;
}

export interface SignFindIdDto {
  memberEmail: string;
}

export interface SignCheckIdEmailDto {
  memberId: string;
  memberEmail: string;
}

export interface SignUpdPwDto {
  memberId: string;
  memberPw: string;
}
