import { ISOtoString } from '@/services/ConvertDateService';

export default class User {
  id: number | null = null;
  name!: string;
  username!: string;
  email!: string;
  role!: string;
  state!: string;
  active!: boolean;
  institutionName!: string;
  type!: string;
  creationDate!: string;
  lastAccess!: string;
  hasDocument!: boolean;

  constructor(jsonObj?: User) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.name = jsonObj.name;
      this.username = jsonObj.username;
      this.email = jsonObj.email;
      this.role = jsonObj.role;
      this.state = jsonObj.state;
      this.active = jsonObj.active;
      this.type = jsonObj.type;
      this.creationDate = ISOtoString(jsonObj.creationDate);
      this.lastAccess = ISOtoString(jsonObj.lastAccess);
      this.institutionName = jsonObj.institutionName;
      this.hasDocument = jsonObj.hasDocument;
    }
  }
}
