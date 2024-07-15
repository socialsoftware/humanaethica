<template>
  <v-card class="table">
    <div class="text-h3">{{ activity.name }}</div>
    <v-data-table
      :headers="headers"
      :items="enrollments"
      :search="search"
      disable-pagination
      :hide-default-footer="true"
      :mobile-breakpoint="0"
      data-cy="activityEnrollmentsTable"
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
          <v-btn
            color="primary"
            dark
            @click="getActivities"
            data-cy="getActivities"
            >Activities</v-btn
          >
        </v-card-title>
      </template>
      <template v-slot:[`item.action`]="{ item }">
        <v-tooltip
          v-if="canParticipate(item) && checkIfEnrollmentPeriodIsOver()"
          bottom
        >
          <template v-slot:activator="{ on }">
            <v-icon
              class="mr-2 action-button"
              @click="selectParticipant(item)"
              v-on="on"
              data-cy="selectParticipantButton"
              >check
            </v-icon>
          </template>
          <span>Select Participant</span>
        </v-tooltip>
        <v-tooltip
          v-if="
            isParticipating(item) &&
            checkIfActivityHasEnded() &&
            !volunteerReviewWritten(item)
          "
          bottom
        >
          <template v-slot:activator="{ on }">
            <v-icon
              class="mr-2 action-button"
              color="blue"
              @click="selectParticipant(item)"
              v-on="on"
              data-cy="editParticipantButton"
              >fa-solid fa-pen
            </v-icon>
          </template>
          <span> Give Rating </span>
        </v-tooltip>
        <v-tooltip
          v-if="isParticipating(item)"
          bottom
          :key="`delete-${item.volunteerId}-${item.participating}`"
        >
          <template v-slot:activator="{ on }">
            <v-icon
              class="action-button"
              color="red"
              @click="deleteParticipation(item)"
              v-on="on"
              data-cy="deleteParticipantButton"
            >
              delete
            </v-icon>
          </template>
          <span>Delete Participant</span>
        </v-tooltip>
      </template>
      <template v-slot:[`item.memberReview`]="{ item }">
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <span class="review-text" v-on="on">{{
              getMemberReview(item)
            }}</span>
          </template>
          <span>{{ getMemberReview(item) }}</span>
        </v-tooltip>
      </template>
      <template v-slot:[`item.volunteerReview`]="{ item }">
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <span class="review-text" v-on="on">{{
              getVolunteerReview(item)
            }}</span>
          </template>
          <span>{{ getVolunteerReview(item) }}</span>
        </v-tooltip>
      </template>
    </v-data-table>
    <participation-selection-dialog
      v-if="currentParticipation && editParticipationSelectionDialog"
      v-model="editParticipationSelectionDialog"
      :participation="currentParticipation"
      v-on:save-participation="onSaveParticipation"
      v-on:close-participation-dialog="onCloseParticipationDialog"
    />
    <participation-deletion-dialog
      v-if="currentParticipation && editParticipationDeletionDialog"
      v-model="editParticipationDeletionDialog"
      :participation="currentParticipation"
      v-on:delete-participation="onDeleteParticipation"
      v-on:close-participation-dialog="onCloseParticipationDialog"
    />
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Activity from '@/models/activity/Activity';
import Enrollment from '@/models/enrollment/Enrollment';
import ParticipationSelectionDialog from '@/views/member/ParticipationSelectionDialog.vue';
import Participation from '@/models/participation/Participation';
import ParticipationDeletionDialog from '@/views/member/ParticipationDeletionDialog.vue';

@Component({
  components: {
    'participation-selection-dialog': ParticipationSelectionDialog,
    'participation-deletion-dialog': ParticipationDeletionDialog,
  },
})
export default class InstitutionActivityEnrollmentsView extends Vue {
  activity!: Activity;
  enrollments: Enrollment[] = [];
  participations: Participation[] = [];
  search: string = '';

  currentParticipation: Participation | null = null;
  editParticipationSelectionDialog: boolean = false;
  editParticipationDeletionDialog: boolean = false;
  showVolunteerRatingDialog: boolean = false;

  headers: object = [
    {
      text: 'Name',
      value: 'volunteerName',
      align: 'left',
      width: '30%',
    },
    {
      text: 'Motivation',
      value: 'motivation',
      align: 'left',
      width: '30%',
    },
    {
      text: 'Member Rating',
      value: 'memberReview',
      align: 'left',
      width: '20%',
    },
    {
      text: 'Volunteer Rating',
      value: 'volunteerReview',
      align: 'left',
      width: '20%',
    },
    {
      text: 'Participating',
      value: 'participating',
      align: 'left',
      width: '10%',
    },
    {
      text: 'Application Date',
      value: 'enrollmentDateTime',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Actions',
      value: 'action',
      align: 'left',
      sortable: false,
      width: '5%',
    },
  ];

