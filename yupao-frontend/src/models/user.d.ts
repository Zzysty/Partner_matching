/**
 * 用户类别
 */
type UserType = {
  id: number;
  username: string;
  userAccount: string;
  profile: string;
  avatarUrl?: string;
  gender: string;
  phone: string;
  email: string;
  userStatus: number;
  userRole: number;
  tags: string[];
  planetCode: number,
  createTime: Date;
};

export default UserType;
