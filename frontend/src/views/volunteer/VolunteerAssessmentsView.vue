<template>
  <div>
    <v-card class="table">
      <v-data-table
        :headers="headers"
        :items="assessments"
        :search="search"
        disable-pagination
        :hide-default-footer="true"
        :mobile-breakpoint="0"
        data-cy="volunteerAssessmentsTable"
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
<!--        <template v-slot:[`item.action`]="{ item }">-->
<!--          <v-tooltip v-if="item.state === 'APPROVED'" bottom>-->
<!--            <template v-slot:activator="{ on }">-->
<!--              <v-icon-->
<!--                class="mr-2 action-button"-->
<!--                color="red"-->
<!--                v-on="on"-->
<!--                data-cy="reportButton"-->
<!--                @click="reportAssessment(item)"-->
<!--                >warning</v-icon-->
<!--              >-->
<!--            </template>-->
<!--            <span>Report Assessment</span>-->
<!--          </v-tooltip>-->
<!--        </template>-->
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
        v-on:save-assessment="onSaveAssessment"
        v-on:close-assessment-dialog="onCloseAssessmentDialog"
      />
    </v-card>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Assessment from '@/models/assessment/Assessment';
import { show } from 'cli-cursor';
import EnrollmentDialog from '@/views/volunteer/EnrollmentDialog.vue';
import Enrollment from '@/models/enrollment/Enrollment';
import AssessmentDialog from '@/views/volunteer/AssessmentDialog.vue';
import Participation from '@/models/participation/Participation';
import Institution from '@/models/institution/Institution';

@Component({
  components: {
    'assessment-dialog': AssessmentDialog,
    'enrollment-dialog': EnrollmentDialog,
  },
  methods: { show },
})
export default class VolunteerAssessmentsView extends Vue {
  assessments: Assessment[] = [];
  search: string = '';

  currentEnrollment: Enrollment | null = null;
  editEnrollmentDialog: boolean = false;

  currentAssessment: Assessment | null = null;
  editAssessmentDialog: boolean = false;

  headers: object = [
    {
      text: 'Institution',
      value: 'institutionName',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Review',
      value: 'review',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Review Date',
      value: 'reviewDate',
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
      this.assessments = await RemoteServices.getVolunteerAssessments();

      console.log(this.assessments);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  onCloseEnrollmentDialog() {
    this.editEnrollmentDialog = false;
    this.currentEnrollment = null;
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
}
</script>

<style lang="scss" scoped></style>
