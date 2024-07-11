import { ISOtoString } from '@/services/ConvertDateService';

export default class Report {
  id: number | null = null;
  activityId: number | null = null;
  volunteerId: number | null = null;
  volunteerName: string | null = null;
  justification!: string;
  reportDateTime!: string;

  constructor(jsonObj?: Report) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.activityId = jsonObj.activityId;
      this.volunteerId = jsonObj.volunteerId;
      this.volunteerName = jsonObj.volunteerName;
      this.justification = jsonObj.justification;
      this.reportDateTime = ISOtoString(jsonObj.reportDateTime);
    }
  }
}
