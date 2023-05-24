import { ISOtoString } from '@/services/ConvertDateService';
import Document from '../management/Document';
import Activity from '@/models/activity/Activity';
import Volunteer from '@/models/volunteer/Volunteer';

export default class Institution {
  id: number | null = null;
  name!: string;
  email!: string;
  active!: boolean;
  nif!: string;
  creationDate!: string;
  activities!: Activity[];

  constructor(jsonObj?: Institution) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.name = jsonObj.name;
      this.email = jsonObj.email;
      this.nif = jsonObj.nif;
      this.active = jsonObj.active;
      this.creationDate = ISOtoString(jsonObj.creationDate);
      this.activities = jsonObj.activities.map((activities: Activity) => {
        return new Activity(activities);
      });
    }
  }
}
