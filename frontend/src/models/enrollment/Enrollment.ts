import { ISOtoString } from '@/services/ConvertDateService';

export default class Enrollment {
  id: number | null = null;
  activityId: number | null = null;
  volunteerId: number | null = null;
  volunteerName: string | null = null;
  motivation!: string;
  enrollmentDateTime!: string;
  participating: boolean = false;

  constructor(jsonObj?: Enrollment) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.activityId = jsonObj.activityId;
      this.volunteerId = jsonObj.volunteerId;
      this.volunteerName = jsonObj.volunteerName;
      this.motivation = jsonObj.motivation;
      this.enrollmentDateTime = ISOtoString(jsonObj.enrollmentDateTime);
      this.participating = jsonObj.participating;
    }
  }
}
