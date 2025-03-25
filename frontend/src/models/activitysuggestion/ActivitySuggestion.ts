import { ISOtoString } from '@/services/ConvertDateService';

export default class ActivitySuggestion {
    id: number | null = null;
    name!: string;
    description!: string;
    region!: string;
    creationDate!: string;
    // formattedCreationDate!: string; TOASK
    applicationDeadline!: string;
    formattedApplicationDeadline!: string;
    startingDate!: string;
    formattedStartingDate!: string;
    endingDate!: string;
    formattedEndingDate!: string;
    participantsNumberLimit!: number;
    state!: string;
    institutionId: number | null = null;
    volunteerId: number | null = null;
    //TODO - depois acrescentar os atributos referentes à mudança de estados

    constructor(jsonObj?: ActivitySuggestion) {
        if (jsonObj) {
            this.id = jsonObj.id;
            this.name = jsonObj.name;
            this.description = jsonObj.description;
            this.region = jsonObj.region;
            this.creationDate = ISOtoString(jsonObj.creationDate);
            // if (jsonObj.creationDate)
            //     this.formattedCreationDate = ISOtoString(jsonObj.creationDate);
            this.applicationDeadline = jsonObj.applicationDeadline;
            if (jsonObj.applicationDeadline)
                this.formattedApplicationDeadline = ISOtoString(jsonObj.applicationDeadline);
            if (jsonObj.startingDate)
                this.formattedStartingDate = ISOtoString(jsonObj.startingDate);
            if (jsonObj.endingDate)
                this.formattedEndingDate = ISOtoString(jsonObj.endingDate);
            this.participantsNumberLimit = jsonObj.participantsNumberLimit;
            this.state = jsonObj.state;
            this.institutionId = jsonObj.institutionId;
            this.volunteerId = jsonObj.volunteerId;
        }
    }
}