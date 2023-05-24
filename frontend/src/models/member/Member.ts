import { ISOtoString } from '@/services/ConvertDateService';
import Document from '../management/Document';
import Institution from '@/models/institution/Institution';

export default class Member {
  id: number | null = null;
  name!: string;
  email!: string;
  username!: boolean;
  creationDate!: string;
  doc!: Document;
  institution!: Institution;

  constructor(jsonObj?: Member) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.name = jsonObj.name;
      this.email = jsonObj.email;
      this.username = jsonObj.username;
      this.creationDate = ISOtoString(jsonObj.creationDate);
      this.doc = jsonObj.doc;
      this.institution = jsonObj.institution;
    }
  }
}
