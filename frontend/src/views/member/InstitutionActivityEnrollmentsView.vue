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
        <v-tooltip v-if="canParticipate(item) &&  checkIfEnrollmentPeriodIsOver(item)" bottom>
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
          v-if="isParticipating(item) && checkIfActivityHasEnded()"
          bottom
        >
          <template v-slot:activator="{ on }">
            <v-icon
              class="mr-2 action-button"
              color="blue"
              @click="selectParticipant(item)"
              v-on="on"
              data-cy="editParticipantButton"
            >
              {{ volunteerReviewWritten(item) ? 'fa-star' : 'fa-solid fa-pen' }}
            </v-icon>
          </template>
          <span>
            {{
              volunteerReviewWritten(item) ? 'Check My Rating' : 'Give Rating'
            }}
          </span>
        </v-tooltip>
        <v-tooltip
          v-if="isParticipating(item) && volunteerReviewWritten(item)"
          bottom
          :key="`show-review-${item.volunteerId}-${item.participating}`"
        >
          <template v-slot:activator="{ on }">
            <v-icon
              class="action-button"
              color="blue"
              @click="showVolunteerReview(item)"
              v-on="on"
              data-cy="showVolunteerReviewButton"
            >
              fa-eye
            </v-icon>
          </template>
          <span>Show Volunteer Rating</span>
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
    <volunteer-rating-dialog
      v-if="currentParticipation && showVolunteerRatingDialog"
      v-model="showVolunteerRatingDialog"
      :participation="currentParticipation"
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
import ParticipationVolunteerRatingDialog from '@/views/member/ParticipationVolunteerRatingDialog.vue';
import ParticipationDeletionDialog from '@/views/member/ParticipationDeletionDialog.vue';

@Component({
  components: {
    'participation-selection-dialog': ParticipationSelectionDialog,
    'participation-deletion-dialog': ParticipationDeletionDialog,
    'volunteer-rating-dialog': ParticipationVolunteerRatingDialog,
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
      width: '50%',
    },
    {
      text: 'Participating',
      value: 'participating',
      align: 'left',
      width: '50%',
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
    let participations =
      await RemoteServices.getActivityParticipations(activityId);

    let existingParticipation = participations.find(
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
    this.enrollments = await RemoteServices.getActivityEnrollments(
      participation.activityId!,
    );
    this.participations = await RemoteServices.getActivityParticipations(
      participation.activityId,
    );
    this.editParticipationDeletionDialog = false;
  }

  async showVolunteerReview(enrollment: Enrollment) {
    let activityId = enrollment.activityId;
    let volunteerId = enrollment.volunteerId;

    let existingParticipation = this.participations.find(
      (p) => p.activityId === activityId && p.volunteerId === volunteerId,
    );

    if (existingParticipation != null) {
      this.currentParticipation = existingParticipation;
      this.currentParticipation.activityId = activityId;
      this.currentParticipation.volunteerId = volunteerId;
      this.showVolunteerRatingDialog = true;
    }
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
          existingParticipation.memberRating &&
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
