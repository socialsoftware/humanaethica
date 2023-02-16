export default class RegisterVolunteer {
  volunteerEmail!: string;
  volunteerName!: string;
  volunteerUsername!: string;

  constructor(jsonObj?: RegisterVolunteer) {
    if (jsonObj) {
      this.volunteerEmail = jsonObj.volunteerEmail;
      this.volunteerName = jsonObj.volunteerName;
      this.volunteerUsername = jsonObj.volunteerUsername;
    }
  }
}
