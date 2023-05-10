<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :items="activities"
      :search="search"
      disable-pagination
      :hide-default-footer="true"
      :mobile-breakpoint="0"
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
            @click="newActivity"
            data-cy="createButton"
            >New Activity</v-btn
          >
        </v-card-title>
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
    <add-activity-dialog
      v-if="addActivityDialog"
      v-model="addActivityDialog"
      v-on:user-created="onCreatedActivity"
      v-on:close-dialog="onCloseDialog"
    />
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Activity from '@/models/activity/Activity';
import AddActivityDialog from '@/views/admin/activity/AddActivityDialog.vue';

@Component({
  components: {
    'add-activity-dialog': AddActivityDialog,
  },
})
export default class ActivitiesView extends Vue {
  activities: Activity[] = [];
  search: string = '';
  addActivityDialog: boolean = false;
  headers: object = [
    { text: 'Name', value: 'name', align: 'left', width: '5%' },
    {
      text: 'Region',
      value: 'region',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Themes',
      value: 'themes',
      align: 'left',
      width: '10%',
    },
    {
      text: 'State',
      value: 'state',
      align: 'left',
      width: '10%',
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
      this.activities = await RemoteServices.getActivities();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }
  async validateActivity(activity: Activity) {
    let activityId = activity.id;
    if (
      activityId !== null &&
      confirm('Are you sure you want to validate this activity?')
    ) {
      try {
        await RemoteServices.validateActivity(activityId);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
  async suspendActivity(activity: Activity) {
    let activityId = activity.id;
    if (
      activityId !== null &&
      confirm('Are you sure you want to validate this activity?')
    ) {
      try {
        await RemoteServices.suspendActivity(activityId);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
  onCreatedActivity(activity: Activity) {
    this.activities.unshift(activity);
    this.addActivityDialog = false;
  }

  onCloseDialog() {
    this.addActivityDialog = false;
  }
  newActivity() {
    this.addActivityDialog = true;
  }
}
</script>

<style lang="scss" scoped></style>
