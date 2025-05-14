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
      :dialog.sync="editParticipationSelectionDialog"
      :participation="currentParticipation"
      @save-participation="onSaveParticipation"
      @close-participation-dialog="onCloseParticipationDialog"
    />
    <participation-deletion-dialog
      v-if="currentParticipation"
      :dialog.sync="editParticipationDeletionDialog"
      :participation="currentParticipation"
      @delete-participation="onDeleteParticipation"
      @close-participation-dialog="onCloseParticipationDialog"
    />
  </v-card>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import RemoteServices from '@/services/RemoteServices';
import Activity from '@/models/activity/Activity';
import Enrollment from '@/models/enrollment/Enrollment';
import Participation from '@/models/participation/Participation';
import ParticipationSelectionDialog from '@/views/member/ParticipationSelectionDialog.vue';
import ParticipationDeletionDialog from '@/views/member/ParticipationDeletionDialog.vue';

export default defineComponent({
  name: 'InstitutionActivityEnrollmentsView',
  components: {
    ParticipationSelectionDialog,
    ParticipationDeletionDialog,
  },
  data() {
    return {
      activity: {} as Activity,
      enrollments: [] as Enrollment[],
      participations: [] as Participation[],
      search: '',
      currentParticipation: null as Participation | null,
      editParticipationSelectionDialog: false,
      editParticipationDeletionDialog: false,
      headers: [
        { text: 'Name', value: 'volunteerName', align: 'left', width: '30%' },
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
      ],
    };
  },
  async created() {
    this.activity = this.$store.getters.getActivity;
    if (this.activity?.id) {
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
  },
  methods: {
    async getActivities() {
      await this.$store.dispatch('setActivity', null);
      this.$router.push({ name: 'institution-activities' }).catch(() => {});
    },
    async selectParticipant(enrollment: Enrollment) {
      const existing = this.participations.find(
        (p) =>
          p.activityId === enrollment.activityId &&
          p.volunteerId === enrollment.volunteerId,
      );
      if (existing) {
        this.currentParticipation = existing;
        this.editParticipationSelectionDialog = true;
      } else if (this.checkIfActivityHasEnded()) {
        this.currentParticipation = new Participation();
        this.currentParticipation.activityId = enrollment.activityId;
        this.currentParticipation.volunteerId = enrollment.volunteerId;
        this.editParticipationSelectionDialog = true;
      } else {
        await this.createParticipation(
          enrollment.activityId!,
          enrollment.volunteerId!,
        );
      }
    },
    async createParticipation(activityId: number, volunteerId: number) {
      const newParticipation = new Participation();
      newParticipation.activityId = activityId;
      newParticipation.volunteerId = volunteerId;
      await RemoteServices.createParticipation(activityId, newParticipation);
      this.currentParticipation = null;
      this.enrollments =
        await RemoteServices.getActivityEnrollments(activityId);
      this.participations =
        await RemoteServices.getActivityParticipations(activityId);
    },
    checkIfActivityHasEnded() {
      return new Date(this.activity.endingDate) < new Date();
    },
    checkIfEnrollmentPeriodIsOver() {
      return new Date(this.activity.applicationDeadline) < new Date();
    },
    onCloseParticipationDialog() {
      this.currentParticipation = null;
      this.editParticipationSelectionDialog = false;
      this.editParticipationDeletionDialog = false;
    },
    async onSaveParticipation(part: Participation) {
      const enrollment = this.enrollments.find(
        (e) => e.volunteerId === part.volunteerId,
      );
      if (enrollment) enrollment.participating = true;
      this.participations = await RemoteServices.getActivityParticipations(
        part.activityId,
      );
      this.currentParticipation = null;
      this.editParticipationSelectionDialog = false;
    },
    async onDeleteParticipation(part: Participation) {
      const enrollment = this.enrollments.find(
        (e) => e.volunteerId === part.volunteerId,
      );
      if (enrollment) enrollment.participating = false;
      this.participations = await RemoteServices.getActivityParticipations(
        part.activityId,
      );
      this.currentParticipation = null;
      this.editParticipationDeletionDialog = false;
    },
    async deleteParticipation(enrollment: Enrollment) {
      const part = this.participations.find(
        (p) =>
          p.activityId === enrollment.activityId &&
          p.volunteerId === enrollment.volunteerId,
      );
      if (part) {
        this.currentParticipation = part;
        this.editParticipationDeletionDialog = true;
      }
    },
    canParticipate(enrollment: Enrollment) {
      return (
        !enrollment.participating &&
        this.activity.participantsNumberLimit >
          this.enrollments.filter((e) => e.participating).length
      );
    },
    isParticipating(enrollment: Enrollment) {
      return !!enrollment.participating;
    },
    volunteerReviewWritten(enrollment: Enrollment) {
      const part = this.participations.find(
        (p) =>
          p.activityId === enrollment.activityId &&
          p.volunteerId === enrollment.volunteerId,
      );
      return !!(part?.volunteerRating && part?.volunteerReview);
    },
    getMemberReview(enrollment: Enrollment): string {
      const part = this.participations.find(
        (p) =>
          p.activityId === enrollment.activityId &&
          p.volunteerId === enrollment.volunteerId,
      );
      if (!part?.memberReview || !part?.memberRating) return '';
      return `${part.memberReview}\nRating: ${this.convertToStars(
        part.memberRating,
      )}`;
    },
    getVolunteerReview(enrollment: Enrollment): string {
      const part = this.participations.find(
        (p) =>
          p.activityId === enrollment.activityId &&
          p.volunteerId === enrollment.volunteerId,
      );
      if (!part?.volunteerReview || !part?.volunteerRating) return '';
      return `${part.volunteerReview}\nRating: ${this.convertToStars(
        part.volunteerRating,
      )}`;
    },
    convertToStars(rating: number): string {
      return `${'★'.repeat(Math.floor(rating))}${'☆'.repeat(
        5 - Math.floor(rating),
      )} ${rating}/5`;
    },
  },
});
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
