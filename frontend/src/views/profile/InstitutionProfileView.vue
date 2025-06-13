<template>
  <div class="container">
    <div v-if="!hasInstitutionProfile">
      <div v-if="!createDialog">
        <p class="font-weight-bold text-h4">Institution Profile</p>
        <div v-if="isMember">
          <p class="pb-4">No institution profile found. Click the button below to create a new one!</p>
          <v-btn
              color="primary"
              dark
              @click="createInstitutionProfile"
              data-cy="createInstitutionPButton"
              >CREATE INSTITUTION PROFILE</v-btn>
        </div>
        <div v-else>
          <p>No institution profile found.</p>
        </div>
      </div>
      <div v-else> 
        <create-institutionProfile
          v-model="createDialog"
          :assessments="assessments"
          :institutionProfileParent="institutionProfile"
          v-on:save-institutionProfile="onSaveInstitutionProfile"
          v-on:close-institutionProfile="onCloseInstitutionProfileDialog"
        />
      </div>
    </div>
    <div v-else>
      <h1>Institution: {{ institutionProfile?.institution?.name ?? 'N/A' }}</h1>
      <v-btn
        color="primary"
        dark
        @click="subscribeToInstitution"
        data-cy="subscribeButton"
      >
        Subscribe
      </v-btn>
      <div class="text-description" data-cy="shortDescription">
        <p><strong>Short Description: </strong> {{ institutionProfile?.shortDescription ?? 'N/A' }}</p>
      </div>
      <div class="stats-container">
        <div class="items">
          <div ref="institutionId" class="icon-wrapper" data-cy="numMembers">
            <span>{{ institutionProfile?.numMembers ?? 'N/A' }}</span>
          </div>
          <div class="project-name">
            <p>Total Members</p>
          </div>
        </div>
        <div class="items">
          <div ref="institutionId" class="icon-wrapper" data-cy="numActivities">
            <span> {{ institutionProfile?.numActivities ?? 'N/A' }}</span>
          </div>
          <div class="project-name">
            <p>Total Activities</p>
          </div>
        </div>
        <div class="items">
          <div ref="institutionId" class="icon-wrapper" data-cy="numVolunteers">
            <span> {{ institutionProfile?.numVolunteers ?? 'N/A' }}</span>
          </div>
          <div class="project-name">
            <p>Total Volunteers</p>
          </div>
        </div>
        
        <div class="items">
          <div ref="institutionId" class="icon-wrapper" data-cy="numAssessments">
            <span> {{ institutionProfile?.numAssessments ?? 'N/A' }}</span>
          </div>
          <div class="project-name">
            <p>Total Assessments</p>
          </div>
        </div>
        <div class="items">
          <div ref="institutionId" class="icon-wrapper" data-cy="averageRating">
            <span> {{ institutionProfile?.averageRating?.toFixed(2) ?? 'N/A' }}</span>
          </div>
          <div class="project-name">
            <p>Average Rating</p>
          </div>
        </div>
     </div>

      <div>
        <h2>Selected Assessments</h2>
        <div>
          <v-card class="table">
            <v-data-table
              :headers="headers"
              :search="search"
              :items="institutionProfile?.selectedAssessments"
              disable-pagination
              :hide-default-footer="true"
              :mobile-breakpoint="0"
              data-cy="institutionProfileAssessmentsTable"
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
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import { ISOtoString } from '../../services/ConvertDateService';
import RemoteServices from '@/services/RemoteServices';
import InstitutionProfile from '@/models/profile/InstitutionProfile';
import Assessment from '@/models/assessment/Assessment';
import InstitutionProfileDialog from '@/views/profile/InstitutionProfileDialog.vue';
import Institution from '@/models/institution/Institution';


@Component({
  methods: { ISOtoString },
  components: {
    'create-institutionProfile': InstitutionProfileDialog,
  }
})
export default class InstitutionProfileView extends Vue {
  institutionProfile: InstitutionProfile | null = null;
  assessments: Assessment[] = []
  hasInstitutionProfile: boolean = false;
  createDialog: boolean = false;
  isMember: boolean = false;
  search: string = '';
  headers = [
    { text: 'Volunteer Name', value: 'volunteerName', align: 'left', width: '30%'},
    { text: 'Review', value: 'review', align: 'left', width: '30%'},
    { text: 'Review Date', value: 'reviewDate', align: 'left', width: '40%' }
  ];

  async created() {
    await this.$store.dispatch('loading');

    try {
      this.institutionProfile = this.$store.getters.getInstitutionProfile;
      
      if(this.$store.getters.getUser !== null && this.$store.getters.getUser.role === 'MEMBER'){
        let institutionId = Number(this.$route.params.id);
        if(!(this.institutionProfile && this.institutionProfile.id !== null && this.institutionProfile.id !== undefined && Number(this.institutionProfile.institution.id) === institutionId)){
     
          this.institutionProfile = await RemoteServices.getInstitutionProfile(institutionId);
          this.assessments = await RemoteServices.getInstitutionAssessments(institutionId);
        }
        
        this.isMember = true;
      }
      if(this.institutionProfile && this.institutionProfile.id !== null && this.institutionProfile.id !== undefined){
        this.hasInstitutionProfile = true;
      }
      await this.$store.dispatch('setInstitutionProfile', null);


    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  createInstitutionProfile(){
    this.institutionProfile = new InstitutionProfile();
    this.createDialog = true;
  }

  onSaveInstitutionProfile(institutionProfile: InstitutionProfile){
    this.institutionProfile = institutionProfile;
    this.hasInstitutionProfile = true;
  }

  onCloseInstitutionProfileDialog(){
    this.institutionProfile = null;
    this.createDialog = false;
  }

  async subscribeToInstitution() {
    console.log("SUBSCRIBED");
    try {
      let userId = this.$store.getters.getUser.id;
      let institutionId = Number(this.$route.params.id);

      await RemoteServices.addSubscription(userId, institutionId);

      this.$store.dispatch('notify', {
        message: 'Successfully subscribed to the institution!',
        type: 'success'
      });
    } catch (error) {
      this.$store.dispatch('error', error);
    }
  }

  async unsubscribeToInstitution() {
    console.log("UNSUBSCRIBED");
    try {
      let userId = this.$store.getters.getUser.id;
      let institutionId = Number(this.$route.params.id);

      await RemoteServices.removeSubscription(userId, institutionId);

      this.$store.dispatch('notify', {
        message: 'Successfully subscribed to the institution!',
        type: 'success'
      });
    } catch (error) {
      this.$store.dispatch('error', error);
    }
  }

}
</script>

<style lang="scss" scoped>
.stats-container {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  justify-content: center;
  align-items: stretch;
  align-content: center;
  height: 100%;

  .items {
    background-color: rgba(255, 255, 255, 0.75);
    color: #696969;
    border-radius: 5px;
    flex-basis: 25%;
    margin: 20px;
    cursor: pointer;
    transition: all 0.6s;
  }
}

.icon-wrapper,
.project-name {
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-wrapper {
  font-size: 100px;
  transform: translateY(0px);
  transition: all 0.6s;
}

.icon-wrapper {
  align-self: end;
}

.project-name {
  align-self: start;
}

.project-name p {
  font-size: 24px;
  font-weight: bold;
  letter-spacing: 2px;
  transform: translateY(0px);
  transition: all 0.5s;
}

.items:hover {
  border: 3px solid black;

  & .project-name p {
    transform: translateY(-10px);
  }

  & .icon-wrapper i {
    transform: translateY(5px);
  }
}

.text-description {
  display: block;
  padding: 1em;
}
</style>