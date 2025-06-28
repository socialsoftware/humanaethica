<template>
  <VDialog v-model="dialogModel" persistent width="800">
    <VCard>
      <VCardTitle>
        <span class="headline">
          {{ isNewEnrollment ? 'New Application' : 'Edit Application' }}
        </span>
      </VCardTitle>

      <VCardText>
        <VForm ref="formRef" lazy-validation>
          <VRow>
            <VCol cols="12">
              <VTextarea
                label="*Motivation"
                :rules="rules"
                required
                v-model="editEnrollment.motivation"
                data-cy="motivationInput"
                auto-grow
                rows="1"
              />
            </VCol>
          </VRow>
        </VForm>
      </VCardText>

      <VCardActions>
        <VSpacer />
        <VBtn color="primary" @click="closeDialog">Close</VBtn>
        <VBtn
          v-if="canSave"
          color="primary"
          @click="submitEnrollment"
          data-cy="saveEnrollment"
        >
          Save
        </VBtn>
      </VCardActions>
    </VCard>
  </VDialog>
</template>

<script lang="ts" setup>
import { ref, computed, watch } from 'vue';
import { useMainStore } from '@/store/useMainStore';
import RemoteServices from '@/services/RemoteServices';
import Enrollment from '@/models/enrollment/Enrollment';

const props = defineProps<{
  dialog: boolean;
  enrollment: Enrollment;
}>();

const emit = defineEmits<{
  (e: 'update:dialog', value: boolean): void;
  (e: 'close-enrollment-dialog'): void;
  (e: 'save-enrollment', value: Enrollment): void;
  (e: 'update-enrollment', value: Enrollment): void;
}>();

const store = useMainStore();

const dialogModel = ref(props.dialog);
watch(() => props.dialog, (val) => (dialogModel.value = val));
watch(dialogModel, (val) => emit('update:dialog', val));

const rules = [
  (v: string) => !!v || 'Motivation is required',
  (v: string) => v.length >= 10 || 'Motivation must be at least 10 characters',
]

const editEnrollment = ref(new Enrollment(props.enrollment));
const formRef = ref();

const isNewEnrollment = computed(() => editEnrollment.value.id === null);

const canSave = computed(() => {
  const motivation = editEnrollment.value.motivation;
  return !!motivation && motivation.length >= 10;
});

const closeDialog = () => {
  emit('close-enrollment-dialog');
};

const submitEnrollment = async () => {
  const form = formRef.value;
  if (!form || !(await form.validate())) return;

  const enrollment = editEnrollment.value;

  try {
    if (enrollment.id !== null) {
      const result = await RemoteServices.editEnrollment(enrollment.id, enrollment);
      emit('update-enrollment', result);
    } else if (enrollment.activityId !== null) {
      const result = await RemoteServices.createEnrollment(enrollment.activityId, enrollment);
      emit('save-enrollment', result);
    }
  } catch (error) {
    store.triggerError(error);
  }
};
</script>

<style scoped lang="scss">
/* Optional: Add responsive or dialog-specific tweaks here */
</style>
