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
        <v-chip v-for="theme in item.themes" v-bind:key="theme.id">
          {{ theme.name }}
        </v-chip>
      </template>
      <template v-slot:[`item.volunteers`]="{ item }">
        <v-chip v-for="volunteer in item.volunteers" v-bind:key="volunteer.id">
          {{ volunteer.name }}
        </v-chip>
      </template>
      <template v-slot:[`item.institution`]="{ item }">
        <v-chip>
          {{ item.institution.name }}
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
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Activity from '@/models/activity/Activity';
import Theme from '@/models/theme/Theme';
import Institution from '@/models/institution/Institution';

@Component
export default class ActivitiesView extends Vue {
  activities: Activity[] = [];
  themes: Theme[] = [];
  institutions: Institution[] = [];
  search: string = '';
  addActivityDialog: boolean = false;
  dialogEditActivity: boolean = false;
  headers: object = [
    {
      text: 'ID',
      value: 'id',
      align: 'left',
      width: '1%',
    },
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
      width: '5%',
    },
    {
      text: 'Volunteers',
      value: 'volunteers',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Institution',
      value: 'institution',
      align: 'left',
      width: '5%',
    },
    {
      text: 'State',
      value: 'state',
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
      this.activities = await RemoteServices.getActivities();
      this.themes = await RemoteServices.getThemes();
      this.institutions = await RemoteServices.getInstitutions();
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
      confirm('Are you sure you want to suspend this activity?')
    ) {
      try {
        await RemoteServices.suspendActivity(activityId);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>

<style lang="scss" scoped></style>
