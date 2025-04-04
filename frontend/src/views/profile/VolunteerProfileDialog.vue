<template>
  <v-dialog v-model="dialog" persistent width="1000">
    <v-card class="container">

      <!-- Title -->
      <v-card-title class="d-flex flex-column align-start">
        <h3>New Volunteer Profile</h3>
      </v-card-title>

      <v-form ref="form" lazy-validation>
        <!-- Short Bio -->
        <v-card-text>  
          <v-text-field 
            label="*Short Bio"
            required
            v-model="shortBio"
            :rules="[rules.required, rules.minSize]"
            data-cy="shortBioInput"
          ></v-text-field>
        </v-card-text>

        <v-card-text class="d-flex justify-center pt-2 text-h6">
          <p>Selected Participations</p>
        </v-card-text>

        <!-- Table -->
        <v-card-text style="max-height: 300px; overflow-y: auto;">
          <v-card class="table">
            <v-data-table
              data-cy = "participationsTable"
              v-model="selectedParticipations"
              :headers="headers"
              :search="search"
              :items="this.participations"
              disable-pagination
              :hide-default-footer="true"
              :mobile-breakpoint="0"
              show-select
            >
              <template v-slot:item.activityName="{ item }">
                {{ activityName(item) }}
              </template>
              <template v-slot:item.institutionName="{ item }">
                {{ institutionName(item) }}
              </template>
              <template v-slot:item.memberRating="{ item }">
                {{ getMemberRating(item) }}
              </template>
              <template v-slot:item.acceptanceDate="{ item }">
                {{ getAcceptanceDate(item) }}
              </template>
              <template v-slot:top>
                <v-card-title>
                  <v-text-field
                    v-model="search"
                    append-icon="search"
                    label="Search"
                    class="mx-2"
                  />
                  <v-spacer />
                </v-card-title>
              </template>
            </v-data-table>
          </v-card>
        </v-card-text>
      </v-form>
      
      <!-- Buttons  -->
      <v-card-actions class="pa-4">
        <v-spacer></v-spacer>
        <v-btn elevation="2"
          @click="$emit('close-volunteer-profile-dialog')"
        >Close</v-btn>
        <v-btn 
          data-cy ="saveVolunteerProfile"
          v-if="shortBio.replace(/\s+/g, ' ').trim().length >= 10"
          elevation="2"
          @click="registerVolunteerProfile"
        >Save</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>


<script lang="ts">

import { Vue, Component, Prop, Model } from 'vue-property-decorator';
import Activity from '@/models/activity/Activity';
import RemoteServices from '@/services/RemoteServices';
import VueCtkDateTimePicker from 'vue-ctk-date-time-picker';
import 'vue-ctk-date-time-picker/dist/vue-ctk-date-time-picker.css';
import { ISOtoString } from '@/services/ConvertDateService';
import Participation from '@/models/participation/Participation';
import VolunteerProfile from '@/models/volunteerProfile/VolunteerProfile';

Vue.component('VueCtkDateTimePicker', VueCtkDateTimePicker);
@Component({
  methods: { ISOtoString },
})
export default class VolunteerProfileDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Array, required: true }) readonly activities!: Activity[];
  @Prop({ type: Array, required: true }) readonly participations!: Participation[];
  selectedParticipations: Participation[] = [];
  editVolunteerProfile: VolunteerProfile = new VolunteerProfile();

  shortBio: string = '';
  search: string = '';

  rules = {
    required: (value: string) => !!value || 'Short bio is required',
    minSize: (value: string) => value.replace(/\s+/g, ' ').trim().length >= 10 || 'Bio is too short',
  };
  
  headers: object = [
    {
      text: 'Activity Name',
      value: 'activityName',
      align: 'left',
      width: '20%',
    },
    {
      text: 'Institution',
      value: 'institutionName',
      align: 'left',
      width: '20%',
    },
    {
      text: 'Rating',
      value: 'memberRating',
      align: 'left',
      width: '20%',
    },
    {
      text: 'Review',
      value: 'memberReview',
      align: 'left',
      width: '30%',
    },
    {
      text: 'Acceptance Date',
      value: 'acceptanceDate',
      align: 'left',
      width: '10%',
    }
  ];

  async created() {
  }

  activityName(participation: Participation) {
    return this.activities.find(activity => activity.id == participation.activityId)?.name;
  }

  institutionName(participation: Participation) {
    let activity = this.activities.find(activity => activity.id == participation.activityId);
    return activity?.institution.name;
  }

  convertToStars(rating: number): string {
    const fullStars = '★'.repeat(Math.floor(rating));
    const emptyStars = '☆'.repeat(Math.floor(5 - rating));
    return `${fullStars}${emptyStars} ${rating}/5`;
  }

  getMemberRating(participation: Participation): string {
    if (
      !participation ||
      participation.memberRating == null
    ) {
      return '';
    }
    return this.convertToStars(participation.memberRating);
  }

  getAcceptanceDate(participation: Participation): string {
    if (
      !participation ||
      participation.acceptanceDate == null
    ) {
      return '';
    }
    return participation.acceptanceDate;
  }

  async registerVolunteerProfile(){
    if ((this.$refs.form as Vue & { validate: () => boolean }).validate()) {
      try{
        this.editVolunteerProfile.selectedParticipations = this.selectedParticipations;
        this.editVolunteerProfile.shortBio = this.shortBio;
        
        const result = await RemoteServices.registerVolunteerProfile(this.editVolunteerProfile);
        this.$emit('save-volunteer-profile', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}

</script>
<style scoped lang="scss"></style>