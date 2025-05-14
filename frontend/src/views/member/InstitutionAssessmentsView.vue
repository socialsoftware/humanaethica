<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :items="assessments"
      :search="search"
      disable-pagination
      :hide-default-footer="true"
      :mobile-breakpoint="0"
      data-cy="institutionAssessmentsTable"
    >
      <template v-slot:top>
        <v-card-title>
          <v-text-field
            v-model="search"
            append-icon="search"
            label="Search"
            class="mx-2"
          />
        </v-card-title>
      </template>
    </v-data-table>
  </v-card>
</template>

<script lang="ts">
import RemoteServices from '@/services/RemoteServices';
import Institution from '@/models/institution/Institution';
import Assessment from '@/models/assessment/Assessment';

export default {
  name: 'InstitutionAssessmentsView',

  data() {
    return {
      institution: new Institution(),
      assessments: [] as Assessment[],
      search: '',
      headers: [
        {
          text: 'Review',
          value: 'review',
          align: 'left',
          width: '30%',
        },
        {
          text: 'Volunteer',
          value: 'volunteerName',
          align: 'left',
          width: '5%',
        },
        {
          text: 'Review Date',
          value: 'reviewDate',
          align: 'left',
          width: '5%',
        },
      ] as object[],
    };
  },

  async created(this: any) {
    await this.$store.dispatch('loading');
    try {
      const userId = this.$store.getters.getUser.id;
      this.institution = await RemoteServices.getInstitution(userId);
      this.assessments = await RemoteServices.getInstitutionAssessments(
        this.institution.id,
      );
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  },
};
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
