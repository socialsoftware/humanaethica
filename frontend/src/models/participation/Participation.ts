import { ISOtoString } from '@/services/ConvertDateService';

export default class Participation {
  id: number | null = null;
  activityId: number | null = null;
  volunteerId: number | null = null;
  memberRating!: number;
  memberReview!: string;
  acceptanceDate!: string;

  constructor(jsonObj?: Participation) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.activityId = jsonObj.activityId;
      this.volunteerId = jsonObj.volunteerId;
      this.memberRating = jsonObj.memberRating;
      this.memberReview = 'placeholder';
      this.acceptanceDate = ISOtoString(jsonObj.acceptanceDate);
    }
  }
}
