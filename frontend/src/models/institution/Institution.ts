export default class Institution {
  id: number | null = null;
  name!: string;
  email!: string;
  valid!: boolean;

  constructor(jsonObj?: Institution) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.name = jsonObj.name;
      this.email = jsonObj.email;
      this.valid = jsonObj.valid;
    }
  }
}
