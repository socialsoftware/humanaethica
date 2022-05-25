export default class AuthUser {
  id: number | null = null;
  name!: string;
  username!: string;
  role!: string;

  constructor(jsonObj?: AuthUser) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.name = jsonObj.name;
      this.username = jsonObj.username;
      this.role = jsonObj.role;
    }
  }
}
