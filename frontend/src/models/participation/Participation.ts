import { ISOtoString } from '@/services/ConvertDateService';

export default class Participation {
  id: number | null = null;
  activityId: number | null = null;
  volunteerId: number | null = null;
  rating!: number;
  acceptanceDate!: string;

  constructor(jsonObj?: Participation) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.activityId = jsonObj.activityId;
      this.volunteerId = jsonObj.volunteerId;
      this.rating = jsonObj.rating;
      this.acceptanceDate = ISOtoString(jsonObj.acceptanceDate);
    }
  }
}