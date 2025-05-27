<template>
  <VCard class="table">
    <VDataTable
      :headers="headers"
      :items="assessments"
      :search="search"
      disable-pagination
      :hide-default-footer="true"
      :mobile-breakpoint="0"
      data-cy="institutionAssessmentsTable"
    >
      <template #top>
        <VCardTitle>
          <VTextField
            v-model="search"
            append-icon="search"
            label="Search"
            class="mx-2"
          />
        </VCardTitle>
      </template>
    </VDataTable>
  </VCard>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { useMainStore } from '@/store/useMainStore'
import RemoteServices from '@/services/RemoteServices'
import Institution from '@/models/institution/Institution'
import Assessment from '@/models/assessment/Assessment'

const store = useMainStore()

const institution = ref(new Institution())
const assessments = ref<Assessment[]>([])
const search = ref('')

const headers = [
  { title: 'Review', key: 'review', align: 'left', width: '30%' },
  { title: 'Volunteer', key: 'volunteerName', align: 'left', width: '5%' },
  { title: 'Review Date', key: 'reviewDate', align: 'left', width: '5%' },
]

onMounted(async () => {
  store.setLoading()
  try {
    const userId = store.user.id
    institution.value = await RemoteServices.getInstitution(userId)
    assessments.value = await RemoteServices.getInstitutionAssessments(institution.value.id)
  } catch (error: any) {
    store.setError(error.message)
  }
  store.clearLoading()
})
</script>

<style lang="scss" scoped>
.table {
  overflow-x: auto;
}
</style>
