import { ISOtoString } from '@/services/ConvertDateService';

export default class Assessment {
  id: number | null = null;
  institutionId: number | null = null;
  institutionName: string | null = null;
  volunteerId: number | null = null;
  volunteerName: String | null = null;
  review!: string;
  reviewDate!: string;

  constructor(jsonObj?: Assessment) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.institutionId = jsonObj.institutionId;
      this.institutionName = jsonObj.institutionName;
      this.volunteerId = jsonObj.volunteerId;
      this.volunteerName = jsonObj.volunteerName;
      this.review = jsonObj.review;
      this.reviewDate = ISOtoString(jsonObj.reviewDate);
    }
  }
}
