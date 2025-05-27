<template>
  <VDialog
    v-model="dialogModel"
    persistent
    max-width="800"
  >
    <VCard>
      <VCardTitle>
        <span class="headline">
          {{ editAssessment && editAssessment.id === null ? 'New Assessment' : 'Edit Assessment' }}
        </span>
      </VCardTitle>
      <VCardText>
        <VForm ref="form" v-model="valid">
          <VRow>
            <VCol cols="12">
              <VTextField
                label="*Review"
                :rules="rules"
                required
                v-model="editAssessment.review"
                data-cy="reviewInput"
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
          @click="emit('close-assessment-dialog')"
        >
          Close
        </VBtn>
        <VBtn
          v-if="canSave"
          color="primary"
          variant="text"
          @click="updateAssessment"
          data-cy="saveAssessment"
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
import Assessment from '@/models/assessment/Assessment'

const props = defineProps<{
  dialog: boolean
  assessment: Assessment
  isUpdate: boolean
}>()
const emit = defineEmits(['update:dialog', 'close-assessment-dialog', 'save-assessment'])

const dialogModel = ref(props.dialog)
const valid = ref(true)
const editAssessment = ref(new Assessment(props.assessment))

watch(
  () => props.dialog,
  (val) => {
    dialogModel.value = val
    if (val) {
      editAssessment.value = new Assessment(props.assessment)
    }
  }
)

watch(dialogModel, (val) => {
  emit('update:dialog', val)
})

const rules = [
  (v: string) => !!v || 'Review is required',
  (v: string) => v.length >= 10 || 'Review must be at least 10 characters',
]

const canSave = computed(() => !!editAssessment.value.review && editAssessment.value.review.length >= 10)

async function updateAssessment() {
  const form = (ref as any).form?.value
  if (editAssessment.value.institutionId !== null && (!form || await form.validate())) {
    try {
      let result
      if (props.isUpdate) {
        result = await RemoteServices.updateAssessment(editAssessment.value)
      } else {
        result = await RemoteServices.createAssessment(
          editAssessment.value.institutionId,
          editAssessment.value
        )
      }
      emit('save-assessment', result)
    } catch (error: any) {
      console.error(error)
    }
  }
}
</script>

<style scoped lang="scss"></style>
