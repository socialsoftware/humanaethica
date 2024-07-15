<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :items="institution.activities"
      :search="search"
      disable-pagination
      :hide-default-footer="true"
      :mobile-breakpoint="0"
      data-cy="memberActivitiesTable"
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
          <v-btn color="primary" dark @click="newActivity" data-cy="newActivity"
            >New Activity</v-btn
          >
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
            <v-chip v-on="on">{{ item.state }} </v-chip>
          </template>
          <span>Justification: {{ item.suspensionJustification }}</span>
        </v-tooltip>
      </template>
      <template v-slot:[`item.action`]="{ item }">
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              class="mr-2 action-button"
              @click="editActivity(item)"
              v-on="on"
              >edit
            </v-icon>
          </template>
          <span>Edit Activity</span>
        </v-tooltip>
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              class="mr-2 action-button"
              @click="showEnrollments(item)"
              v-on="on"
              data-cy="showEnrollments"
              >fa-solid fa-people-group
            </v-icon>
          </template>
          <span>Show Applications</span>
        </v-tooltip>
        <v-tooltip bottom v-if="item.state != 'SUSPENDED'">
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
    <activity-dialog
      v-if="currentActivity && editActivityDialog"
      v-model="editActivityDialog"
      :activity="currentActivity"
      :themes="themes"
      v-on:save-activity="onSaveActivity"
      v-on:close-activity-dialog="onCloseActivityDialog"
    />
    <suspend-activity-dialog
      v-if="currentActivity && suspendActivityDialog"
      v-model="suspendActivityDialog"
      :activity="currentActivity"
      :themes="themes"
      v-on:suspend-activity="onSuspendActivity"
      v-on:close-activity-dialog="onCloseSuspendActivityDialog"
    />
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Theme from '@/models/theme/Theme';
import Institution from '@/models/institution/Institution';
import Activity from '@/models/activity/Activity';
import ActivityDialog from '@/views/member/ActivityDialog.vue';
import SuspendActivityDialog from '@/views/member/SuspendActivityDialog.vue';

@Component({
  components: {
    'activity-dialog': ActivityDialog,
    'suspend-activity-dialog': SuspendActivityDialog,
  },
})
export default class InstitutionActivitiesView extends Vue {
  institution: Institution = new Institution();
  themes: Theme[] = [];
  search: string = '';

  currentActivity: Activity | null = null;
  editActivityDialog: boolean = false;
  suspendActivityDialog: boolean = false;

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
      text: 'Participants Limit',
      value: 'participantsNumberLimit',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Applications',
      value: 'numberOfEnrollments',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Participations',
      value: 'numberOfParticipations',
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
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      let userId = this.$store.getters.getUser.id;
      this.institution = await RemoteServices.getInstitution(userId);
      this.themes = await RemoteServices.getThemesAvailable();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  newActivity() {
    this.currentActivity = new Activity();
    this.editActivityDialog = true;
  }

  editActivity(activity: Activity) {
    this.currentActivity = activity;
    this.editActivityDialog = true;
  }

  onCloseActivityDialog() {
    this.currentActivity = null;
    this.editActivityDialog = false;
  }

  onSaveActivity(activity: Activity) {
    this.institution.activities = this.institution.activities.filter(
      (a) => a.id !== activity.id,
    );
    this.institution.activities.unshift(activity);
    this.editActivityDialog = false;
    this.currentActivity = null;
  }

  suspendActivity(activity: Activity) {
    this.currentActivity = activity;
    this.suspendActivityDialog = true;
  }

  onCloseSuspendActivityDialog() {
    this.currentActivity = null;
    this.suspendActivityDialog = false;
  }

  async onSuspendActivity(activity: Activity) {
    if (activity.id !== null) {
      this.editActivityDialog = false;
      this.currentActivity = null;

      let userId = this.$store.getters.getUser.id;
      this.institution = await RemoteServices.getInstitution(userId);
      this.themes = await RemoteServices.getThemesAvailable();
    }
  }

  async showEnrollments(activity: Activity) {
    await this.$store.dispatch('setActivity', activity);
    await this.$router.push({ name: 'activity-enrollments' });
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
