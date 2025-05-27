<template>
  <VDialog
    v-model="dialogModel"
    persistent
    max-width="800"
  >
    <VCard>
      <VCardTitle>
        <span class="headline">Delete Participation?</span>
      </VCardTitle>
      <VCardText class="text-body-1">
        Are you sure you want to delete this participation?<br />
        WARNING: Deleting this participation will also delete any associated rating.
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
          color="red"
          variant="text"
          data-cy="deleteParticipationDialogButton"
          @click="deleteParticipation"
        >
          Confirm
        </VBtn>
      </VCardActions>
    </VCard>
  </VDialog>
</template>

<script lang="ts" setup>
import { ref, watch } from 'vue'
import RemoteServices from '@/services/RemoteServices'
import Participation from '@/models/participation/Participation'

const props = defineProps<{
  dialog: boolean
  participation: Participation
}>()
const emit = defineEmits(['update:dialog', 'delete-participation', 'close-participation-dialog'])

const dialogModel = ref(props.dialog)
const editParticipation = ref(new Participation(props.participation))

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

async function deleteParticipation() {
  if (editParticipation.value && editParticipation.value.id !== null) {
    try {
      const result = await RemoteServices.deleteParticipation(editParticipation.value.id)
      emit('delete-participation', result)
      emit('close-participation-dialog')
    } catch (error) {
      // Optionally handle error with store.setError(error.message)
    }
  }
}
</script>

<style scoped lang="scss"></style>
