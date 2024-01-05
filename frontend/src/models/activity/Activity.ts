import { ISOtoString } from '@/services/ConvertDateService';
import Theme from '@/models/theme/Theme';
import Volunteer from '@/models/volunteer/Volunteer';
import Institution from '@/models/institution/Institution';

export default class Activity {
  id: number | null = null;
  name!: string;
  region!: string;
  themes: Theme[] = [];
  volunteers: Volunteer[] = [];
  institution!: Institution;
  state!: string;
  creationDate!: string;
  alreadyJoined?: boolean;
  description!: string;
  startingDate!: string;
  endingDate!: string;

  constructor(jsonObj?: Activity) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.name = jsonObj.name;
      this.region = jsonObj.region;
      this.themes = jsonObj.themes.map((themes: Theme) => {
        return new Theme(themes);
      });
      this.volunteers = jsonObj.volunteers.map((volunteers: Volunteer) => {
        return new Volunteer(volunteers);
      });
      this.institution = jsonObj.institution;
      this.state = jsonObj.state;
      this.creationDate = ISOtoString(jsonObj.creationDate);
      this.alreadyJoined = false;
      this.description = jsonObj.description;
      this.startingDate = ISOtoString(jsonObj.startingDate);
      this.endingDate = ISOtoString(jsonObj.endingDate);
    }
  }
}
