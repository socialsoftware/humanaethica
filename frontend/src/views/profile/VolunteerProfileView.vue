<template>
  <div class="container">
    <!-- TODO: Add creation button here (only if there is no profile) -->
    <div>
      <h1>Volunteer: SHOW VOLUNTEER NAME HERE</h1>
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

@Component({
  components: {
  }
})
export default class VolunteerProfileView extends Vue {
  userId: number = 0;

  activities: Activity[] = [];

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
      this.activities = await RemoteServices.getActivities();

      // TODO
    } catch (error) {
      await this.$store.dispatch('error', error);
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