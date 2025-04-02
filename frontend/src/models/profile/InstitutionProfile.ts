import Assessment from '@/models/assessment/Assessment';
import Institution from '@/models/institution/Institution';

export default class InstitutionProfile{
  id: number | null = null;
  shortDescription!: string;
  numMembers!: string;
  numActivities!: number;
  numAssessments!: number;
  numVolunteers!: number;
  averageRating!: number;
  institution!: Institution;
  selectedAssessments:Assessment[] = [];

  constructor(jsonObj?: InstitutionProfile) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.shortDescription = jsonObj.shortDescription;
      this.numMembers = jsonObj.numMembers;
      this.numActivities = jsonObj.numActivities;
      this.numAssessments = jsonObj.numAssessments;
      this.numVolunteers = jsonObj.numVolunteers;
      this.selectedAssessments = jsonObj.selectedAssessments.map((selectedAssessments: Assessment) => {
        return new Assessment(selectedAssessments);
      });
      this.institution = jsonObj.institution;
      this.averageRating = jsonObj.averageRating;
     
    }
  }
}
