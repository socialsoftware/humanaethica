export default class RegisterActivity {
  activityName!: string;
  activityRegion!: string;
  activityTheme!: string;

  constructor(jsonObj?: RegisterActivity) {
    if (jsonObj) {
      this.activityName = jsonObj.activityName;
      this.activityRegion = jsonObj.activityRegion;
      this.activityTheme = jsonObj.activityTheme;
    }
  }
}
