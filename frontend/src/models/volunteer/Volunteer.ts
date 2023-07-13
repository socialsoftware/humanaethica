import { ISOtoString } from '@/services/ConvertDateService';
import Document from '../management/Document';
import Activity from '@/models/activity/Activity';

export default class Volunteer {
  id: number | null = null;
  name!: string;
  email!: string;
  username!: boolean;
  creationDate!: string;
  activities: Activity[] = [];
  doc!: Document;

  constructor(jsonObj?: Volunteer) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.name = jsonObj.name;
      this.email = jsonObj.email;
      this.username = jsonObj.username;
      this.creationDate = ISOtoString(jsonObj.creationDate);
      this.doc = jsonObj.doc;
      this.activities = jsonObj.activities.map((activities: Activity) => {
        return new Activity(activities);
      });
    }
  }
}
