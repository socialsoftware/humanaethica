import User from '@/models/user/User';

export default class RegisterUser extends User {
  password!: string;
  confirmationToken!: string;

  constructor(jsonObj?: RegisterUser) {
    super(jsonObj);
    if (jsonObj) {
      this.password = jsonObj.password;
      this.confirmationToken = jsonObj.confirmationToken;
    }
  }
}
