<template>
  <VCard class="table">
    <VDataTable
      :headers="headers"
      :items="institutions"
      :search="search"
      disable-pagination
      :hide-default-footer="true"
      :mobile-breakpoint="0"
    >
      <template #top>
        <VCardTitle>
          <VTextField
            v-model="search"
            append-inner-icon="mdi-magnify"
            label="Search"
            class="mx-2"
          />
        </VCardTitle>
      </template>
      <template #item.themes="{ item }">
        <VChip v-for="theme in item.themes" :key="theme.id">
          {{ theme.name }}
        </VChip>
      </template>
      <template #item.action="{ item }">
        <VTooltip v-if="item.active && !isDemoInstitution(item)" location="bottom">
          <template #activator="{ props }">
            <VIcon
              class="mr-2 action-button"
              color="red"
              v-bind="props"
              data-cy="deleteButton"
              @click="deleteInstitution(item)"
            >
              mdi-delete
            </VIcon>
          </template>
          <span>Delete institution</span>
        </VTooltip>
        <VTooltip v-if="!isDemoInstitution(item)" location="bottom">
          <template #activator="{ props }">
            <VIcon
              class="mr-2 action-button"
              color="black"
              v-bind="props"
              data-cy="documentButton"
              @click="getDocument(item)"
            >
              mdi-file-document
            </VIcon>
          </template>
          <span>See document</span>
        </VTooltip>
        <VTooltip v-if="!item.active && !isDemoInstitution(item)" location="bottom">
          <template #activator="{ props }">
            <VIcon
              class="mr-2 action-button"
              color="green"
              v-bind="props"
              data-cy="validateButton"
              @click="validateInstitution(item)"
            >
              mdi-check-bold
            </VIcon>
          </template>
          <span>Validate institution</span>
        </VTooltip>
      </template>
    </VDataTable>
  </VCard>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { useMainStore } from '@/store/useMainStore'
import RemoteServices from '@/services/RemoteServices'
import Institution from '@/models/institution/Institution'

const store = useMainStore()
const institutions = ref<Institution[]>([])
const search = ref('')

const headers = [
  { title: 'Name', key: 'name', align: 'left', width: '25%' },
  { title: 'Themes', key: 'themes', align: 'left', width: '10%' },
  { title: 'Email', key: 'email', align: 'left', width: '10%' },
  { title: 'NIF', key: 'nif', align: 'left', width: '10%' },
  { title: 'Active', key: 'active', align: 'center', width: '5%' },
  { title: 'Creation Date', key: 'creationDate', align: 'center', width: '10%' },
  { title: 'Actions', key: 'action', align: 'center', sortable: false, width: '5%' },
]

onMounted(async () => {
  store.setLoading()
  try {
    institutions.value = await RemoteServices.getInstitutions()
  } catch (error: any) {
    store.setError(error.message)
  }
  store.clearLoading()
})

async function deleteInstitution(institution: Institution) {
  const institutionId = institution.id
  if (
    institutionId !== null &&
    window.confirm('Are you sure you want to delete the institution?')
  ) {
    try {
      institutions.value = await RemoteServices.deleteInstitution(institutionId)
    } catch (error: any) {
      store.setError(error.message)
    }
  }
}

async function validateInstitution(institution: Institution) {
  const institutionId = institution.id
  if (
    institutionId !== null &&
    window.confirm('Are you sure you want to validate this institution?')
  ) {
    try {
      institutions.value = await RemoteServices.validateInstitution(institutionId)
    } catch (error: any) {
      store.setError(error.message)
    }
  }
}

async function getDocument(institution: Institution) {
  const institutionId = institution.id
  if (institutionId !== null) {
    try {
      await RemoteServices.getInstitutionDocument(institutionId)
    } catch (error: any) {
      store.setError(error.message)
    }
  }
}

function isDemoInstitution(institution: Institution): boolean {
  return institution.name === 'DEMO INSTITUTION'
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
