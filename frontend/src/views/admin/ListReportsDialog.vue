<template>
  <v-dialog
    :value="dialog"
    @input="$emit('update:dialog', $event)"
    persistent
    width="800"
  >
    <v-card>
      <v-card-title>
        <span class="headline">
          {{ 'Reports' }}
        </span>
      </v-card-title>
      <v-card-text>
        <v-card class="table">
          <v-data-table
            :headers="headers"
            :items="reports"
            :search="search"
            disable-pagination
            :hide-default-footer="true"
            :mobile-breakpoint="0"
            data-cy="activityReportsTable"
          ></v-data-table>
        </v-card>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn
          color="blue-darken-1"
          variant="text"
          @click="$emit('close-enrollment-dialog')"
          data-cy="closeReportList"
        >
          Close
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import Activity from '@/models/activity/Activity';
import Report from '@/models/report/Report';
import RemoteServices from '@/services/RemoteServices';
import { ISOtoString } from '@/services/ConvertDateService';

export default {
  name: 'ReportDialog',

  props: {
    dialog: {
      type: Boolean,
      required: true,
    },
    activity: {
      type: Object as () => Activity,
      required: true,
    },
  },

  data() {
    return {
      reports: [] as Report[],
      search: '',
      headers: [
        {
          text: 'Volunteer Name',
          value: 'volunteerName',
          align: 'left',
          width: '5%',
        },
        {
          text: 'Justification',
          value: 'justification',
          align: 'left',
          width: '5%',
        },
      ] as object[],
      ISOtoString,
    };
  },

  async created(this: any) {
    await this.$store.dispatch('loading');
    try {
      if (this.activity != null && this.activity.id != null) {
        this.reports = await RemoteServices.getActivityReports(
          this.activity.id,
        );
      }
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  },
};
</script>

<style scoped lang="scss"></style>
