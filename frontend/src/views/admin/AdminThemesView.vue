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
          <VBtn
            color="primary"
            class="ml-2"
            @click="openTreeView"
            data-cy="createButton"
          >
            Tree
          </VBtn>
          <VBtn
            color="primary"
            class="ml-2"
            @click="openNewTheme"
            data-cy="createButton"
          >
            New Theme
          </VBtn>
        </VCardTitle>
      </template>
      <template #item.institutions="{ item }">
        <VChip v-for="institution in item.institutions" :key="institution.name">
          {{ institution.name }}
        </VChip>
      </template>
      <template #item.parentTheme="{ item }">
        <VChip v-if="item.parentTheme && item.parentTheme.completeName">
          {{ item.parentTheme.completeName }}
        </VChip>
      </template>
      <template #item.state="{ item }">
        <span :class="getStateClass(item.state)">{{ item.state }}</span>
      </template>
      <template #item.action="{ item }">
        <VTooltip v-if="item.state === 'SUBMITTED' || item.state === 'APPROVED'" location="bottom">
          <template #activator="{ props }">
            <VIcon
              class="mr-2 action-button"
              color="red"
              v-bind="props"
              data-cy="deleteButton"
              @click="deleteTheme(item)"
            >mdi-delete</VIcon>
          </template>
          <span>Delete Theme</span>
        </VTooltip>
        <VTooltip v-if="item.state === 'SUBMITTED' || item.state === 'DELETED'" location="bottom">
          <template #activator="{ props }">
            <VIcon
              class="mr-2 action-button"
              color="green"
              v-bind="props"
              data-cy="validateButton"
              @click="validateTheme(item)"
            >mdi-check-bold</VIcon>
          </template>
          <span>Validate Theme</span>
        </VTooltip>
      </template>
    </VDataTable>
    <AdminAddTheme
      v-if="addTheme"
      v-model:dialog="addTheme"
      @theme-created="onCreatedTheme"
      @close-dialog="onCloseDialog"
      @theme-error="onThemeError"
    />
    <ThemesTreeView
      v-if="treeView"
      v-model:dialog="treeView"
      @close-dialog="onCloseDialog"
      @tree-error="onTreeError"
    />
  </VCard>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { useMainStore } from '@/store/useMainStore'
import RemoteServices from '@/services/RemoteServices'
import AdminAddTheme from '@/views/admin/AdminAddTheme.vue'
import ThemesTreeView from '@/views/admin/ThemesTreeView.vue'
import Theme from '@/models/theme/Theme'

const store = useMainStore()

const themes = ref<Theme[]>([])
const addTheme = ref(false)
const treeView = ref(false)
const search = ref('')

const headers = [
  { title: 'Name', key: 'completeName', align: 'left', width: '5%' },
  { title: 'Parent', key: 'parentTheme', align: 'left', width: '10%' },
  { title: 'Institutions', key: 'institutions', align: 'left', width: '10%' },
  { title: 'State', key: 'state', align: 'left', width: '10%' },
  { title: 'Actions', key: 'action', align: 'left', sortable: false, width: '5%' },
]

onMounted(async () => {
  store.setLoading()
  try {
    themes.value = await RemoteServices.getThemes()
  } catch (error: any) {
    store.setError(error.message)
  }
  store.clearLoading()
})

function openNewTheme() {
  addTheme.value = true
}

function openTreeView() {
  treeView.value = true
}

async function onCreatedTheme() {
  themes.value = await RemoteServices.getThemes()
  addTheme.value = false
}

function onCloseDialog() {
  addTheme.value = false
  treeView.value = false
}

async function deleteTheme(theme: Theme) {
  const themeId = theme.id
  if (
    themeId !== null &&
    window.confirm('Are you sure you want to delete the theme?')
  ) {
    try {
      themes.value = await RemoteServices.deleteTheme(themeId)
    } catch (error: any) {
      store.setError(error.message)
    }
  }
}

async function validateTheme(theme: Theme) {
  const themeId = theme.id
  if (
    themeId !== null &&
    window.confirm('Are you sure you want to validate this theme?')
  ) {
    try {
      await RemoteServices.validateTheme(themeId)
      themes.value = await RemoteServices.getThemes()
    } catch (error: any) {
      store.setError(error.message)
    }
  }
}

function getStateClass(state: string): string {
  switch (state) {
    case 'SUBMITTED':
      return 'orange--text'
    case 'APPROVED':
      return 'green--text'
    case 'DELETED':
      return 'red--text'
    default:
      return ''
  }
}

function onThemeError(message: string) {
  store.setError(message);
}

function onTreeError(message: string) {
  store.setError(message)
}
</script>

<style lang="scss" scoped>
.orange--text,
.green--text,
.red--text {
  font-weight: bold;
}
.orange--text {
  color: orange;
}
.green--text {
  color: green;
}
.red--text {
  color: red;
}
.table {
  overflow-x: auto;
}
.action-button {
  cursor: pointer;
}
</style>
