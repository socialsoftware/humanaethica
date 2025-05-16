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
        <v-chip v-for="theme in item.themes" :key="theme.id">
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
          <v-tooltip location="bottom">
            <template v-slot:activator="{ props }">
              <v-chip v-bind="props">{{ item.state }}</v-chip>
            </template>
            <span>Justification: {{ item.suspensionJustification }}</span>
          </v-tooltip>
        </v-chip>
      </template>
      <template v-slot:[`item.action`]="{ item }">
        <v-tooltip location="bottom" v-if="item.state === 'REPORTED' || item.state === 'SUSPENDED'">
          <template v-slot:activator="{ props }">
            <v-icon
              class="mr-2 action-button"
              color="green"
              v-bind="props"
              data-cy="validateButton"
              @click="validateActivity(item)"
            >mdi-check-bold</v-icon>
          </template>
          <span>Validate Activity</span>
        </v-tooltip>
        <v-tooltip location="bottom" v-if="item.state === 'REPORTED' || item.state === 'APPROVED'">
          <template v-slot:activator="{ props }">
            <v-icon
              class="mr-2 action-button"
              color="red"
              v-bind="props"
              data-cy="suspendButton"
              @click="suspendActivity(item)"
            >mdi-pause-octagon</v-icon>
          </template>
          <span>Suspend Activity</span>
        </v-tooltip>
      </template>
    </v-data-table>
    <reports-dialog
      v-if="listReportsDialog"
      v-model:dialog="listReportsDialog"
      :activity="currentActivity"
      @close-enrollment-dialog="onCloseReportsDialog"
    />
    <suspend-activity-dialog
      v-if="currentActivity && suspendActivityDialog"
      v-model:dialog="suspendActivityDialog"
      :activity="currentActivity"
      :themes="themes"
      @suspend-activity="onSuspendActivity"
      @close-activity-dialog="onCloseSuspendActivityDialog"
    />
  </v-card>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import type { DataTableHeader } from 'vuetify';
import RemoteServices from '@/services/RemoteServices';
import Activity from '@/models/activity/Activity';
import Theme from '@/models/theme/Theme';
import Institution from '@/models/institution/Institution';
import ListReportsDialog from '@/views/admin/ListReportsDialog.vue';
import SuspendActivityDialog from '@/views/member/SuspendActivityDialog.vue';
import { useMainStore } from '@/store/useMainStore';

const store = useMainStore();

const activities = ref<Activity[]>([]);
const themes = ref<Theme[]>([]);
const institutions = ref<Institution[]>([]);
const search = ref('');
const currentActivity = ref<Activity | null>(null);
const listReportsDialog = ref(false);
const suspendActivityDialog = ref(false);

const headers = ref<DataTableHeader[]>([
  { title: 'ID', key: 'id', align: 'start', width: '1%' },
  { title: 'Name', key: 'name', align: 'start', width: '5%' },
  { title: 'Region', key: 'region', align: 'start', width: '5%' },
  { title: 'Participants', key: 'participantsNumberLimit', align: 'start', width: '5%' },
  { title: 'Description', key: 'description', align: 'start', width: '10%' },
  { title: 'Themes', key: 'themes', align: 'start', width: '5%' },
  { title: 'Institution', key: 'institution', align: 'start', width: '5%' },
  { title: 'Start Date', key: 'formattedStartingDate', align: 'start', width: '5%' },
  { title: 'Application Deadline', key: 'formattedApplicationDeadline', align: 'start', width: '5%' },
  { title: 'End Date', key: 'formattedEndingDate', align: 'start', width: '5%' },
  { title: 'State', key: 'state', align: 'start', width: '5%' },
  { title: 'Creation Date', key: 'creationDate', align: 'start', width: '5%' },
  { title: 'Actions', key: 'action', align: 'start', sortable: false, width: '5%' },
]);

onMounted(async () => {
  store.setLoading();
  try {
    activities.value = await RemoteServices.getActivities();
    themes.value = await RemoteServices.getThemes();
    institutions.value = await RemoteServices.getInstitutions();
  } catch (error: any) {
    store.setError(error.message);
  }
  store.clearLoading();
});

async function validateActivity(activity: Activity) {
  if (activity.id !== null) {
    try {
      const result = await RemoteServices.validateActivity(activity.id);
      activities.value = activities.value.filter((a) => a.id !== activity.id);
      activities.value.unshift(result);
    } catch (error: any) {
      store.setError(error.message);
    }
  }
}

function suspendActivity(activity: Activity) {
  currentActivity.value = activity;
  suspendActivityDialog.value = true;
}

function onCloseSuspendActivityDialog() {
  currentActivity.value = null;
  suspendActivityDialog.value = false;
}

async function onSuspendActivity(activity: Activity) {
  if (activity.id !== null) {
    try {
      activities.value = activities.value.filter((a) => a.id !== activity.id);
      activities.value.unshift(activity);
      currentActivity.value = null;
      suspendActivityDialog.value = false;
    } catch (error: any) {
      store.setError(error.message);
    }
  }
}

function openReportsDialog(activity: Activity) {
  currentActivity.value = activity;
  listReportsDialog.value = true;
}

function onCloseReportsDialog() {
  listReportsDialog.value = false;
  currentActivity.value = null;
}
</script>

<style lang="scss" scoped></style>
