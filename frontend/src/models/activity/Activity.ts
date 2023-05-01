import { ISOtoString } from '@/services/ConvertDateService';
import Document from '../management/Document';

export default class Activity {
  id: number | null = null;
  name!: string;
  region!: string;
  state!: boolean;
  creationDate!: string;

  constructor(jsonObj?: Activity) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.name = jsonObj.name;
      this.region = jsonObj.region;
      this.state = jsonObj.state;
      this.creationDate = ISOtoString(jsonObj.creationDate);
    }
  }
}
