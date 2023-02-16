export default class RegisterMember {
  memberEmail!: string;
  memberName!: string;
  memberUsername!: string;

  constructor(jsonObj?: RegisterMember) {
    if (jsonObj) {
      this.memberEmail = jsonObj.memberEmail;
      this.memberName = jsonObj.memberName;
      this.memberUsername = jsonObj.memberUsername;
    }
  }
}
