<template>
  <VCard class="table">
    <VDataTable
      :headers="headers"
      :items="institution.activities"
      :search="search"
      disable-pagination
      hide-default-footer
      :mobile-breakpoint="0"
      data-cy="memberActivitiesTable"
    >
      <template #top>
        <VCardTitle>
          <VTextField
            v-model="search"
            append-inner-icon="mdi-magnify"
            label="Search"
            class="mx-2"
          />
          <VSpacer />
          <VBtn color="primary" dark @click="newActivity" data-cy="newActivity">
            New Activity
          </VBtn>
        </VCardTitle>
      </template>

      <template #item.themes="{ item }">
        <VChip v-for="theme in item.themes" :key="theme.id">
          {{ theme.completeName }}
        </VChip>
      </template>

      <template #item.state="{ item }">
        <VTooltip location="bottom">
          <template #activator="{ props }">
            <VChip v-bind="props">{{ item.state }}</VChip>
          </template>
          <span>Justification: {{ item.suspensionJustification }}</span>
        </VTooltip>
      </template>

      <template #item.action="{ item }">
        <VTooltip location="bottom">
          <template #activator="{ props }">
            <VIcon class="mr-2 action-button" v-bind="props" @click="editActivity(item)">
              mdi-pencil
            </VIcon>
          </template>
          <span>Edit Activity</span>
        </VTooltip>

        <VTooltip location="bottom">
          <template #activator="{ props }">
            <VIcon
              class="mr-2 action-button"
              v-bind="props"
              @click="showEnrollments(item)"
              data-cy="showEnrollments"
            >
              mdi-account-group
            </VIcon>
          </template>
          <span>Show Applications</span>
        </VTooltip>

        <VTooltip v-if="item.state !== 'SUSPENDED'" location="bottom">
          <template #activator="{ props }">
            <VIcon
              class="mr-2 action-button"
              color="red"
              v-bind="props"
              @click="suspendActivity(item)"
              data-cy="suspendButton"
            >
              mdi-pause-octagon
            </VIcon>
          </template>
          <span>Suspend Activity</span>
        </VTooltip>
      </template>
    </VDataTable>

    <!-- Dialogs -->
    <ActivityDialog
      v-if="currentActivity && editActivityDialog"
      v-model:dialog="editActivityDialog"
      :activity="currentActivity"
      :themes="themes"
      @save-activity="onSaveActivity"
      @close-activity-dialog="onCloseActivityDialog"
    />

    <SuspendActivityDialog
      v-if="currentActivity && suspendActivityDialog"
      v-model:dialog="suspendActivityDialog"
      :activity="currentActivity"
      :themes="themes"
      @suspend-activity="onSuspendActivity"
      @close-activity-dialog="onCloseSuspendActivityDialog"
    />
  </VCard>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useMainStore } from '@/store/useMainStore';
import RemoteServices from '@/services/RemoteServices';
import Activity from '@/models/activity/Activity';
import Theme from '@/models/theme/Theme';
import Institution from '@/models/institution/Institution';
import ActivityDialog from '@/views/member/ActivityDialog.vue';
import SuspendActivityDialog from '@/views/member/SuspendActivityDialog.vue';

const store = useMainStore();
const router = useRouter();

const institution = ref(new Institution());
const themes = ref<Theme[]>([]);
const search = ref('');
const currentActivity = ref<Activity | null>(null);
const editActivityDialog = ref(false);
const suspendActivityDialog = ref(false);

// Vuetify 3 uses 'key' instead of 'value'
const headers = [
  { title: 'Name', key: 'name' },
  { title: 'Region', key: 'region' },
  { title: 'Participants Limit', key: 'participantsNumberLimit' },
  { title: 'Applications', key: 'numberOfEnrollments' },
  { title: 'Participations', key: 'numberOfParticipations' },
  { title: 'Themes', key: 'themes' },
  { title: 'Description', key: 'description' },
  { title: 'State', key: 'state' },
  { title: 'Start Date', key: 'formattedStartingDate' },
  { title: 'End Date', key: 'formattedEndingDate' },
  { title: 'Application Deadline', key: 'formattedApplicationDeadline' },
  { title: 'Creation Date', key: 'creationDate' },
  { title: 'Actions', key: 'action', sortable: false },
];

onMounted(async () => {
  store.setLoading();
  try {
    const userId = store.user?.id;
    if (userId != null) {
      institution.value = await RemoteServices.getInstitution(userId);
      themes.value = await RemoteServices.getThemesAvailable();
    }
  } catch (error) {
    store.setError(error instanceof Error ? error.message : 'Unknown error');
  } finally {
    store.clearLoading();
  }
});

function newActivity() {
  currentActivity.value = new Activity();
  editActivityDialog.value = true;
}

function editActivity(activity: Activity) {
  currentActivity.value = new Activity(activity);
  editActivityDialog.value = true;
}

function onCloseActivityDialog() {
  currentActivity.value = null;
  editActivityDialog.value = false;
}

function onSaveActivity(activity: Activity) {
  institution.value.activities = institution.value.activities.filter(
    (a) => a.id !== activity.id,
  );
  institution.value.activities.unshift(activity);
  onCloseActivityDialog();
}

function suspendActivity(activity: Activity) {
  currentActivity.value = activity;
  suspendActivityDialog.value = true;
}

function onCloseSuspendActivityDialog() {
  currentActivity.value = null;
  suspendActivityDialog.value = false;
}

async function onSuspendActivity() {
  if (store.user?.id != null) {
    institution.value = await RemoteServices.getInstitution(store.user.id);
    themes.value = await RemoteServices.getThemesAvailable();
    onCloseSuspendActivityDialog();
  }
}

async function showEnrollments(activity: Activity) {
  store.setActivity(activity);
  await router.push({ name: 'activity-enrollments' });
}
</script>

<style scoped lang="scss">
.table {
  margin: 2%;
  width: 96%;
  padding: 0 20px;
}

.action-button {
  padding: 8px !important;
  background: #d9d9d9;
  margin: 4px;
  border-radius: 20px;
  border: 1px solid #878787 !important;
  cursor: pointer;
}

.action-button:hover {
  color: white !important;
  background: #878787;
}
</style>
