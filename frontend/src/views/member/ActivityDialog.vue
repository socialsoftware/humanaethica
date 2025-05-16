<template>
  <VDialog v-model="internalDialog" persistent width="1300">
    <VCard>
      <VCardTitle>
        <span class="headline">
          {{ editActivity.id === null ? 'New Activity' : 'Edit Activity' }}
        </span>
      </VCardTitle>
      <VCardText>
        <VForm ref="form" v-model="formValid" lazy-validation>
          <VRow>
            <VCol cols="12" sm="6" md="4">
              <VTextField
                label="*Name"
                :rules="[v => !!v || 'Activity name is required']"
                required
                v-model="editActivity.name"
                data-cy="nameInput"
              />
            </VCol>
            <VCol cols="12">
              <VTextField
                label="*Region"
                :rules="[v => !!v || 'Region name is required']"
                required
                v-model="editActivity.region"
                data-cy="regionInput"
              />
            </VCol>
            <VCol cols="12" sm="6" md="4">
              <VTextField
                label="*Number of Participants"
                :rules="[v => isNumberValid(v) || 'Number must be between 1 and 5']"
                required
                v-model="editActivity.participantsNumberLimit"
                data-cy="participantsNumberInput"
              />
            </VCol>
            <VCol cols="12" sm="6">
              <VSelect
                label="Themes"
                v-model="editActivity.themes"
                :items="themes"
                multiple
                return-object
                item-title="completeName"
                item-value="id"
                required
              />
            </VCol>
            <VCol cols="12">
              <VTextField
                label="*Description"
                :rules="[v => !!v || 'Description is required']"
                required
                v-model="editActivity.description"
                data-cy="descriptionInput"
              />
            </VCol>
            <v-col>
              <label class="text-subtitle-2 font-weight-medium">*Application Deadline</label>
              <VTextField
                v-model="editActivity.applicationDeadline"
                type="datetime-local"
                data-cy="applicationDeadline"
                density="comfortable"
                variant="outlined"
                :rules="[v => !!v || 'Required']"
              />
            </v-col>

            <v-col>
              <label class="text-subtitle-2 font-weight-medium">*Starting Date</label>
              <VTextField
                v-model="editActivity.startingDate"
                type="datetime-local"
                data-cy="startingDate"
                density="comfortable"
                variant="outlined"
                :rules="[v => !!v || 'Required']"
              />
            </v-col>

            <v-col>
              <label class="text-subtitle-2 font-weight-medium">*Ending Date</label>
              <VTextField
                v-model="editActivity.endingDate"
                type="datetime-local"
                data-cy="endingDate"
                density="comfortable"
                variant="outlined"
                :rules="[v => !!v || 'Required']"
              />
            </v-col>
          </VRow>
        </VForm>
      </VCardText>
      <VCardActions>
        <VSpacer></VSpacer>
        <VBtn color="blue-darken-1" variant="text" @click="emit('close-activity-dialog')">Close</VBtn>
        <VBtn :disabled="!canSave" color="blue-darken-1" variant="text" @click="updateActivity" data-cy="saveActivity">Save</VBtn>
      </VCardActions>
    </VCard>
  </VDialog>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue';
import Activity from '@/models/activity/Activity';
import Theme from '@/models/theme/Theme';
import RemoteServices from '@/services/RemoteServices';

const props = defineProps<{
  dialog: boolean;
  activity: Activity;
  themes: Theme[];
}>();

const emit = defineEmits<{
  (e: 'close-activity-dialog'): void;
  (e: 'save-activity', result: Activity): void;
  (e: 'update:dialog', val: boolean): void;
}>();

const internalDialog = computed({
  get: () => props.dialog,
  set: (val: boolean) => emit('update:dialog', val),
});

const form = ref();
const formValid = ref(false);
const editActivity = ref(new Activity(props.activity));

watch(() => props.activity, (newVal) => {
  editActivity.value = new Activity(newVal);
});

const canSave = computed(() => {
  const a = editActivity.value;
  return !!a.name && !!a.region && !!a.participantsNumberLimit && !!a.description && !!a.startingDate && !!a.endingDate && !!a.applicationDeadline;
});

function isNumberValid(value: any): boolean {
  const parsed = parseInt(value);
  return !isNaN(parsed) && parsed >= 1 && parsed <= 5;
}

async function updateActivity() {
  const valid = await form.value?.validate();
  if (!valid?.valid) return;

  try {
    const a = editActivity.value;
    const result = a.id !== null ? await RemoteServices.updateActivityAsMember(a.id, a) : await RemoteServices.registerActivity(a);
    emit('save-activity', result);
  } catch (error) {
    console.error(error);
  }
}
</script>

<style scoped lang="scss"></style>
