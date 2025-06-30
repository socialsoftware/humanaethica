<template>
  <VCard class="table" data-cy="users">
    <VDataTable
      :headers="headers"
      :items="users"
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
      <template #item.action="{ item }">
        <VTooltip v-if="item.state !== 'DELETED' && !isDemoUser(item)" location="bottom">
          <template #activator="{ props }">
            <VIcon
              class="mr-2 action-button"
              color="red"
              v-bind="props"
              data-cy="deleteButton"
              @click="deleteUser(item)"
              aria-label="Delete user"
              role="button"
              tabindex="0"
            >
              mdi-delete
            </VIcon>
          </template>
          <span>Delete user</span>
        </VTooltip>
        <VTooltip v-if="item.hasDocument" location="bottom">
          <template #activator="{ props }">
            <VIcon
              class="mr-2 action-button"
              color="black"
              v-bind="props"
              data-cy="documentButton"
              @click="getDocument(item)"
              aria-label="See document"
              role="button"
              tabindex="0"
            >
              mdi-file-document
            </VIcon>
          </template>
          <span>See document</span>
        </VTooltip>
        <VTooltip
          v-if="item.state === 'SUBMITTED' || (item.state === 'DELETED' && !isDemoUser(item))"
          location="bottom"
        >
          <template #activator="{ props }">
            <VIcon
              class="mr-2 action-button"
              color="green"
              v-bind="props"
              data-cy="validateButton"
              @click="validateUser(item)"
              aria-label="Validate user"
              role="button"
              tabindex="0"
            >
              mdi-check-bold
            </VIcon>
          </template>
          <span>Validate user</span>
        </VTooltip>
      </template>
    </VDataTable>
  </VCard>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { useMainStore } from '@/store/useMainStore'
import RemoteServices from '@/services/RemoteServices'
import User from '@/models/user/User'

const store = useMainStore()

const users = ref<User[]>([])
const search = ref('')

const headers = [
  { title: 'Username', key: 'username', align: 'left', width: '15%' },
  { title: 'Name', key: 'name', align: 'left', width: '15%' },
  { title: 'Email', key: 'email', align: 'left', width: '10%' },
  { title: 'Role', key: 'role', align: 'left', width: '5%' },
  { title: 'Institution', key: 'institutionName', align: 'left', width: '15%' },
  { title: 'Active', key: 'active', align: 'left', width: '15%' },
  { title: 'State', key: 'state', align: 'left', width: '10%' },
  { title: 'Type', key: 'type', align: 'left', width: '10%' },
  { title: 'Creation Date', key: 'creationDate', align: 'left', width: '10%' },
  { title: 'Last Access', key: 'lastAccess', align: 'left', width: '30%' },
  { title: 'Actions', key: 'action', align: 'left', sortable: false, width: '5%' },
]

onMounted(async () => {
  store.setLoading()
  try {
    users.value = await RemoteServices.getUsers()
    users.value.forEach((user: User) => {
      if (user.institutionName == null) {
        user.institutionName = 'None'
      }
    })
  } catch (error: any) {
    store.setError(error.message)
  }
  store.clearLoading()
})

async function deleteUser(user: User) {
  const userId = user.id
  if (
    userId !== null &&
    window.confirm('Are you sure you want to delete the user?')
  ) {
    try {
      users.value = await RemoteServices.deleteUser(userId)
    } catch (error: any) {
      store.setError(error.message)
    }
  }
}

async function getDocument(user: User) {
  const userId = user.id
  if (userId !== null) {
    try {
      await RemoteServices.getUserDocument(userId)
    } catch (error: any) {
      store.setError(error.message)
    }
  }
}

async function validateUser(user: User) {
  const userId = user.id
  if (
    userId !== null &&
    window.confirm('Are you sure you want to validate this user?')
  ) {
    try {
      await RemoteServices.validateUser(userId)
      users.value = await RemoteServices.getUsers()
    } catch (error: any) {
      store.setError(error.message)
    }
  }
}

function isDemoUser(user: User): boolean {
  return (
    user.username === 'ars' ||
    user.username === 'demo-member' ||
    user.username === 'demo-volunteer'
  )
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
