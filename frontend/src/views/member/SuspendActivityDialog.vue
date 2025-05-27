<template>
  <VDialog
    v-model="dialogModel"
    persistent
    max-width="800"
  >
    <VCard>
      <VCardTitle>
        <span class="headline">Suspend Activity</span>
      </VCardTitle>
      <VCardText>
        <VForm ref="form" v-model="valid">
          <VRow>
            <VCol cols="12">
              <VTextarea
                label="*Suspension reason"
                :rules="rules"
                required
                auto-grow
                rows="1"
                v-model="editActivity.suspensionJustification"
                data-cy="suspensionReasonInput"
              />
            </VCol>
          </VRow>
        </VForm>
      </VCardText>
      <VCardActions>
        <VSpacer />
        <VBtn
          color="primary"
          variant="text"
          @click="emit('close-activity-dialog')"
        >
          Close
        </VBtn>
        <VBtn
          v-if="canSuspend"
          color="primary"
          variant="text"
          @click="suspendActivity"
          data-cy="suspendActivity"
        >
          Suspend
        </VBtn>
      </VCardActions>
    </VCard>
  </VDialog>
</template>

<script lang="ts" setup>
import { ref, computed, watch } from 'vue'
import { useMainStore } from '@/store/useMainStore'
import RemoteServices from '@/services/RemoteServices'
import Activity from '@/models/activity/Activity'

const props = defineProps<{
  dialog: boolean
  activity: Activity
}>()
const emit = defineEmits(['update:dialog', 'close-activity-dialog', 'suspend-activity'])

const store = useMainStore()
const dialogModel = ref(props.dialog)
const valid = ref(true)
const editActivity = ref(new Activity(props.activity))

watch(
  () => props.dialog,
  (val) => {
    dialogModel.value = val
    if (val) {
      editActivity.value = new Activity(props.activity)
    }
  }
)

watch(dialogModel, (val) => {
  emit('update:dialog', val)
})

const rules = [
  (v: string) => !!v || 'Suspension reason is required',
  (v: string) => v.length > 10 || 'Suspension reason must be 10 or more characters',
  (v: string) => v.length <= 250 || 'Suspension reason must be less or equal to 250 characters',
]

const canSuspend = computed(() => {
  const justification = editActivity.value.suspensionJustification
  return !!justification && justification.length >= 10 && justification.length <= 250
})

async function suspendActivity() {
  const form = (ref as any).form?.value
  if (editActivity.value.id != null && (!form || await form.validate())) {
    try {
      const result = await RemoteServices.suspendActivity(
        editActivity.value.id,
        editActivity.value.suspensionJustification
      )
      emit('suspend-activity', result)
    } catch (error: any) {
      store.setError(error.message)
    }
  }
}
</script>

<style scoped lang="scss">
.table {
  overflow-x: auto;
}
</style>
