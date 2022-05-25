import User from '@/models/user/User';

export default class RegisterUser extends User {
  password!: string;
  state!: string;
  confirmationToken!: string;

  constructor(jsonObj?: RegisterUser) {
    super(jsonObj);
    if (jsonObj) {
      this.password = jsonObj.password;
      this.state = jsonObj.state;
      this.confirmationToken = jsonObj.confirmationToken;
    }
  }
}
