import { ISOtoString } from '@/services/ConvertDateService';
import Institution from '../institution/Institution';

export default class ActivitySuggestion {
    id: number | null = null;
    numberVotes: number | null = null;
    name!: string;
    description!: string;
    region!: string;
    creationDate!: string;
    formattedCreationDate!: string;
    applicationDeadline!: string;
    formattedApplicationDeadline!: string;
    startingDate!: string;
    formattedStartingDate!: string;
    endingDate!: string;
    formattedEndingDate!: string;
    participantsNumberLimit!: number;
    state!: string;
    institution!: Institution;
    volunteerId: number | null = null;
    rejectionJustification!: string;

    constructor(jsonObj?: ActivitySuggestion) {
        if (jsonObj) {
            this.id = jsonObj.id;
            this.numberVotes = jsonObj.numberVotes;
            this.name = jsonObj.name;
            this.description = jsonObj.description;
            this.region = jsonObj.region;
            this.creationDate = ISOtoString(jsonObj.creationDate);
            if (jsonObj.creationDate)
                this.formattedCreationDate = ISOtoString(jsonObj.creationDate);
            this.applicationDeadline = jsonObj.applicationDeadline;
            if (jsonObj.applicationDeadline)
                this.formattedApplicationDeadline = ISOtoString(jsonObj.applicationDeadline);
            this.startingDate = jsonObj.startingDate;
            if (jsonObj.startingDate)
                this.formattedStartingDate = ISOtoString(jsonObj.startingDate);
            this.endingDate = jsonObj.endingDate;
            if (jsonObj.endingDate)
                this.formattedEndingDate = ISOtoString(jsonObj.endingDate);
            this.participantsNumberLimit = jsonObj.participantsNumberLimit;
            this.state = jsonObj.state;
            this.institution = jsonObj.institution;
            this.volunteerId = jsonObj.volunteerId;
            this.rejectionJustification = jsonObj.rejectionJustification;
        }
    }
}