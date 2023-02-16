export default class Document {
  name!: string;
  content!: BigUint64Array[];

  constructor(jsonObj?: Document | null) {
    if (jsonObj) {
      this.name = jsonObj.name;
      this.content = jsonObj.content;
    }
  }
}
