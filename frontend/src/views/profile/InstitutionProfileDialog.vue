<template>
  <v-dialog v-model="dialog" persistent width="900" elevation="11">
    <v-card>
      <v-card-title>
        <span class="headline">
         New Institution Profile
        </span>
      </v-card-title>
      <v-card-text>
        <v-form ref="form" lazy-validation>
          <v-row>
           
            <v-col cols="12">
              <v-text-field
                label="*Short description"
                :rules="[
                  (v) => !!v?.trim() || 'Short description is required',
                  (v) => (v && v.length >= 10) || 'Description must be at least 10 characters'
                ]"
                required
                v-model="shortDes"
                data-cy="shortDescriptionInput"
              ></v-text-field>
            </v-col>
            <v-col cols="12" class="text-center">
              <h1>Selected Assessments</h1>
            </v-col>
          

          <v-card class="table">
            <v-data-table
              v-model="selAssessment"
              :headers="headers"
              :search="search"
              :items="filteredAssessments"
              disable-pagination
              :hide-default-footer="true"
              :mobile-breakpoint="0"
              show-select
              item-key="id"
              
              data-cy="institutionAssessmentsTable"
            >
             
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
       
        
          </v-row>
        </v-form>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn
          color="blue-darken-1"
          variant="text"
          
          data-cy="closeInstitutionProfile"
        >
          Close
        </v-btn>
      

        <v-btn
          v-if=" shortDes.trim().length > 10"
          color="blue-darken-1"
          variant="text"
          
          data-cy="saveInstitutionProfile"
        >
          Save
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
<script lang="ts">
import { Vue, Component, Prop, Model } from 'vue-property-decorator';
import InstitutionProfile from '@/models/profile/InstitutionProfile';
import Assessment from '@/models/assessment/Assessment';
import RemoteServices from '@/services/RemoteServices';
import VueCtkDateTimePicker from 'vue-ctk-date-time-picker';
import 'vue-ctk-date-time-picker/dist/vue-ctk-date-time-picker.css';
import { ISOtoString } from '@/services/ConvertDateService';


Vue.component('VueCtkDateTimePicker', VueCtkDateTimePicker);
@Component({
  methods: { ISOtoString },
})
export default class InstitutionProfileDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Array, required: true }) readonly assessments!: Assessment[];

  institutionProfile: InstitutionProfile = new InstitutionProfile();
  search: string = '';
  selAssessment: Assessment[] = [];
  shortDes: string = ''
  cypressCondition: boolean = false;
  
  headers = [
    { text: 'Volunteer Name', value: 'volunteerName' },
    { text: 'Review', value: 'review' },
    { text: 'Review Date', value: 'reviewDate' }
  ];

  get filteredAssessments() {
    if (!this.assessments || this.assessments.length === 0) {
      return [];
    }
    return this.assessments.filter((item) => {
      const searchTerm = this.search.toLowerCase();
      return (
        item.volunteerName?.toLowerCase().includes(searchTerm) ||
        item.review?.toLowerCase().includes(searchTerm) ||
        item.reviewDate?.toLowerCase().includes(searchTerm)
      );
    });
  }


  async created() {
  }

}
</script>

<style scoped lang="scss">
.v-input--underlined .v-input__control {
  border-bottom: 2px solid #1976D2; /* Adjust the color or thickness as needed */
}

</style>
