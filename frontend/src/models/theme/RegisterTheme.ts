export default class RegisterTheme {
    themeName!: string;

    constructor(jsonObj?: RegisterTheme) {
        if (jsonObj) {
            this.themeName = jsonObj.themeName;
        }
    }
}