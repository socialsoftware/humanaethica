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
        <template v-slot:[`item.state`]="{ item }">
          <v-tooltip bottom>
            <template v-slot:activator="{ on }">
              <v-chip
                v-if="item.state === 'REPORTED'"
                class="mouseover"
                @mouseover="showJustification(item)"
                @mouseleave="hideJustification(item)"
                v-on="on"
              >
                {{ item.state }}
              </v-chip>
              <v-chip v-else>
                {{ item.state }}
              </v-chip>
            </template>
            <span v-html="formattedJustification(item.justification)"></span>
          </v-tooltip>
        </template>
        <template v-slot:[`item.action`]="{ item }">
          <v-tooltip v-if="item.state === 'APPROVED' && canReport(item)" bottom>
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
          <v-tooltip
            v-if="item.state === 'REPORTED' && canUnReport(item)"
            bottom
          >
            <template v-slot:activator="{ on }">
              <v-icon
                class="mr-2 action-button"
                color="blue"
                v-on="on"
                data-cy="UnReportButton"
                @click="unReportActivity(item)"
                >warning</v-icon
              >
            </template>
            <span>Unreport Activity</span>
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
        :dialog.sync="editEnrollmentDialog"
        :enrollment="currentEnrollment"
        @save-enrollment="onSaveEnrollment"
        @close-enrollment-dialog="onCloseEnrollmentDialog"
      />
      <assessment-dialog
        v-if="currentAssessment && editAssessmentDialog"
        :dialog.sync="editAssessmentDialog"
        :assessment="currentAssessment"
        :is_update="false"
        @save-assessment="onSaveAssessment"
        @close-assessment-dialog="onCloseAssessmentDialog"
      />
      <report-dialog
        v-if="currentReport && createReportDialog"
        :dialog.sync="createReportDialog"
        :report="currentReport"
        @save-report="onSaveReport"
        @close-report-dialog="onCloseReportDialog"
      />
    </v-card>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import RemoteServices from '@/services/RemoteServices';
import Activity from '@/models/activity/Activity';
import EnrollmentDialog from '@/views/volunteer/EnrollmentDialog.vue';
import Enrollment from '@/models/enrollment/Enrollment';
import AssessmentDialog from '@/views/volunteer/AssessmentDialog.vue';
import Assessment from '@/models/assessment/Assessment';
import Participation from '@/models/participation/Participation';
import ReportDialog from '@/views/volunteer/ReportDialog.vue';
import Report from '@/models/report/Report';

