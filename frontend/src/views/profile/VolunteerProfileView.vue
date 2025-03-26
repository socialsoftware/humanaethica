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
          @click="registerVolunteerProfile"
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
        />
      </div>
    </div>
       
    <div v-else>
      <h1>Volunteer: the name goes here</h1>
      <div class="text-description">
        <p><strong>Short Bio: </strong> SHOW SHORT BIO HERE</p>
      </div>
      <div class="stats-container">
        <div class="items">
          <div ref="volunteerId" class="icon-wrapper">
            <span>42</span>
          </div>
          <div class="project-name">
            <p>Total Enrollments</p>
          </div>
        </div>
        <!-- TODO: Change 42 above and add other fields here -->
    
        <div class="items">
          <div ref="volunteerId" class="icon-wrapper">
            <span>42</span>
          </div>
          <div class="project-name">
            <p>Total Assessments</p>
          </div>
        </div>
        <!-- TODO: Change 42 above and add other fields here -->
        <div class="items">
          <div ref="volunteerId" class="icon-wrapper">
            <span>{{ getNumTotalParticipations() }}</span>
          </div>
          <div class="project-name">
            <p>Total Participations</p>
          </div>
        </div>
        <!-- TODO: Change 42 above and add other fields here -->
      
        <div class="items">
          <div ref="volunteerId" class="icon-wrapper">
            <span>42</span>
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
  volunteerProfile: VolunteerProfile = new VolunteerProfile();
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
      if(this.$store.getters.getUser !== null && this.$store.getters.getUser.role === 'VOLUNTEER'){
        this.activities = await RemoteServices.getActivities();
        this.volunteerProfile = await RemoteServices.getVolunteerProfile(this.userId);
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

  getNumTotalParticipations(): number{
    //return this.volunteerProfile?.numTotalParticipations || 0;
    return 3;
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

  registerVolunteerProfile() {
    this.editVolunteerProfileDialog = true;
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
