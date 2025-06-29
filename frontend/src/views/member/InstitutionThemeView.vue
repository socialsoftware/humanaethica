<template>
  <VCard class="table">
    <VDataTable
      :headers="headers"
      :items="themes"
      :search="search"
      disable-pagination
      :hide-default-footer="true"
      :mobile-breakpoint="0"
    >
      <template #top>
        <VCardTitle class="d-flex align-center">
          <VTextField
            v-model="search"
            append-icon="search"
            label="Search"
            class="mx-2"
          />
          <VSpacer />
          <VBtn color="primary" class="ml-2" @click="newTheme" data-cy="createButton">
            New Theme
          </VBtn>
          <VBtn color="primary" class="ml-2" @click="associateThemeDialog" data-cy="associateButton">
            Associate Theme
          </VBtn>
        </VCardTitle>
      </template>
      <template #item.action="{ item }">
        <VTooltip location="bottom">
          <template #activator="{ props }">
            <VIcon
              class="mr-2 action-button"
              color="red"
              v-bind="props"
              data-cy="deleteButton"
              @click="deleteTheme(item)"
              aria-label="Delete Theme"
              role="button"
              tabindex="0"
            >
              mdi-delete
            </VIcon>
          </template>
          <span>Delete Theme</span>
        </VTooltip>
      </template>
    </VDataTable>
    <RegisterTheme
      v-if="addTheme"
      v-model:dialog="addTheme"
      @theme-created="onCreatedTheme"
      @close-dialog="onCloseDialog"
      @theme-error="onThemeError"
    />
    <AssociateTheme
      v-if="associateTheme"
      v-model:dialog="associateTheme"
      @theme-associated="onAssociateTheme"
      @close-dialog="onCloseDialog"
      @associate-error="onAssociateError"
    />
  </VCard>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { useMainStore } from '@/store/useMainStore'
import RemoteServices from '@/services/RemoteServices'
import RegisterTheme from '@/views/member/InstitutionAddTheme.vue'
import AssociateTheme from '@/views/member/InstitutionAssociateThemeView.vue'
import type Theme from '@/models/theme/Theme'

const store = useMainStore()

const themes = ref<Theme[]>([])
const addTheme = ref(false)
const associateTheme = ref(false)
const search = ref('')

const headers = [
  { text: 'Name', value: 'completeName', align: 'left', width: '15%' },
  {
    text: 'Delete',
    value: 'action',
    align: 'left',
    sortable: false,
    width: '5%',
  },
]

const fetchThemes = async () => {
  store.setLoading()
  try {
    themes.value = await RemoteServices.getThemesbyInstitution()
  } catch (error: any) {
    store.setError(error.message)
  }
  store.clearLoading()
}

onMounted(fetchThemes)

const newTheme = () => {
  addTheme.value = true
}

const associateThemeDialog = () => {
  associateTheme.value = true
}

const onAssociateTheme = async () => {
  await fetchThemes()
  associateTheme.value = false
}

const onCreatedTheme = () => {
  addTheme.value = false
  fetchThemes()
}

const onCloseDialog = () => {
  addTheme.value = false
  associateTheme.value = false
}

const deleteTheme = async (theme: Theme) => {
  const themeId = theme.id
  if (
    themeId !== null &&
    window.confirm('Are you sure you want to delete the theme?')
  ) {
    try {
      await RemoteServices.removeThemetoInstitution(themeId)
      await fetchThemes()
    } catch (error: any) {
      store.setError(error.message)
    }
  }
}

function onThemeError(message: string) {
  store.setError(message);
}

function onAssociateError(message: string) {
  store.setError(message)
}
</script>

<style lang="scss" scoped>
.table {
  overflow-x: auto;
  .action-button {
    cursor: pointer;
  }
}
@media (max-width: 600px) {
  .table {
    padding: 0;
    .v-card-title {
      flex-direction: column;
      gap: 8px;
    }
    .v-btn {
      width: 100%;
    }
  }
}
</style>
