import AuthUser from '@/models/user/AuthUser';

export default class TokenAuthUser {
  token!: string;
  user!: AuthUser;

  constructor(jsonObj?: TokenAuthUser) {
    if (jsonObj) {
      this.token = jsonObj.token;
      this.user = new AuthUser(jsonObj.user);
    }
  }
}