export default defineComponent({
  name: 'VolunteerActivitiesView',
  components: {
    'assessment-dialog': AssessmentDialog,
    'enrollment-dialog': EnrollmentDialog,
    'report-dialog': ReportDialog,
  },
  data() {
    return {
      activities: [] as Activity[],
      enrollments: [] as Enrollment[],
      participations: [] as Participation[],
      assessments: [] as Assessment[],
      reports: [] as Report[],
      search: '',
      currentEnrollment: null as Enrollment | null,
      editEnrollmentDialog: false,
      currentAssessment: null as Assessment | null,
      editAssessmentDialog: false,
      currentActivtiy: null as Activity | null,
      currentReport: null as Report | null,
      createReportDialog: false,
      headers: [
        { text: 'Name', value: 'name', align: 'left', width: '5%' },
        { text: 'Region', value: 'region', align: 'left', width: '5%' },
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
        { text: 'Themes', value: 'themes', align: 'left', width: '5%' },
        {
          text: 'Description',
          value: 'description',
          align: 'left',
          width: '30%',
        },
        { text: 'State', value: 'state', align: 'left', width: '5%' },
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
      ],
    };
  },
  async created() {
    await this.$store.dispatch('loading');
    try {
      this.activities = await RemoteServices.getActivities();
      this.enrollments = await RemoteServices.getVolunteerEnrollments();
      this.participations = await RemoteServices.getVolunteerParticipations();
      this.assessments = await RemoteServices.getVolunteerAssessments();
      this.reports = await RemoteServices.getVolunteerReportsAsVolunteer();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
    this.updateActivitiesList();
  },
  methods: {
    reportActivity(activity: Activity) {
      this.currentReport = new Report();
      this.currentReport.activityId = activity.id;
      this.createReportDialog = true;
      this.currentActivtiy = activity;
    },
    canEnroll(activity: Activity) {
      const deadline = new Date(activity.applicationDeadline);
      return (
        deadline > new Date() &&
        !this.enrollments.some((e) => e.activityId === activity.id)
      );
    },
    applyForActivity(activity: Activity) {
      this.currentEnrollment = new Enrollment();
      this.currentEnrollment.activityId = activity.id;
      this.editEnrollmentDialog = true;
    },
    onCloseEnrollmentDialog() {
      this.editEnrollmentDialog = false;
      this.currentEnrollment = null;
    },
    onSaveEnrollment(enrollment: Enrollment) {
      this.enrollments.push(enrollment);
      this.editEnrollmentDialog = false;
      this.currentEnrollment = null;
      this.updateActivitiesList();
    },
    canAssess(activity: Activity) {
      const now = new Date();
      const endDate = new Date(activity.endingDate);
      return (
        now > endDate &&
        this.participations.some((p) => p.activityId === activity.id) &&
        !this.assessments.some(
          (a) => a.institutionId === activity.institution.id,
        )
      );
    },
    writeAssessment(activity: Activity) {
      this.currentAssessment = new Assessment();
      this.currentAssessment.institutionId = activity.institution.id;
      this.editAssessmentDialog = true;
    },
    onCloseAssessmentDialog() {
      this.editAssessmentDialog = false;
      this.currentAssessment = null;
    },
    onSaveAssessment(assessment: Assessment) {
      this.assessments.push(assessment);
      this.editAssessmentDialog = false;
      this.currentAssessment = null;
    },
    updateActivitiesList() {
      this.activities = this.activities.filter(
        (a) => !this.enrollments.some((e) => e.activityId === a.id),
      );
    },
    canReport(activity: Activity) {
      const deadline = new Date(activity.endingDate);
      return (
        deadline > new Date() &&
        !this.reports.some((r) => r.activityId === activity.id)
      );
    },
    onCloseReportDialog() {
      this.createReportDialog = false;
      this.currentReport = null;
    },
    async onSaveReport(report: Report) {
      this.reports.push(report);
      this.createReportDialog = false;
      this.currentReport = null;

      if (this.currentActivtiy && this.currentActivtiy.id !== null) {
        try {
          const result = await RemoteServices.reportActivity(
            this.$store.getters.getUser.id,
            this.currentActivtiy.id,
          );
          this.activities = this.activities.filter(
            (a) => a.id !== this.currentActivtiy!.id,
          );
          this.activities.unshift(result);
        } catch (error) {
          await this.$store.dispatch('error', error);
        }
      }
      this.currentActivtiy = null;
    },
    canUnReport(activity: Activity) {
      const deadline = new Date(activity.endingDate);
      return (
        deadline > new Date() &&
        this.reports.some((r) => r.activityId === activity.id)
      );
    },
    async unReportActivity(activity: Activity) {
      const index = this.reports.findIndex((r) => r.activityId == activity.id);
      this.currentReport = this.reports[index];

      if (activity.id !== null) {
        try {
          const result = await RemoteServices.validateActivity(activity.id);
          this.activities = this.activities.filter((a) => a.id !== activity.id);
          this.activities.unshift(result);
        } catch (error) {
          await this.$store.dispatch('error', error);
        }
      }

      if (this.currentReport && this.currentReport.id !== null) {
        try {
          await RemoteServices.deleteReport(this.currentReport.id);
          this.reports = await RemoteServices.getVolunteerReportsAsVolunteer();
        } catch (error) {
          await this.$store.dispatch('error', error);
        }
      }
      this.currentReport = null;
    },
    async showJustification(activity: Activity) {
      const index = this.reports.findIndex((r) => r.activityId == activity.id);
      this.currentReport = this.reports[index];

      if (this.currentReport) {
        this.$set(activity, 'justification', this.currentReport.justification);
        this.$set(activity, 'showJustification', true);
      }
    },
    hideJustification(activity: Activity) {
      this.$set(activity, 'showJustification', false);
    },
    formattedJustification(justification: string) {
      if (!justification) return '';
      return justification.replace(/(.{20})/g, '$1<br>');
    },
  },
});
</script>

<style lang="scss" scoped>
.mouseover {
  cursor: pointer;
}
</style>
