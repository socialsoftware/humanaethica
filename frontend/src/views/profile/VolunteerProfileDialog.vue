<template>

  <v-dialog v-model="dialog" persistent width="800">
    <v-card class="container">
      <v-card-title class="d-flex flex-column align-start">
        <h3>New Volunteer Profile</h3>
      </v-card-title>

      <!-- Short Bio Field -->
      <v-card-text>
        <v-text-field label="Short Bio"></v-text-field>
      </v-card-text>

      <v-card-title class="align-center">
        <p>Seleceted Participations</p>
      </v-card-title>

      <v-card-text style="max-height: 300px;">
      <v-card class="table">
          <v-data-table
          :headers="headers"
          :search="search"
         
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

        <v-btn elevation="2"> Save </v-btn>
        <v-btn elevation="2"> Close </v-btn>
      </v-card>
  </v-dialog>

</template>

<script lang="ts">

import { Vue, Component, Prop, Model } from 'vue-property-decorator';
import Activity from '@/models/activity/Activity';
import Theme from '@/models/theme/Theme';
import RemoteServices from '@/services/RemoteServices';
import VueCtkDateTimePicker from 'vue-ctk-date-time-picker';
import 'vue-ctk-date-time-picker/dist/vue-ctk-date-time-picker.css';
import { ISOtoString } from '@/services/ConvertDateService';
import Participation from '@/models/participation/Participation';

Vue.component('VueCtkDateTimePicker', VueCtkDateTimePicker);
@Component({
  methods: { ISOtoString },
})
export default class VolunteerProfileDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Array, required: true }) readonly activities!: Activity[];
  @Prop({ type: Array, required: true }) readonly participations!: Participation[];

  editActivity: Activity = new Activity();

  cypressCondition: boolean = false;

  search: string = '';
  
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
      width: '40%',
    }
  ];

  async created() {
   console.log("entrou dialog1");
  }

  isNumberValid(value: any) {
    if (!/^\d+$/.test(value)) return false;
    const parsedValue = parseInt(value);
    return parsedValue >= 1 && parsedValue <= 5;
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

}

</script>
<style scoped lang="scss"></style>