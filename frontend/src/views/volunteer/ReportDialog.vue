<template>
  <VDialog
    v-model="dialogModel"
    persistent
    max-width="800"
  >
    <VCard>
      <VCardTitle>
        <span class="headline">New Report</span>
      </VCardTitle>
      <VCardText>
        <VForm ref="form" v-model="valid">
          <VRow>
            <VCol cols="12">
              <VTextarea
                label="*Justification"
                :rules="rules"
                required
                v-model="editReport.justification"
                data-cy="justificationInput"
                auto-grow
                rows="1"
              />
            </VCol>
          </VRow>
        </VForm>
      </VCardText>
      <VCardActions>
        <VSpacer />
        <VBtn
          color="blue-darken-1"
          variant="text"
          @click="emit('close-report-dialog')"
        >
          Close
        </VBtn>
        <VBtn
          v-if="canSave"
          color="blue-darken-1"
          variant="text"
          @click="createReport"
          data-cy="saveReport"
        >
          Save
        </VBtn>
      </VCardActions>
    </VCard>
  </VDialog>
</template>

<script lang="ts" setup>
import { ref, watch, computed } from 'vue'
import RemoteServices from '@/services/RemoteServices'
import Report from '@/models/report/Report'

const props = defineProps<{
  dialog: boolean
  report: Report
}>()
const emit = defineEmits(['update:dialog', 'close-report-dialog', 'save-report'])

const dialogModel = ref(props.dialog)
const valid = ref(true)
const editReport = ref(new Report(props.report))

watch(
  () => props.dialog,
  (val) => {
    dialogModel.value = val
    if (val) {
      editReport.value = new Report(props.report)
    }
  }
)

watch(dialogModel, (val) => {
  emit('update:dialog', val)
})

const rules = [
  (v: string) => !!v || 'Justification is required',
  (v: string) => v.length >= 10 || 'Justification must be at least 10 characters',
  (v: string) => v.length <= 256 || 'Justification must be less than or equal to 256 characters',
]

const canSave = computed(() =>
  !!editReport.value.justification &&
  editReport.value.justification.length >= 10 &&
  editReport.value.justification.length <= 256
)

async function createReport() {
  const form = (ref as any).form?.value
  if (editReport.value.activityId !== null && (!form || await form.validate())) {
    try {
      const result = await RemoteServices.createReport(
        editReport.value.activityId,
        editReport.value
      )
      emit('save-report', result)
    } catch (error: any) {
      // Use your store's error handler if available
      // store.setError(error.message)
      console.error(error)
    }
  }
}
</script>

<style scoped lang="scss"></style>
