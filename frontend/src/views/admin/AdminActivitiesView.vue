<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :items="activities"
      :search="search"
      disable-pagination
      :hide-default-footer="true"
      :mobile-breakpoint="0"
      data-cy="adminActivitiesTable"
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
      <template v-slot:[`item.institution`]="{ item }">
        <v-chip>
          {{ item.institution.name }}
        </v-chip>
      </template>
      <template v-slot:[`item.state`]="{ item }">
        <v-chip
          v-if="item.state === 'REPORTED'"
          class="button"
          color="blue"
          data-cy="reportedButton"
          @click="openReportsDialog(item)"
        >
          {{ item.state }}
        </v-chip>
        <v-chip v-else>
          <v-tooltip bottom>
            <template v-slot:activator="{ on }">
              <v-chip v-on="on">{{ item.state }} </v-chip>
            </template>
            <span>Justification: {{ item.suspensionJustification }}</span>
          </v-tooltip>
        </v-chip>
      </template>
      <template v-slot:[`item.action`]="{ item }">
        <v-tooltip
          bottom
          v-if="item.state == 'REPORTED' || item.state == 'SUSPENDED'"
        >
          <template v-slot:activator="{ on }">
            <v-icon
              class="mr-2 action-button"
              color="green"
              v-on="on"
              data-cy="validateButton"
              @click="validateActivity(item)"
              >mdi-check-bold</v-icon
            >
          </template>
          <span>Validate Activity</span>
        </v-tooltip>
        <v-tooltip
          bottom
          v-if="item.state == 'REPORTED' || item.state == 'APPROVED'"
        >
          <template v-slot:activator="{ on }">
            <v-icon
              class="mr-2 action-button"
              color="red"
              v-on="on"
              data-cy="suspendButton"
              @click="suspendActivity(item)"
              >mdi-pause-octagon</v-icon
            >
          </template>
          <span>Suspend Activity</span>
        </v-tooltip>
      </template>
    </v-data-table>
    <reports-dialog
      v-if="listReportsDialog"
      :dialog.sync="listReportsDialog"
      :activity="currentActivity"
      @close-enrollment-dialog="onCloseReportsDialog"
    />
    <suspend-activity-dialog
      v-if="currentActivity && suspendActivityDialog"
      :dialog.sync="suspendActivityDialog"
      :activity="currentActivity"
      :themes="themes"
      @suspend-activity="onSuspendActivity"
      @close-activity-dialog="onCloseSuspendActivityDialog"
    />
  </v-card>
</template>

<script lang="ts">
import Vue from 'vue';
import RemoteServices from '@/services/RemoteServices';
import Activity from '@/models/activity/Activity';
import Theme from '@/models/theme/Theme';
import Institution from '@/models/institution/Institution';
import ListReportsDialog from '@/views/admin/ListReportsDialog.vue';
import SuspendActivityDialog from '@/views/member/SuspendActivityDialog.vue';

export default Vue.extend({
  name: 'AdminActivitiesView',

  components: {
    'reports-dialog': ListReportsDialog,
    'suspend-activity-dialog': SuspendActivityDialog,
  },

  data() {
    return {
      activities: [] as Activity[],
      themes: [] as Theme[],
      institutions: [] as Institution[],
      search: '',
      currentActivity: null as Activity | null,
      listReportsDialog: false,
      suspendActivityDialog: false,
      headers: [
        { text: 'ID', value: 'id', align: 'left', width: '1%' },
        { text: 'Name', value: 'name', align: 'left', width: '5%' },
        { text: 'Region', value: 'region', align: 'left', width: '5%' },
        {
          text: 'Participants',
          value: 'participantsNumberLimit',
          align: 'left',
          width: '5%',
        },
        {
          text: 'Description',
          value: 'description',
          align: 'left',
          width: '10%',
        },
        { text: 'Themes', value: 'themes', align: 'left', width: '5%' },
        { text: 'Themes', value: 'themes', align: 'left', width: '5%' },
        {
          text: 'Institution',
          value: 'institution',
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
          text: 'Application Deadline',
          value: 'formattedApplicationDeadline',
          align: 'left',
          width: '5%',
        },
        {
          text: 'End Date',
          value: 'formattedEndingDate',
          align: 'left',
          width: '5%',
        },
        { text: 'State', value: 'state', align: 'left', width: '5%' },
        {
          text: 'Creation Date',
          value: 'creationDate',
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
      this.themes = await RemoteServices.getThemes();
      this.institutions = await RemoteServices.getInstitutions();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  },

  methods: {
    async validateActivity(this: any, activity: Activity) {
      if (activity.id !== null) {
        try {
          const result = await RemoteServices.validateActivity(activity.id);
          this.activities = this.activities.filter(
            (a: Activity) => a.id !== activity.id,
          );
          this.activities.unshift(result);
        } catch (error) {
          await this.$store.dispatch('error', error);
        }
      }
    },

    suspendActivity(this: any, activity: Activity) {
      this.currentActivity = activity;
      this.suspendActivityDialog = true;
    },

    onCloseSuspendActivityDialog(this: any) {
      this.currentActivity = null;
      this.suspendActivityDialog = false;
    },

    async onSuspendActivity(this: any, activity: Activity) {
      if (activity.id !== null) {
        try {
          this.activities = this.activities.filter(
            (a: Activity) => a.id !== activity.id,
          );
          this.activities.unshift(activity);
          this.currentActivity = null;
          this.suspendActivityDialog = false;
        } catch (error) {
          await this.$store.dispatch('error', error);
        }
      }
    },

    openReportsDialog(this: any, activity: Activity) {
      this.currentActivity = activity;
      this.listReportsDialog = true;
    },

    onCloseReportsDialog(this: any) {
      this.listReportsDialog = false;
      this.currentActivity = null;
    },
  },
});
</script>

<style lang="scss" scoped></style>
