import { ISOtoString } from '@/services/ConvertDateService';
import Document from '../management/Document';
import Theme from '@/models/theme/Theme';

export default class Activity {
  id: number | null = null;
  name!: string;
  region!: string;
  themes: Theme[] = [];
  state!: string;
  creationDate!: string;

  constructor(jsonObj?: Activity) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.name = jsonObj.name;
      this.region = jsonObj.region;
      this.themes = jsonObj.themes.map((themes: Theme) => {
        return new Theme(themes);
      });
      this.state = jsonObj.state;
      this.creationDate = ISOtoString(jsonObj.creationDate);
    }
  }
}