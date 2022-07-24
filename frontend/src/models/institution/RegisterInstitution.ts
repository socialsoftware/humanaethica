export default class RegisterInstitution {
  institutionEmail!: string;
  institutionName!: string;
  institutionNif!: string;
  memberUsername!: string;
  memberEmail!: string;
  memberName!: string;

  constructor(jsonObj?: RegisterInstitution) {
    if (jsonObj) {
      this.institutionEmail = jsonObj.institutionEmail;
      this.institutionName = jsonObj.institutionName;
      this.institutionNif = jsonObj.institutionNif;
      this.memberUsername = jsonObj.memberUsername;
      this.memberEmail = jsonObj.memberEmail;
      this.memberName = jsonObj.memberName;
    }
  }
}
