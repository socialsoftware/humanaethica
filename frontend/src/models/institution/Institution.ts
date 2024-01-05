import { ISOtoString } from '@/services/ConvertDateService';
import Activity from '@/models/activity/Activity';
import Theme from '@/models/theme/Theme';

export default class Institution {
  id: number | null = null;
  name!: string;
  email!: string;
  active!: boolean;
  nif!: string;
  creationDate!: string;
  activities!: Activity[];
  themes: Theme[] = [];

  constructor(jsonObj?: Institution) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.name = jsonObj.name;
      this.email = jsonObj.email;
      this.nif = jsonObj.nif;
      this.active = jsonObj.active;
      this.creationDate = ISOtoString(jsonObj.creationDate);
      this.themes = jsonObj.themes.map((theme: Theme) => {
        return new Theme(theme);
      });
      this.activities = jsonObj.activities.map((activities: Activity) => {
        return new Activity(activities);
      });
    }
  }
}
