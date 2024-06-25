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
        <v-tooltip v-if="canParticipate(item)" bottom>
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
        <v-tooltip v-if="isParticipating(item)" bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              class="mr-2 action-button"
              color="blue"
              @click="selectParticipant(item)"
              v-on="on"
              data-cy="editParticipantButton"
              >edit
            </v-icon>
          </template>
          <span>Edit Participant</span>
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
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Activity from '@/models/activity/Activity';
import Enrollment from '@/models/enrollment/Enrollment';
import ParticipationSelectionDialog from '@/views/member/ParticipationSelectionDialog.vue';
import Participation from '@/models/participation/Participation';

@Component({
  components: {
    'participation-selection-dialog': ParticipationSelectionDialog,
  },
})
export default class InstitutionActivityEnrollmentsView extends Vue {
  activity!: Activity;
  enrollments: Enrollment[] = [];
  search: string = '';

  currentParticipation: Participation | null = null;
  editParticipationSelectionDialog: boolean = false;

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
    } else {
      this.currentParticipation = new Participation();
      this.currentParticipation.activityId = activityId;
      this.currentParticipation.volunteerId = volunteerId;
    }

    this.editParticipationSelectionDialog = true;
  }

  onCloseParticipationDialog() {
    this.currentParticipation = null;
    this.editParticipationSelectionDialog = false;
  }

  async onSaveParticipation(participation: Participation) {
    let enrollment = this.enrollments.find(
      (e) => e.volunteerId === participation.volunteerId,
    );
    if (enrollment) enrollment.participating = true;

    this.currentParticipation = null;
    this.editParticipationSelectionDialog = false;
  }

  async onDeleteParticipation(enrollment: Enrollment) {
    this.enrollments = await RemoteServices.getActivityEnrollments(
      enrollment.activityId!,
    );
  }

  async deleteParticipation(enrollment: Enrollment) {
    let activityId = enrollment.activityId;
    let volunteerId = enrollment.volunteerId;
    let participations =
      await RemoteServices.getActivityParticipations(activityId);

    let existingParticipation = participations.find(
      (p) => p.activityId === activityId && p.volunteerId === volunteerId,
    );
    if (existingParticipation && existingParticipation.id !== null) {
      try {
        await RemoteServices.deleteParticipation(existingParticipation.id);
        this.onDeleteParticipation(enrollment);
      } catch (error) {
        await this.$store.dispatch('error', error);
        console.error('Error deleting participation:', error);
      }
    }
  }

  canParticipate(enrollment: Enrollment) {
    console.log(this.enrollments.filter((e) => e.participating).length);
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
