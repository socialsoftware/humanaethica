<template>
  <VDialog
    v-model="dialogModel"
    persistent
    max-width="800"
  >
    <VCard>
      <VCardTitle>
        <span class="headline">
          {{ editParticipation && editParticipation.id === null ? 'Create Participation' : 'Your Rating' }}
        </span>
      </VCardTitle>
      <VCardText>
        <VForm ref="form" v-model="valid">
          <VRow>
            <VCol cols="12" class="d-flex align-center">
              <VTextField
                label="Rating"
                :rules="[isNumberValid]"
                v-model="editParticipation.memberRating"
                data-cy="participantsNumberInput"
              />
            </VCol>
            <VCol cols="12">
              <VTextarea
                label="Review"
                v-model="editParticipation.memberReview"
                :rules="[v => !!v || 'Review is required', v => v.length >= 10 || 'Review must be at least 10 characters', v => v.length < 100 || 'Review must be less than 100 characters']"
                data-cy="participantsReviewInput"
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
          color="primary"
          variant="text"
          @click="emit('close-participation-dialog')"
        >
          Close
        </VBtn>
        <VBtn
          v-if="isReviewValid && isRatingValid"
          color="primary"
          variant="text"
          @click="createUpdateParticipation"
          data-cy="createParticipation"
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
import Participation from '@/models/participation/Participation'

const props = defineProps<{
  dialog: boolean
  participation: Participation
}>()
const emit = defineEmits(['update:dialog', 'close-participation-dialog', 'save-participation'])

const dialogModel = ref(props.dialog)
const form = ref()
const valid = ref(true)
const editParticipation = ref(new Participation(props.participation))

const isReviewValid = computed(() => {
  const review = editParticipation.value.memberReview
  return !!review && review.length >= 10 && review.length < 100
})

const isRatingValid = computed(() => {
  return !!editParticipation.value.memberRating
})

function isNumberValid(value: any) {
  if (value === null || value === undefined || value === '') return true
  if (!/^\d+$/.test(value)) return 'Rating between 1 and 5'
  const parsed = parseInt(value, 10)
  return (parsed >= 1 && parsed <= 5) || 'Rating between 1 and 5'
}

async function createUpdateParticipation() {
  if (!form.value || !(await form.value.validate())) return
  try {
    let result
    if (editParticipation.value.id !== null && editParticipation.value.id !== undefined) {
      result = await RemoteServices.updateParticipationMember(
        editParticipation.value.id,
        editParticipation.value
      )
    } else {
      result = await RemoteServices.createParticipation(
        editParticipation.value.activityId!,
        editParticipation.value
      )
    }
    emit('save-participation', result)
    emit('close-participation-dialog')
  } catch (error: any) {
  }
}

watch(
  () => props.dialog,
  (val) => {
    dialogModel.value = val
    if (val) {
      editParticipation.value = new Participation(props.participation)
    }
  }
)

watch(dialogModel, (val) => {
  emit('update:dialog', val)
})
</script>

<style scoped lang="scss"></style>
