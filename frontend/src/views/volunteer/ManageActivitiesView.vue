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
        </v-card-title>
      </template>
      <template v-slot:[`item.themes`]="{ item }">
        <v-chip v-for="theme in item.themes">
          {{ theme.name }}
        </v-chip>
      </template>
      <template v-slot:[`item.action`]="{ item }">
        <v-tooltip v-if="!item.alreadyJoined" bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              class="mr-2 action-button"
              color="green"
              v-on="on"
              data-cy="joinButton"
              @click="joinActivity(item)"
              >mdi-check-bold</v-icon
            >
          </template>
          <span>Join Activity</span>
        </v-tooltip>
        <v-tooltip v-if="item.alreadyJoined" bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              class="mr-2 action-button"
              color="red"
              v-on="on"
              data-cy="joinButton"
              @click="leaveActivity(item)"
              >mdi-export</v-icon
            >
          </template>
          <span>Leave Activity</span>
        </v-tooltip>
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              class="mr-2 action-button"
              color="red"
              v-on="on"
              data-cy="joinButton"
              @click="reportActivity(item)"
              >warning</v-icon
            >
          </template>
          <span>Report Activity</span>
        </v-tooltip>
      </template>
    </v-data-table>
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Activity from '@/models/activity/Activity';

@Component({
  components: {},
})
export default class ManageActivitiesView extends Vue {
  activities: Activity[] = [];
  ownActivities: Activity[] = [];
  search: string = '';
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
      text: 'Themes',
      value: 'themes',
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
      this.ownActivities = await RemoteServices.getOwnActivities(
        this.$store.getters.getUser.id
      );
      /*
      for (i; i < this.activities.length; i++) {
        for (j; j < this.ownActivities.length; j++)
          if (this.ownActivities[j].id == this.activities[i].id) {
            this.activities[i].alreadyJoined = true;
          }
      }
      */
      this.activities.forEach((activity) => {
        if (this.ownActivities.some((ownActivity) => ownActivity.id === activity.id)) {
          activity.alreadyJoined = true;
        }
      });
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async joinActivity(activity: Activity) {
    let activityId = activity.id;
    if (
      activityId !== null &&
      confirm('Are you sure you want to join this activity?')
    ) {
      try {
        await RemoteServices.joinActivity(
          this.$store.getters.getUser.id,
          activityId
        );
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
  async leaveActivity(activity: Activity) {
    let activityId = activity.id;
    if (
      activityId !== null &&
      confirm('Are you sure you want to leave this activity?')
    ) {
      try {
        await RemoteServices.leaveActivity(
          this.$store.getters.getUser.id,
          activityId
        );
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
  async reportActivity(activity: Activity) {
    let activityId = activity.id;
    if (
      activityId !== null &&
      confirm('Are you sure you want to report this activity?')
    ) {
      try {
        await RemoteServices.reportActivity(
          this.$store.getters.getUser.id,
          activityId
        );
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>

<style lang="scss" scoped></style>
