import { ISOtoString } from "@/services/ConvertDateService";


export default class Notification {
  id: number | null = null;
  message!: string;
//   themes: Theme[] = [];
//   institution!: Institution;
  creationDate!: string;
  read!: boolean;
//   formattedStartingDate!: string;
//   endingDate!: string;
//   formattedEndingDate!: string;

  constructor(jsonObj?: Notification) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.message = jsonObj.message;
      this.creationDate = ISOtoString(jsonObj.creationDate);
      this.read = jsonObj.read;
    //   this.themes = jsonObj.themes.map((themes: Theme) => {
    //     return new Theme(themes);
    //   });
    //   this.institution = jsonObj.institution;
    //   this.state = jsonObj.state;
    }
  }
}
