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
        <template v-slot:[`item.action`]="{ item }">
          <v-tooltip bottom>
            <template v-slot:activator="{ on }">
              <v-icon
                class="mr-2 action-button"
                color="blue"
                v-on="on"
                data-cy="writeAssessmentButton"
                @click="editAssessment(item)"
                >fa-solid fa-pen-to-square</v-icon
              >
            </template>
            <span>Edit Assessment</span>
          </v-tooltip>
          <v-tooltip bottom>
            <template v-slot:activator="{ on }">
              <v-icon
                class="mr-2 action-button"
                color="red"
                v-on="on"
                data-cy="deleteAssessmentButton"
                @click="deleteAssessment(item)"
                >fa-solid fa-trash</v-icon
              >
            </template>
            <span>Delete Assessment</span>
          </v-tooltip>
        </template>
      </v-data-table>
      <assessment-dialog
        v-if="currentAssessment && editAssessmentDialog"
        :dialog.sync="editAssessmentDialog"
        :assessment="currentAssessment"
        :is_update="true"
        @save-assessment="onSaveAssessment"
        @close-assessment-dialog="onCloseAssessmentDialog"
      />
    </v-card>
  </div>
</template>

<script lang="ts">
import Vue from 'vue';
import RemoteServices from '@/services/RemoteServices';
import Assessment from '@/models/assessment/Assessment';
import AssessmentDialog from '@/views/volunteer/AssessmentDialog.vue';

export default Vue.extend({
  name: 'VolunteerAssessmentsView',

  components: {
    AssessmentDialog,
  },

  data() {
    return {
      assessments: [] as Assessment[],
      search: '',
      currentAssessment: null as Assessment | null,
      editAssessmentDialog: false,
      headers: [
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
      ],
    };
  },

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.assessments = await RemoteServices.getVolunteerAssessments();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  },

  methods: {
    editAssessment(this: any, item: Assessment) {
      this.currentAssessment = item;
      this.editAssessmentDialog = true;
    },

    async deleteAssessment(this: any, assessment: Assessment) {
      if (
        assessment.id !== null &&
        confirm('Are you sure you want to delete the assessment?')
      ) {
        try {
          await RemoteServices.deleteAssessment(assessment.id);
        } catch (error) {
          await this.$store.dispatch('error', error);
        }

        const index = this.assessments.findIndex(
          (a: Assessment) => a.id == assessment.id,
        );
        if (index !== -1) {
          this.assessments.splice(index, 1);
        }
      }
    },

    onCloseAssessmentDialog(this: any) {
      this.editAssessmentDialog = false;
      this.currentAssessment = null;
    },

    onSaveAssessment(this: any, assessment: Assessment) {
      const index = this.assessments.findIndex(
        (a: Assessment) => a.id == assessment.id,
      );
      if (index !== -1) {
        const currentAssessment = this.assessments[index];
        currentAssessment.review = assessment.review;
        currentAssessment.reviewDate = assessment.reviewDate;
      }

      this.editAssessmentDialog = false;
      this.currentAssessment = null;
    },
  },
});
</script>

<style lang="scss" scoped></style>
