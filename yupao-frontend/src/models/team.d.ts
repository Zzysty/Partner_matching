import UserType from "./user";

/**
 * 队伍类别
 */
type TeamType = {
  id: number;
  name: string;
  description: string;
  maxNum: number;
  expireTime: Date;
  userId: number;
  status: number;
  password: string;
  createTime: Date;
  updateTime: Date;
  createUser: UserType;
  hasJoinNum: number;
  hasJoin: boolean;
};

export default TeamType;
