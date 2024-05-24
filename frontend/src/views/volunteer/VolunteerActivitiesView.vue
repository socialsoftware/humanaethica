<template>
  <div>
    <v-card class="table">
      <v-data-table
        :headers="headers"
        :items="activities"
        :search="search"
        disable-pagination
        :hide-default-footer="true"
        :mobile-breakpoint="0"
        data-cy="volunteerActivitiesTable"
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
        <template v-slot:[`item.themes`]="{ item }">
          <v-chip v-for="theme in item.themes" v-bind:key="theme.id">
            {{ theme.completeName }}
          </v-chip>
        </template>
        <template v-slot:[`item.action`]="{ item }">
          <v-tooltip v-if="item.state === 'APPROVED'" bottom>
            <template v-slot:activator="{ on }">
              <v-icon
                class="mr-2 action-button"
                color="red"
                v-on="on"
                data-cy="reportButton"
                @click="reportActivity(item)"
                >warning</v-icon
              >
            </template>
            <span>Report Activity</span>
          </v-tooltip>
          <v-tooltip v-if="item.state === 'APPROVED' && canEnroll(item)" bottom>
            <template v-slot:activator="{ on }">
              <v-icon
                class="mr-2 action-button"
                color="blue"
                v-on="on"
                data-cy="applyButton"
                @click="applyForActivity(item)"
                >fa-sign-in-alt</v-icon
              >
            </template>
            <span>Apply for Activity</span>
          </v-tooltip>
          <v-tooltip v-if="item.state === 'APPROVED' && canAssess(item)" bottom>
            <template v-slot:activator="{ on }">
              <v-icon
                class="mr-2 action-button"
                color="blue"
                v-on="on"
                data-cy="writeAssessmentButton"
                @click="writeAssessment(item)"
                >fa-solid fa-pen-to-square</v-icon
              >
            </template>
            <span>Write Assessment</span>
          </v-tooltip>
        </template>
      </v-data-table>
      <enrollment-dialog
        v-if="currentEnrollment && editEnrollmentDialog"
        v-model="editEnrollmentDialog"
        :enrollment="currentEnrollment"
        v-on:save-enrollment="onSaveEnrollment"
        v-on:close-enrollment-dialog="onCloseEnrollmentDialog"
      />
      <assessment-dialog
        v-if="currentAssessment && editAssessmentDialog"
        v-model="editAssessmentDialog"
        :assessment="currentAssessment"
        :is_update="false"
        v-on:save-assessment="onSaveAssessment"
        v-on:close-assessment-dialog="onCloseAssessmentDialog"
      />
    </v-card>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Activity from '@/models/activity/Activity';
import { show } from 'cli-cursor';
import EnrollmentDialog from '@/views/volunteer/EnrollmentDialog.vue';
import Enrollment from '@/models/enrollment/Enrollment';
import AssessmentDialog from '@/views/volunteer/AssessmentDialog.vue';
import Assessment from '@/models/assessment/Assessment';
import Participation from '@/models/participation/Participation';

@Component({
  components: {
    'assessment-dialog': AssessmentDialog,
    'enrollment-dialog': EnrollmentDialog,
  },
  methods: { show },
})
export default class VolunteerActivitiesView extends Vue {
  activities: Activity[] = [];
  enrollments: Enrollment[] = [];
  participations: Participation[] = [];
  assessments: Assessment[] = [];
  search: string = '';

  currentEnrollment: Enrollment | null = null;
  editEnrollmentDialog: boolean = false;

  currentAssessment: Assessment | null = null;
  editAssessmentDialog: boolean = false;

  headers: object = [
    {
      text: 'Name',
      value: 'name',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Region',
      value: 'region',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Institution',
      value: 'institution.name',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Participants Limit',
      value: 'participantsNumberLimit',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Themes',
      value: 'themes',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Description',
      value: 'description',
      align: 'left',
      width: '30%',
    },
    {
      text: 'State',
      value: 'state',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Start Date',
      value: 'formattedStartingDate',
      align: 'left',
      width: '5%',
    },
    {
      text: 'End Date',
      value: 'formattedEndingDate',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Application Deadline',
      value: 'formattedApplicationDeadline',
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
    await this.$store.dispatch('loading');
    try {
      this.activities = await RemoteServices.getActivities();
      this.enrollments = await RemoteServices.getVolunteerEnrollments();
      this.participations = await RemoteServices.getVolunteerParticipations();
      this.assessments = await RemoteServices.getVolunteerAssessments();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
    this.updateActivitiesList();
  }

  async reportActivity(activity: Activity) {
    if (activity.id !== null) {
      try {
        const result = await RemoteServices.reportActivity(
          this.$store.getters.getUser.id,
          activity.id,
        );
        this.activities = this.activities.filter((a) => a.id !== activity.id);
        this.activities.unshift(result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  canEnroll(activity: Activity) {
    let deadline = new Date(activity.applicationDeadline);
    let now = new Date();

    return (
      deadline > now &&
      !this.enrollments.some((e: Enrollment) => e.activityId === activity.id)
    );
  }

  applyForActivity(activity: Activity) {
    this.currentEnrollment = new Enrollment();
    this.currentEnrollment.activityId = activity.id;
    this.editEnrollmentDialog = true;
  }

  onCloseEnrollmentDialog() {
    this.editEnrollmentDialog = false;
    this.currentEnrollment = null;
  }

  async onSaveEnrollment(enrollment: Enrollment) {
    this.enrollments.push(enrollment);
    this.editEnrollmentDialog = false;
    this.currentEnrollment = null;
    this.updateActivitiesList();
  }

  canAssess(activity: Activity) {
    let endDate = new Date(activity.endingDate);
    let now = new Date();

    return (
      now > endDate &&
      this.participations.some(
        (p: Participation) => p.activityId === activity.id,
      ) &&
      !this.assessments.some(
        (a: Assessment) => a.institutionId === activity.institution.id,
      )
    );
  }

  writeAssessment(activity: Activity) {
    this.currentAssessment = new Assessment();
    this.currentAssessment.institutionId = activity.institution.id;
    this.editAssessmentDialog = true;
  }

  onCloseAssessmentDialog() {
    this.editAssessmentDialog = false;
    this.currentAssessment = null;
  }

  async onSaveAssessment(assessment: Assessment) {
    this.assessments.push(assessment);
    this.editAssessmentDialog = false;
    this.currentAssessment = null;
  }

  updateActivitiesList() {
    this.activities = this.activities.filter(
      (a: Activity) =>
        !this.enrollments.some((e: Enrollment) => e.activityId === a.id),
    );
  }
}
</script>

<style lang="scss" scoped></style>
