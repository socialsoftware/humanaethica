import Participation from '../participation/Participation';
import User from '../user/User';

export default class VolunteerProfile {
    id: number | null = null;
    shortBio!: string;
    numTotalEnrollments!: number;
    numTotalParticipations!: number;
    numTotalAssessments!: number;
    averageRating!: number;
    volunteer!: User;
    selectedParticipations: Participation[] = [];

    constructor(jsonObj?: VolunteerProfile ) {
        if (jsonObj) {
        this.id = jsonObj.id;
        this.shortBio = jsonObj.shortBio;
        this.numTotalEnrollments = jsonObj.numTotalEnrollments;
        this.numTotalParticipations = jsonObj.numTotalParticipations;
        this.numTotalAssessments = jsonObj.numTotalAssessments;
        this.averageRating = jsonObj.averageRating;
        this.volunteer = jsonObj.volunteer;
        this.selectedParticipations = jsonObj.selectedParticipations.map((participations: Participation) => {
                return new Participation(participations);
              });
        }
    }
}