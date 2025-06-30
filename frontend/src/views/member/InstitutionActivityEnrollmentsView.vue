<template>
  <VCard class="table">
    <div class="text-h3">{{ activity.name }}</div>
    <VDataTable
      :headers="headers"
      :items="enrollments"
      :search="search"
      disable-pagination
      :hide-default-footer="true"
      :mobile-breakpoint="0"
      data-cy="activityEnrollmentsTable"
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
          <VBtn
            color="primary"
            @click="getActivities"
            data-cy="getActivities"
          >
            Activities
          </VBtn>
        </VCardTitle>
      </template>
      <template #item.action="{ item }">
        <VTooltip v-if="canParticipate(item) && checkIfEnrollmentPeriodIsOver()" location="bottom">
          <template #activator="{ props }">
            <VIcon
              class="mr-2 action-button"
              v-bind="props"
              @click="selectParticipant(item)"
              data-cy="selectParticipantButton"
            >
              mdi-check
            </VIcon>
          </template>
          <span>Select Participant</span>
        </VTooltip>
        <VTooltip
          v-if="isParticipating(item) && checkIfActivityHasEnded() && !volunteerReviewWritten(item)"
          location="bottom"
        >
          <template #activator="{ props }">
            <VIcon
              class="mr-2 action-button"
              color="blue"
              v-bind="props"
              @click="selectParticipant(item)"
              data-cy="editParticipantButton"
            >
              mdi-pencil
            </VIcon>
          </template>
          <span>Give Rating</span>
        </VTooltip>
        <VTooltip
          v-if="isParticipating(item)"
          location="bottom"
          :key="`delete-${item.volunteerId}-${item.participating}`"
        >
          <template #activator="{ props }">
            <VIcon
              class="action-button"
              color="red"
              v-bind="props"
              @click="deleteParticipation(item)"
              data-cy="deleteParticipantButton"
            >
              mdi-delete
            </VIcon>
          </template>
          <span>Delete Participant</span>
        </VTooltip>
      </template>
      <template #item.memberReview="{ item }">
        <VTooltip location="bottom">
          <template #activator="{ props }">
            <span class="review-text" v-bind="props">{{ getMemberReview(item) }}</span>
          </template>
          <span>{{ getMemberReview(item) }}</span>
        </VTooltip>
      </template>
      <template #item.volunteerReview="{ item }">
        <VTooltip location="bottom">
          <template #activator="{ props }">
            <span class="review-text" v-bind="props">{{ getVolunteerReview(item) }}</span>
          </template>
          <span>{{ getVolunteerReview(item) }}</span>
        </VTooltip>
      </template>
    </VDataTable>
    <ParticipationSelectionDialog
      v-if="currentParticipation && editParticipationSelectionDialog"
      v-model:dialog="editParticipationSelectionDialog"
      :participation="currentParticipation"
      @save-participation="onSaveParticipation"
      @close-participation-dialog="onCloseParticipationDialog"
    />
    <ParticipationDeletionDialog
      v-if="currentParticipation"
      v-model:dialog="editParticipationDeletionDialog"
      :participation="currentParticipation"
      @delete-participation="onDeleteParticipation"
      @close-participation-dialog="onCloseParticipationDialog"
    />
  </VCard>
</template>

<script lang="ts" setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useMainStore } from '@/store/useMainStore'
import RemoteServices from '@/services/RemoteServices'
import Activity from '@/models/activity/Activity'
import Enrollment from '@/models/enrollment/Enrollment'
import Participation from '@/models/participation/Participation'
import ParticipationSelectionDialog from '@/views/member/ParticipationSelectionDialog.vue'
import ParticipationDeletionDialog from '@/views/member/ParticipationDeletionDialog.vue'

const store = useMainStore()
const router = useRouter()

const activity = ref<Activity>(store.activity)
const enrollments = ref<Enrollment[]>([])
const participations = ref<Participation[]>([])
const search = ref('')
const currentParticipation = ref<Participation | null>(null)
const editParticipationSelectionDialog = ref(false)
const editParticipationDeletionDialog = ref(false)

const headers = [
  { title: 'Name', key: 'volunteerName', align: 'left', width: '30%' },
  { title: 'Motivation', key: 'motivation', align: 'left', width: '30%' },
  { title: 'Member Rating', key: 'memberReview', align: 'left', width: '20%' },
  { title: 'Volunteer Rating', key: 'volunteerReview', align: 'left', width: '20%' },
  { title: 'Participating', key: 'participating', align: 'left', width: '10%' },
  { title: 'Application Date', key: 'enrollmentDateTime', align: 'left', width: '5%' },
  { title: 'Actions', key: 'action', align: 'left', sortable: false, width: '5%' },
]

