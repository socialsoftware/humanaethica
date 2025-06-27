import { ISOtoString } from "@/services/ConvertDateService";


export default class Notification {
  id: number | null = null;
  message!: string;
  creationDate!: string;
  read!: boolean;

  constructor(jsonObj?: Notification) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.message = jsonObj.message;
      this.creationDate = ISOtoString(jsonObj.creationDate);
      this.read = jsonObj.read;
    }
  }
}
