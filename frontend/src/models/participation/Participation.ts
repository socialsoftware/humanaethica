import { ISOtoString } from '@/services/ConvertDateService';

export default class Participation {
  id: number | null = null;
  activityId: number | null = null;
  volunteerId: number | null | undefined = null;
  memberRating!: number;
  memberReview!: string;
  volunteerRating!: number;
  volunteerReview!: string;
  acceptanceDate!: string;

  constructor(jsonObj?: Participation) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.activityId = jsonObj.activityId;
      this.volunteerId = jsonObj.volunteerId;
      this.memberRating = jsonObj.memberRating;
      this.memberReview = jsonObj.memberReview;
      this.volunteerRating = jsonObj.volunteerRating;
      this.volunteerReview = jsonObj.volunteerReview;
      this.acceptanceDate = ISOtoString(jsonObj.acceptanceDate);
    }
  }
}
