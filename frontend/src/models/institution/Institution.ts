import { ISOtoString } from '@/services/ConvertDateService';
import Document from '../management/Document';

export default class Institution {
  id: number | null = null;
  name!: string;
  email!: string;
  active!: boolean;
  nif!: string;
  creationDate!: string;

  constructor(jsonObj?: Institution) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.name = jsonObj.name;
      this.email = jsonObj.email;
      this.nif = jsonObj.nif;
      this.active = jsonObj.active;
      this.creationDate = ISOtoString(jsonObj.creationDate);
    }
  }
}
