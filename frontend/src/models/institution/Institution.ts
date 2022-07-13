import { ISOtoString } from '@/services/ConvertDateService';

export default class Institution {
  id: number | null = null;
  name!: string;
  email!: string;
  valid!: boolean;
  nif!: string;
  creationDate!: string;

  constructor(jsonObj?: Institution) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.name = jsonObj.name;
      this.email = jsonObj.email;
      this.nif = jsonObj.nif;
      this.valid = jsonObj.valid;
      this.creationDate = ISOtoString(jsonObj.creationDate);
    }
  }
}
