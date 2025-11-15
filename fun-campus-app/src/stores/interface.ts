export interface UserInfoState {
  user_id: string;
  avatar: string;
  mobile: string;
  email: string;
  nickname: string;
  username: string;
  gender: number;
  permission: string[];
  role: string[];
  token: string;
  tokenExpired: number;
}
