<template>
  <VCard class="table">
    <VDataTable
      :headers="headers"
      :items="assessments"
      :search="search"
      disable-pagination
      :hide-default-footer="true"
      :mobile-breakpoint="0"
      data-cy="volunteerAssessmentsTable"
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
        </VCardTitle>
      </template>
      <template #item.action="{ item }">
        <VTooltip location="bottom">
          <template #activator="{ props }">
            <VIcon
              class="mr-2 action-button"
              color="blue"
              v-bind="props"
              data-cy="writeAssessmentButton"
              @click="editAssessment(item)"
            >
              mdi-pencil
            </VIcon>
          </template>
          <span>Edit Assessment</span>
        </VTooltip>
        <VTooltip location="bottom">
          <template #activator="{ props }">
            <VIcon
              class="mr-2 action-button"
              color="red"
              v-bind="props"
              data-cy="deleteAssessmentButton"
              @click="deleteAssessment(item)"
            >
              mdi-delete
            </VIcon>
          </template>
          <span>Delete Assessment</span>
        </VTooltip>
      </template>
    </VDataTable>
    <AssessmentDialog
      v-if="currentAssessment && editAssessmentDialog"
      v-model:dialog="editAssessmentDialog"
      :assessment="currentAssessment"
      :is-update="true"
      @save-assessment="onSaveAssessment"
      @close-assessment-dialog="onCloseAssessmentDialog"
    />
  </VCard>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { useMainStore } from '@/store/useMainStore'
import RemoteServices from '@/services/RemoteServices'
import Assessment from '@/models/assessment/Assessment'
import AssessmentDialog from '@/views/volunteer/AssessmentDialog.vue'

const store = useMainStore()

const assessments = ref<Assessment[]>([])
const search = ref('')
const currentAssessment = ref<Assessment | null>(null)
const editAssessmentDialog = ref(false)

const headers = [
  { title: 'Institution', key: 'institutionName', align: 'left', width: '5%' },
  { title: 'Review', key: 'review', align: 'left', width: '5%' },
  { title: 'Review Date', key: 'reviewDate', align: 'left', width: '5%' },
  { title: 'Actions', key: 'action', align: 'left', sortable: false, width: '5%' },
]

onMounted(async () => {
  store.setLoading()
  try {
    assessments.value = await RemoteServices.getVolunteerAssessments()
  } catch (error: any) {
    store.setError(error.message)
  }
  store.clearLoading()
})

function editAssessment(item: Assessment) {
  currentAssessment.value = item
  editAssessmentDialog.value = true
}

async function deleteAssessment(assessment: Assessment) {
  if (
    assessment.id !== null &&
    window.confirm('Are you sure you want to delete the assessment?')
  ) {
    try {
      await RemoteServices.deleteAssessment(assessment.id)
      const index = assessments.value.findIndex((a) => a.id == assessment.id)
      if (index !== -1) {
        assessments.value.splice(index, 1)
      }
    } catch (error: any) {
      store.setError(error.message)
    }
  }
}

function onCloseAssessmentDialog() {
  editAssessmentDialog.value = false
  currentAssessment.value = null
}

function onSaveAssessment(assessment: Assessment) {
  const index = assessments.value.findIndex((a) => a.id == assessment.id)
  if (index !== -1) {
    const current = assessments.value[index]
    current.review = assessment.review
    current.reviewDate = assessment.reviewDate
  }
  editAssessmentDialog.value = false
  currentAssessment.value = null
}
</script>

<style lang="scss" scoped>
.table {
  overflow-x: auto;
}
.action-button {
  cursor: pointer;
}
</style>
