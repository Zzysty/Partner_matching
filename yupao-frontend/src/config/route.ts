import Index from "../pages/Index.vue";
import TeamPage from "../pages/TeamPage.vue";
import UserPage from "../pages/UserPage.vue";
import SearchPage from "../pages/SearchPage.vue";
import UserEditPage from "../pages/UserEditPage.vue";
import SearchResultPage from "../pages/SearchResultPage.vue";
import UserLoginPage from "../pages/UserLoginPage.vue";
import 'vant/es/toast/style';
import TeamAddPage from "../pages/TeamAddPage.vue";
import TeamUpdatePage from "../pages/TeamUpdatePage.vue";
import UserUpdatePage from "../pages/UserUpdatePage.vue";
import UserTeamAddPage from "../pages/UserTeamAddPage.vue";
import UserTeamJoinPage from "../pages/UserTeamJoinPage.vue";

const routes = [
  {path: '/', title: '主页', component: Index},
  {path: '/team', title: '队伍', component: TeamPage},
  {path: '/user', title: '用户', component: UserPage},
  {path: '/search', title: '搜索', component: SearchPage},
  {path: '/user/list', title: '搜索结果', component: SearchResultPage},
  {path: '/user/edit', title: '编辑用户', component: UserEditPage},
  {path: '/user/login', title: '登录', component: UserLoginPage},
  {path: '/team/add', title: '添加队伍', component: TeamAddPage},
  {path: '/team/update', title: '更新队伍', component: TeamUpdatePage},
  {path: '/user/update', title: '更新用户', component: UserUpdatePage},
  {path: '/user/team/add', title: '已创建队伍', component: UserTeamAddPage},
  {path: '/user/team/join', title: '已加入队伍', component: UserTeamJoinPage},
]

export default routes;
