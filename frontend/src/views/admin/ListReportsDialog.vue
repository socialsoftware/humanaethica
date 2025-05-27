<template>
  <VDialog
    v-model="dialogModel"
    persistent
    max-width="800"
  >
    <VCard>
      <VCardTitle>
        <span class="headline">Reports</span>
      </VCardTitle>
      <VCardText>
        <VCard class="table">
          <VDataTable
            :headers="headers"
            :items="reports"
            :search="search"
            disable-pagination
            :hide-default-footer="true"
            :mobile-breakpoint="0"
            data-cy="activityReportsTable"
          />
        </VCard>
      </VCardText>
      <VCardActions>
        <VSpacer />
        <VBtn
          color="blue-darken-1"
          variant="text"
          @click="emit('close-enrollment-dialog')"
          data-cy="closeReportList"
        >
          Close
        </VBtn>
      </VCardActions>
    </VCard>
  </VDialog>
</template>

<script lang="ts" setup>
import { ref, watch, onMounted } from 'vue'
import Activity from '@/models/activity/Activity'
import Report from '@/models/report/Report'
import RemoteServices from '@/services/RemoteServices'
import { useMainStore } from '@/store/useMainStore'

const props = defineProps<{
  dialog: boolean
  activity: Activity
}>()
const emit = defineEmits(['update:dialog', 'close-enrollment-dialog'])

const store = useMainStore()
const dialogModel = ref(props.dialog)
const reports = ref<Report[]>([])
const search = ref('')

const headers = [
  { title: 'Volunteer Name', key: 'volunteerName', align: 'left', width: '5%' },
  { title: 'Justification', key: 'justification', align: 'left', width: '5%' }
]

watch(
  () => props.dialog,
  (val) => {
    dialogModel.value = val
    if (val) fetchReports()
  }
)

watch(dialogModel, (val) => {
  emit('update:dialog', val)
})

async function fetchReports() {
  store.setLoading()
  try {
    if (props.activity && props.activity.id != null) {
      reports.value = await RemoteServices.getActivityReports(props.activity.id)
    }
  } catch (error: any) {
    store.setError(error.message)
  }
  store.clearLoading()
}

onMounted(fetchReports)
</script>

<style scoped lang="scss">
.table {
  overflow-x: auto;
}
</style>
