<template>
  <div class="container">
    <!-- TODO: Add creation button here (only if there is no profile) -->
    <div v-if="!createdProfile">
      <div v-if="!editVolunteerProfileDialog">
        <h2 class="profile-title">Volunteer Profile</h2>
        <p class="profile-text">
          No volunteer profile found. Click the button below to create a new one!
        </p>
        <v-btn 
          color="primary"
          class="profile-button"
          @click="openVolunteerProfileDialog"
        >
          CREATE MY PROFILE
        </v-btn>
      </div>
      <div v-else>
        <volunteer-profile-dialog
          v-if="editVolunteerProfileDialog"
          v-model="editVolunteerProfileDialog"
          :participations="this.participations"
          :activities="this.activities"
          v-on:close-volunteer-profile-dialog="onCloseVolunteerProfileDialog"
          v-on:save-volunteer-profile="onSaveVolunteerProfile"
        />
      </div>
    </div>
       
    <div v-else>
      <h1>Volunteer: {{ getVolunteerName() }}</h1>
      <div class="text-description">
        <p><strong>Short Bio: </strong> {{ getShortBio() }}</p>
      </div>
      <div class="stats-container">
        <div class="items">
          <div ref="volunteerId" class="icon-wrapper">
            <span>{{ getNumTotalEnrollments() }}</span>
          </div>
          <div class="project-name">
            <p>Total Enrollments</p>
          </div>
        </div>
    
        <div class="items">
          <div ref="volunteerId" class="icon-wrapper">
            <span>{{ getNumTotalAssessments() }}</span>
          </div>
          <div class="project-name">
            <p>Total Assessments</p>
          </div>
        </div>
        <div class="items">
          <div ref="volunteerId" class="icon-wrapper">
            <span>{{ getNumTotalParticipations() }}</span>
          </div>
          <div class="project-name">
            <p>Total Participations</p>
          </div>
        </div>
      
        <div class="items">
          <div ref="volunteerId" class="icon-wrapper">
            <span>{{ getAverageRating() }}</span>
          </div>
          <div class="project-name">
            <p>Average rating</p>
          </div>
        </div>
      </div>

      <div>
        <h2>Selected Participations</h2>
        <div>
          <v-card class="table">
            <v-data-table
              :headers="headers"
              :search="search"
              :items="this.volunteerProfile?.selectedParticipations"
              disable-pagination
              :hide-default-footer="true"
              :mobile-breakpoint="0"
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
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from "@/services/RemoteServices";
import Participation from "@/models/participation/Participation";
import Activity from "@/models/activity/Activity";
import VolunteerProfile from '@/models/volunteerProfile/VolunteerProfile';
import VolunteerProfileDialog from '@/views/profile/VolunteerProfileDialog.vue'; 
import User from '@/models/user/User';

@Component({
  components: {
    'volunteer-profile-dialog' : VolunteerProfileDialog,
  }
})
export default class VolunteerProfileView extends Vue {
  userId: number = 0;
  volunteerProfile: VolunteerProfile | null = null;
  activities: Activity[] = [];

  createdProfile: boolean = false;
  editVolunteerProfileDialog: boolean = false;
  participations: Participation[] = [];

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
    await this.$store.dispatch('loading');

    try {
      this.userId = Number(this.$route.params.id);
      this.volunteerProfile = await RemoteServices.getVolunteerProfile(this.userId);
      if(this.$store.getters.getUser !== null && this.$store.getters.getUser.role === 'VOLUNTEER'){
        this.activities = await RemoteServices.getActivities();
        this.participations = await RemoteServices.getVolunteerParticipations();
      }
      if (this.volunteerProfile && this.volunteerProfile.id !== null &&  this.volunteerProfile.id !== undefined) {
        this.createdProfile = true;  // Profile exists
      }
      else{
        this.createdProfile = false; 
      }
      
    } catch (error) {
      await this.$store.dispatch('error', error);
      this.createdProfile = false; 
    }
    await this.$store.dispatch('clearLoading');
  }

  activityName(participation: Participation) {
    return this.activities.find(activity => activity.id == participation.activityId)?.name;
  }

  institutionName(participation: Participation) {
    let activity = this.activities.find(activity => activity.id == participation.activityId);
    return activity?.institution.name;
  }

  getVolunteerName(): string {
    return this.volunteerProfile?.volunteer.name || "the name goes here"; 
  }

  getShortBio(): string {
    return this.volunteerProfile?.shortBio || "SHOW SHORT BIO HERE"; 
  }

  getNumTotalParticipations(): number{
    return this.volunteerProfile?.numTotalParticipations || 0;
  }

  getNumTotalAssessments(): number{
    return this.volunteerProfile?.numTotalAssessments || 0;
  }

  getNumTotalEnrollments(): number{
    return this.volunteerProfile?.numTotalEnrollments || 0;
  }

  getAverageRating(): number {
    return this.volunteerProfile?.averageRating || 0;
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

  convertToStars(rating: number): string {
    const fullStars = '★'.repeat(Math.floor(rating));
    const emptyStars = '☆'.repeat(Math.floor(5 - rating));
    return `${fullStars}${emptyStars} ${rating}/5`;
  }

  openVolunteerProfileDialog() {
    this.editVolunteerProfileDialog = true;
  }

  registerVolunteerProfile() {
    this.volunteerProfile = new VolunteerProfile();
    this.editVolunteerProfileDialog = true;
  }

  onSaveVolunteerProfile(volunteerProfile : VolunteerProfile){
    this.createdProfile = true;
    this.volunteerProfile = volunteerProfile;
    this.editVolunteerProfileDialog = false;
  }

  onCloseVolunteerProfileDialog(){
    this.editVolunteerProfileDialog = false;
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
