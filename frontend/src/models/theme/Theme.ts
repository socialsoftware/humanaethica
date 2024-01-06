import Institution from '@/models/institution/Institution';

export default class Theme {
  id: number | null = null;
  name!: string;
  completeName!: string;
  institutions: Institution[] = [];
  state!: string;
  parentTheme?: Theme;
  level?: number;

  constructor(jsonObj?: Theme) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.name = jsonObj.name;
      this.completeName = jsonObj.completeName;
      this.institutions = jsonObj.institutions.map(
        (institutions: Institution) => {
          return new Institution(institutions);
        },
      );
      this.state = jsonObj.state;
      this.parentTheme = jsonObj.parentTheme;
      this.level = jsonObj.level;
    }
  }
}