  async created() {
    this.activity = this.$store.getters.getActivity;
    if (this.activity !== null && this.activity.id !== null) {
      await this.$store.dispatch('loading');
      try {
        this.enrollments = await RemoteServices.getActivityEnrollments(
          this.activity.id,
        );
        this.participations = await RemoteServices.getActivityParticipations(
          this.activity.id,
        );
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
      await this.$store.dispatch('clearLoading');
    }
  }

  async selectParticipant(enrollment: Enrollment) {
    let activityId = enrollment.activityId;
    let volunteerId = enrollment.volunteerId;

    let existingParticipation = this.participations.find(
      (p) => p.activityId === activityId && p.volunteerId === volunteerId,
    );

    if (existingParticipation) {
      this.currentParticipation = existingParticipation;
      this.currentParticipation.activityId = activityId;
      this.currentParticipation.volunteerId = volunteerId;
      this.editParticipationSelectionDialog = true;
    } else if (this.checkIfActivityHasEnded()) {
      this.currentParticipation = new Participation();
      this.currentParticipation.activityId = activityId;
      this.currentParticipation.volunteerId = volunteerId;
      this.editParticipationSelectionDialog = true;
    } else {
      this.createParticipation(activityId!, volunteerId!);
    }
  }

  async createParticipation(activityId: number, volunteerId: number) {
    this.currentParticipation = new Participation();
    this.currentParticipation.activityId = activityId;
    this.currentParticipation.volunteerId = volunteerId;
    await RemoteServices.createParticipation(
      this.currentParticipation!.activityId!,
      this.currentParticipation!,
    );
    this.currentParticipation = null;
    this.enrollments = await RemoteServices.getActivityEnrollments(activityId!);
    this.participations = await RemoteServices.getActivityParticipations(
      activityId!,
    );
  }

  checkIfActivityHasEnded() {
    let endingDate = new Date(this.activity.endingDate);
    let now = new Date();
    return endingDate < now;
  }

  checkIfEnrollmentPeriodIsOver() {
    let endingDate = new Date(this.activity.applicationDeadline);
    let now = new Date();
    return endingDate < now;
  }

  onCloseParticipationDialog() {
    this.currentParticipation = null;
    this.editParticipationSelectionDialog = false;
    this.editParticipationDeletionDialog = false;
    this.showVolunteerRatingDialog = false;
  }

  async onSaveParticipation(participation: Participation) {
    let enrollment = this.enrollments.find(
      (e) => e.volunteerId === participation.volunteerId,
    );
    this.participations = await RemoteServices.getActivityParticipations(
      participation.activityId,
    );
    if (enrollment) enrollment.participating = true;

    this.currentParticipation = null;
    this.editParticipationSelectionDialog = false;
  }

  async onDeleteParticipation(participation: Participation) {
    this.participations = await RemoteServices.getActivityParticipations(
      participation.activityId,
    );
    let enrollment = this.enrollments.find(
      (e) => e.volunteerId === participation.volunteerId,
    );
    if (enrollment) enrollment.participating = false;
    this.currentParticipation = null;
    this.editParticipationDeletionDialog = false;
  }

  async deleteParticipation(enrollment: Enrollment) {
    let activityId = enrollment.activityId;
    let volunteerId = enrollment.volunteerId;

    let existingParticipation = this.participations.find(
      (p) => p.activityId === activityId && p.volunteerId === volunteerId,
    );

    if (existingParticipation != null) {
      this.currentParticipation = existingParticipation;
      this.currentParticipation.activityId = activityId;
      this.currentParticipation.volunteerId = volunteerId;
      this.editParticipationDeletionDialog = true;
    }
  }

  canParticipate(enrollment: Enrollment) {
    return (
      !enrollment.participating &&
      this.activity.participantsNumberLimit >
        this.enrollments.filter((e) => e.participating).length
    );
  }

  isParticipating(enrollment: Enrollment) {
    if (enrollment.participating) {
      return true;
    }
  }

  volunteerReviewWritten(enrollment: Enrollment) {
    try {
      const existingParticipation = this.participations.find(
        (p) =>
          p.activityId === enrollment.activityId &&
          p.volunteerId === enrollment.volunteerId,
      );

      if (existingParticipation) {
        return !!(
          existingParticipation.volunteerRating &&
          existingParticipation.volunteerReview
        );
      } else {
        return false;
      }
    } catch (error) {
      console.error('Error fetching participations:', error);
      return false;
    }
  }

  getMemberReview(enrollment: Enrollment): string {
    let activityId = enrollment.activityId;
    let volunteerId = enrollment.volunteerId;

    let participation = this.participations.find(
      (p) => p.activityId === activityId && p.volunteerId === volunteerId,
    );

    if (
      !participation ||
      participation.memberReview == null ||
      participation.memberRating == null
    ) {
      return '';
    }
    const stars = this.convertToStars(participation.memberRating);
    return `${participation.memberReview}\nRating: ${stars}`;
  }
  getVolunteerReview(enrollment: Enrollment): string {
    let activityId = enrollment.activityId;
    let volunteerId = enrollment.volunteerId;

    let participation = this.participations.find(
      (p) => p.activityId === activityId && p.volunteerId === volunteerId,
    );

    if (
      !participation ||
      participation.volunteerReview == null ||
      participation.volunteerRating == null
    ) {
      return '';
    }
    const stars = this.convertToStars(participation.volunteerRating);
    return `${participation.volunteerReview}\nRating: ${stars}`;
  }

  convertToStars(rating: number): string {
    const fullStars = '★'.repeat(Math.floor(rating));
    const emptyStars = '☆'.repeat(Math.floor(5 - rating));
    return `${fullStars}${emptyStars} ${rating}/5`;
  }

  async getActivities() {
    await this.$store.dispatch('setActivity', null);
    this.$router.push({ name: 'institution-activities' }).catch(() => {});
  }
}
</script>

<style lang="scss" scoped>
.date-fields-container {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.date-fields-row {
  display: flex;
  gap: 16px;
  margin-top: 8px;
}
</style>

<style>
.review-text {
  white-space: pre-line;
}
</style>
