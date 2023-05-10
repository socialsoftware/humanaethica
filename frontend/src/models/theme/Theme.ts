//import { ISOtoString } from '@/services/ConvertDateService';
//import Document from '../management/Document';

import Institution from '@/models/institution/Institution';

export default class Theme {
  id: number | null = null;
  name!: string;
  institutions: Institution[] = [];
  state!: string;

  constructor(jsonObj?: Theme) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.name = jsonObj.name;
      this.institutions = jsonObj.institutions.map(
        (institutions: Institution) => {
          return new Institution(institutions);
        }
      );
      this.state = jsonObj.state;
    }
  }
}