onMounted(async () => {
  if (activity.value?.id) {
    store.setLoading()
    try {
      enrollments.value = await RemoteServices.getActivityEnrollments(activity.value.id)
      participations.value = await RemoteServices.getActivityParticipations(activity.value.id)
    } catch (error: any) {
      store.setError(error.message)
    }
    store.clearLoading()
  }
})

async function getActivities() {
  store.setActivity(null)
  router.push({ name: 'institution-activities' }).catch(() => {})
}

async function selectParticipant(enrollment: Enrollment) {
  const existing = participations.value.find(
    (p) =>
      p.activityId === enrollment.activityId &&
      p.volunteerId === enrollment.volunteerId
  )
  if (existing) {
    currentParticipation.value = { ...existing }
    editParticipationSelectionDialog.value = true
  } else if (checkIfActivityHasEnded()) {
    const newPart = new Participation()
    newPart.activityId = enrollment.activityId
    newPart.volunteerId = enrollment.volunteerId
    currentParticipation.value = newPart
    editParticipationSelectionDialog.value = true
  } else {
    await createParticipation(enrollment.activityId!, enrollment.volunteerId!)
  }
}

async function createParticipation(activityId: number, volunteerId: number) {
  const newParticipation = new Participation()
  newParticipation.activityId = activityId
  newParticipation.volunteerId = volunteerId
  await RemoteServices.createParticipation(activityId, newParticipation)
  currentParticipation.value = null
  enrollments.value = await RemoteServices.getActivityEnrollments(activityId)
  participations.value = await RemoteServices.getActivityParticipations(activityId)
}

function checkIfActivityHasEnded() {
  return new Date(activity.value.endingDate) < new Date()
}

function checkIfEnrollmentPeriodIsOver() {
  return new Date(activity.value.applicationDeadline) < new Date()
}

function onCloseParticipationDialog() {
  currentParticipation.value = null
  editParticipationSelectionDialog.value = false
  editParticipationDeletionDialog.value = false
}

async function onSaveParticipation(part: Participation) {
  const enrollment = enrollments.value.find(
    (e) => e.volunteerId === part.volunteerId
  )
  if (enrollment) enrollment.participating = true
  participations.value = await RemoteServices.getActivityParticipations(part.activityId)
  currentParticipation.value = null
  editParticipationSelectionDialog.value = false
}

async function onDeleteParticipation(part: Participation) {
  const enrollment = enrollments.value.find(
    (e) => e.volunteerId === part.volunteerId
  )
  if (enrollment) enrollment.participating = false
  participations.value = await RemoteServices.getActivityParticipations(part.activityId)
  currentParticipation.value = null
  editParticipationDeletionDialog.value = false
}

async function deleteParticipation(enrollment: Enrollment) {
  const part = participations.value.find(
    (p) =>
      p.activityId === enrollment.activityId &&
      p.volunteerId === enrollment.volunteerId
  )
  if (part) {
    currentParticipation.value = part
    editParticipationDeletionDialog.value = true
  }
}

function canParticipate(enrollment: Enrollment) {
  return (
    !enrollment.participating &&
    activity.value.participantsNumberLimit >
      enrollments.value.filter((e) => e.participating).length
  )
}

function isParticipating(enrollment: Enrollment) {
  return !!enrollment.participating
}

function volunteerReviewWritten(enrollment: Enrollment) {
  const part = participations.value.find(
    (p) =>
      p.activityId === enrollment.activityId &&
      p.volunteerId === enrollment.volunteerId
  )
  return !!(part?.volunteerRating && part?.volunteerReview)
}

function getMemberReview(enrollment: Enrollment): string {
  const part = participations.value.find(
    (p) =>
      p.activityId === enrollment.activityId &&
      p.volunteerId === enrollment.volunteerId
  )
  if (!part?.memberReview || !part?.memberRating) return ''
  return `${part.memberReview}\nRating: ${convertToStars(part.memberRating)}`
}

function getVolunteerReview(enrollment: Enrollment): string {
  const part = participations.value.find(
    (p) =>
      p.activityId === enrollment.activityId &&
      p.volunteerId === enrollment.volunteerId
  )
  if (!part?.volunteerReview || !part?.volunteerRating) return ''
  return `${part.volunteerReview}\nRating: ${convertToStars(part.volunteerRating)}`
}

function convertToStars(rating: number): string {
  return `${'★'.repeat(Math.floor(rating))}${'☆'.repeat(5 - Math.floor(rating))} ${rating}/5`
}
</script>

<style lang="scss" scoped>
.table {
  overflow-x: auto;
}
.review-text {
  white-space: pre-line;
}
</style>
