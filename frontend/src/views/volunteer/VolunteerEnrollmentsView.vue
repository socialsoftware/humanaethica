<template>
  <VCard class="table">
    <VDataTable
      :headers="headers"
      :items="activities"
      :search="search"
      disable-pagination
      :hide-default-footer="true"
      :mobile-breakpoint="0"
      data-cy="volunteerEnrollmentsTable"
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
      <template #item.themes="{ item }">
        <VChip v-for="theme in item.themes" :key="theme.id">
          {{ theme.completeName }}
        </VChip>
      </template>
      <template #item.memberReview="{ item }">
        <span class="review-text">{{ getMemberReview(item) }}</span>
      </template>
      <template #item.volunteerReview="{ item }">
        <span class="review-text">{{ getVolunteerReview(item) }}</span>
      </template>
      <template #item.state="{ item }">
        <VTooltip location="bottom">
          <template #activator="{ props }">
            <VChip
              v-if="item.state === 'REPORTED'"
              class="mouseover"
              @mouseover="showJustification(item)"
              @mouseleave="hideJustification(item)"
              v-bind="props"
            >
              {{ item.state }}
            </VChip>
            <VChip v-else>
              {{ item.state }}
            </VChip>
          </template>
          <span v-html="formattedJustification(item.justification)"></span>
        </VTooltip>
      </template>
      <template #item.action="{ item }">
        <VTooltip v-if="item.state === 'APPROVED' && canReport(item)" location="bottom">
          <template #activator="{ props }">
            <VIcon
              class="mr-2 action-button"
              color="red"
              v-bind="props"
              data-cy="reportButton"
              @click="reportActivity(item)"
              aria-label="Report Activity"
              role="button"
              tabindex="0"
            >
              mdi-alert
            </VIcon>
          </template>
          <span>Report Activity</span>
        </VTooltip>
        <VTooltip v-if="item.state === 'REPORTED' && canUnReport(item)" location="bottom">
          <template #activator="{ props }">
            <VIcon
              class="mr-2 action-button"
              color="blue"
              v-bind="props"
              data-cy="UnReportButton"
              @click="unReportActivity(item)"
              aria-label="Unreport Activity"
              role="button"
              tabindex="0"
            >
              mdi-alert
            </VIcon>
          </template>
          <span>Unreport Activity</span>
        </VTooltip>
        <VTooltip v-if="item.state === 'APPROVED' && canEditOrRemoveEnroll(item)" location="bottom">
          <template #activator="{ props }">
            <VIcon
              class="mr-2 action-button"
              color="red"
              v-bind="props"
              data-cy="deleteEnrollmentButton"
              @click="deleteEnrollmentForActivity(item)"
              aria-label="Delete Enrollment"
              role="button"
              tabindex="0"
            >
              mdi-logout
            </VIcon>
          </template>
          <span>Delete Enrollment</span>
        </VTooltip>
        <VTooltip v-if="item.state === 'APPROVED' && canEditOrRemoveEnroll(item)" location="bottom">
          <template #activator="{ props }">
            <VIcon
              class="mr-2 action-button"
              color="blue"
              v-bind="props"
              data-cy="updateEnrollmentButton"
              @click="editEnrollmentForActivity(item)"
              aria-label="Edit Enrollment"
              role="button"
              tabindex="0"
            >
              mdi-pencil
            </VIcon>
          </template>
          <span>Edit Enrollment</span>
        </VTooltip>
        <VTooltip v-if="item.state === 'APPROVED' && canAssess(item)" location="bottom">
          <template #activator="{ props }">
            <VIcon
              class="mr-2 action-button"
              color="blue"
              v-bind="props"
              data-cy="writeAssessmentButton"
              @click="writeAssessment(item)"
              aria-label="Write Assessment"
              role="button"
              tabindex="0"
            >
              mdi-pencil
            </VIcon>
          </template>
          <span>Write Assessment</span>
        </VTooltip>
        <VTooltip v-if="canRate(item) && !memberAndVolunteerRatingExist(item)" location="bottom">
          <template #activator="{ props }">
            <VIcon
              class="mr-2 action-button"
              color="blue"
              v-bind="props"
              data-cy="writeParticipationButton"
              @click="giveRating(item)"
              aria-label="Give Rating"
              role="button"
              tabindex="0"
            >
              mdi-star
            </VIcon>
          </template>
          <span>Give Rating</span>
        </VTooltip>
      </template>
    </VDataTable>
    <EnrollmentDialog
      v-if="currentEnrollment && editEnrollmentDialog"
      v-model:dialog="editEnrollmentDialog"
      :enrollment="currentEnrollment"
      @update-enrollment="onUpdateEnrollment"
      @close-enrollment-dialog="onCloseEnrollmentDialog"
    />
    <AssessmentDialog
      v-if="currentAssessment && editAssessmentDialog"
      v-model:dialog="editAssessmentDialog"
      :assessment="currentAssessment"
      @save-assessment="onSaveAssessment"
      @close-assessment-dialog="onCloseAssessmentDialog"
    />
    <ParticipationDialog
      v-if="currentParticipation && editParticipationDialog"
      v-model:dialog="editParticipationDialog"
      :participation="currentParticipation"
      @save-participation="onSaveParticipation"
      @close-participation-dialog="onCloseParticipationDialog"
    />
    <ReportDialog
      v-if="currentReport && createReportDialog"
      v-model:dialog="createReportDialog"
      :report="currentReport"
      @save-report="onSaveReport"
      @close-report-dialog="onCloseReportDialog"
    />
  </VCard>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { useMainStore } from '@/store/useMainStore'
import RemoteServices from '@/services/RemoteServices'
import Activity from '@/models/activity/Activity'
import EnrollmentDialog from '@/views/volunteer/EnrollmentDialog.vue'
import Enrollment from '@/models/enrollment/Enrollment'
import AssessmentDialog from '@/views/volunteer/AssessmentDialog.vue'
import Assessment from '@/models/assessment/Assessment'
import Participation from '@/models/participation/Participation'
import ParticipationDialog from '@/views/volunteer/ParticipationDialog.vue'
import ReportDialog from '@/views/volunteer/ReportDialog.vue'
import Report from '@/models/report/Report'

const store = useMainStore()

const activities = ref<Activity[]>([])
const enrollments = ref<Enrollment[]>([])
const participations = ref<Participation[]>([])
const assessments = ref<Assessment[]>([])
const reports = ref<Report[]>([])
const search = ref('')
const currentEnrollment = ref<Enrollment | null>(null)
const editEnrollmentDialog = ref(false)
const currentAssessment = ref<Assessment | null>(null)
const editAssessmentDialog = ref(false)
const currentParticipation = ref<Participation | null>(null)
const editParticipationDialog = ref(false)
const currentActivity = ref<Activity | null>(null)
const currentReport = ref<Report | null>(null)
const createReportDialog = ref(false)

const headers = [
  { title: 'Name', key: 'name', align: 'left', width: '5%' },
  { title: 'Region', key: 'region', align: 'left', width: '5%' },
  { title: 'Institution', key: 'institution.name', align: 'left', width: '5%' },
  { title: 'Participants Limit', key: 'participantsNumberLimit', align: 'left', width: '5%' },
  { title: 'Themes', key: 'themes', align: 'left', width: '5%' },
  { title: 'Description', key: 'description', align: 'left', width: '30%' },
  { title: 'Member Rating', key: 'memberReview', align: 'left', width: '20%' },
  { title: 'Volunteer Rating', key: 'volunteerReview', align: 'left', width: '20%' },
  { title: 'State', key: 'state', align: 'left', width: '5%' },
  { title: 'Start Date', key: 'formattedStartingDate', align: 'left', width: '5%' },
  { title: 'End Date', key: 'formattedEndingDate', align: 'left', width: '5%' },
  { title: 'Application Deadline', key: 'formattedApplicationDeadline', align: 'left', width: '5%' },
  { title: 'Actions', key: 'action', align: 'left', sortable: false, width: '5%' },
]

onMounted(async () => {
  store.setLoading()
  try {
    activities.value = await RemoteServices.getActivities()
    enrollments.value = await RemoteServices.getVolunteerEnrollments()
    participations.value = await RemoteServices.getVolunteerParticipations()
    assessments.value = await RemoteServices.getVolunteerAssessments()
    reports.value = await RemoteServices.getVolunteerReportsAsVolunteer()
    updateActivitiesList()
  } catch (error: any) {
    store.setError(error.message)
  }
  store.clearLoading()
})

function reportActivity(activity: Activity) {
  currentReport.value = new Report()
  currentReport.value.activityId = activity.id
  createReportDialog.value = true
  currentActivity.value = activity
}

function getMemberReview(activity: Activity): string {
  const participation = participations.value.find(
    (p) => p.activityId === activity.id
  )
  if (
    !participation ||
    participation.memberReview == null ||
    participation.memberRating == null
  ) {
    return ''
  }
  const stars = convertToStars(participation.memberRating)
  return `${participation.memberReview}\nRating: ${stars}`
}

function getVolunteerReview(activity: Activity): string {
  const participation = participations.value.find(
    (p) => p.activityId === activity.id
  )
  if (
    !participation ||
    participation.volunteerReview == null ||
    participation.volunteerRating == null
  ) {
    return ''
  }
  const stars = convertToStars(participation.volunteerRating)
  return `${participation.volunteerReview}\nRating: ${stars}`
}

function convertToStars(rating: number): string {
  const fullStars = '★'.repeat(Math.floor(rating))
  const emptyStars = '☆'.repeat(Math.floor(5 - rating))
  return `${fullStars}${emptyStars} ${rating}/5`
}

async function deleteEnrollmentForActivity(activity: Activity) {
  const enrollment = enrollments.value.find((e) => e.activityId == activity.id)
  currentEnrollment.value = enrollment ?? null
  if (currentEnrollment.value?.id !== null) {
    try {
      await RemoteServices.removeEnrollment(currentEnrollment.value.id)
      enrollments.value = await RemoteServices.getVolunteerEnrollments()
    } catch (error: any) {
      store.setError(error.message)
    }
  }
  currentEnrollment.value = null
  updateActivitiesList()
}

function canEditOrRemoveEnroll(activity: Activity) {
  const deadline = new Date(activity.applicationDeadline)
  const now = new Date()
  return (
    deadline > now &&
    enrollments.value.some((e) => e.activityId === activity.id)
  )
}

function canRate(activity: Activity) {
  const enrollment = enrollments.value.find((e) => e.activityId === activity.id)
  const endDate = new Date(activity.endingDate)
  const now = new Date()
  return now > endDate && enrollment && enrollment.participating
}

function editEnrollmentForActivity(activity: Activity) {
  const enrollment = enrollments.value.find((e) => e.activityId == activity.id)
  currentEnrollment.value = enrollment ?? null
  editEnrollmentDialog.value = true
}

function onCloseEnrollmentDialog() {
  editEnrollmentDialog.value = false
  currentEnrollment.value = null
}

async function onUpdateEnrollment(enrollment: Enrollment) {
  enrollments.value.push(enrollment)
  editEnrollmentDialog.value = false
  currentEnrollment.value = null
  enrollments.value = await RemoteServices.getVolunteerEnrollments()
}

function updateActivitiesList() {
  activities.value = activities.value.filter((a) =>
    enrollments.value.some((e) => e.activityId === a.id)
  )
}

function canAssess(activity: Activity) {
  const endDate = new Date(activity.endingDate)
  const now = new Date()
  return (
    now > endDate &&
    participations.value.some((p) => p.activityId === activity.id) &&
    !assessments.value.some(
      (a) => a.institutionId === activity.institution.id
    )
  )
}

function giveRating(activity: Activity) {
  const activityId = activity.id
  const enrollment = enrollments.value.find((e) => e.activityId === activity.id)
  const volunteerId = enrollment?.volunteerId
  const existingParticipation = participations.value.find(
    (p) => p.activityId === activityId && p.volunteerId === volunteerId
  )
  if (existingParticipation) {
    currentParticipation.value = { ...existingParticipation }
    editParticipationDialog.value = true
  }
}

function memberAndVolunteerRatingExist(activity: Activity) {
  const enrollment = enrollments.value.find((e) => e.activityId === activity.id)
  if (enrollment) {
    const existingParticipation = participations.value.find(
      (p) =>
        p.activityId === activity.id &&
        p.volunteerId === enrollment.volunteerId &&
        p.memberRating &&
        p.volunteerRating
    )
    if (existingParticipation) {
      return true
    }
  }
  return false
}

function onCloseParticipationDialog() {
  editParticipationDialog.value = false
  currentParticipation.value = null
}

async function onSaveParticipation() {
  editParticipationDialog.value = false
  currentParticipation.value = null
  participations.value = await RemoteServices.getVolunteerParticipations()
}

function onCloseAssessmentDialog() {
  editAssessmentDialog.value = false
  currentAssessment.value = null
}

function writeAssessment(activity: Activity) {
  currentAssessment.value = new Assessment()
  currentAssessment.value.institutionId = activity.institution.id
  editAssessmentDialog.value = true
}

async function onSaveAssessment(assessment: Assessment) {
  assessments.value.push(assessment)
  editAssessmentDialog.value = false
  currentAssessment.value = null
}

function canReport(activity: Activity) {
  const deadline = new Date(activity.endingDate)
  const now = new Date()
  return (
    deadline > now &&
    !reports.value.some((r) => r.activityId === activity.id)
  )
}

function onCloseReportDialog() {
  createReportDialog.value = false
  currentReport.value = null
}

async function onSaveReport(report: Report) {
  reports.value.push(report)
  createReportDialog.value = false
  currentReport.value = null
  if (currentActivity.value != null && currentActivity.value.id !== null) {
    try {
      const result = await RemoteServices.reportActivity(
        store.user.id,
        currentActivity.value.id
      )
      activities.value = activities.value.filter(
        (a) => a.id !== currentActivity.value!.id
      )
      activities.value.unshift(result)
    } catch (error: any) {
      store.setError(error.message)
    }
  }
  currentActivity.value = null
}

function canUnReport(activity: Activity) {
  const deadline = new Date(activity.endingDate)
  const now = new Date()
  return (
    deadline > now && reports.value.some((r) => r.activityId === activity.id)
  )
}

async function unReportActivity(activity: Activity) {
  const index = reports.value.findIndex((r) => r.activityId == activity.id)
  currentReport.value = reports.value[index]
  if (activity.id !== null) {
    try {
      const result = await RemoteServices.validateActivity(activity.id)
      activities.value = activities.value.filter((a) => a.id !== activity.id)
      activities.value.unshift(result)
    } catch (error: any) {
      store.setError(error.message)
    }
  }
  if (currentReport.value?.id !== null) {
    try {
      await RemoteServices.deleteReport(currentReport.value.id)
      reports.value = await RemoteServices.getVolunteerReportsAsVolunteer()
    } catch (error: any) {
      store.setError(error.message)
    }
  }
  currentReport.value = null
}

async function showJustification(activity: Activity) {
  const index = reports.value.findIndex((r) => r.activityId == activity.id)
  currentReport.value = reports.value[index]
  if (currentReport.value) {
    activity.justification = currentReport.value.justification
    activity.showJustification = true
  }
}

function hideJustification(activity: Activity) {
  activity.showJustification = false
}

function formattedJustification(justification: string) {
  if (!justification) return ''
  return justification.replace(/(.{20})/g, '$1<br>')
}
</script>

<style lang="scss" scoped>
.review-text {
  white-space: pre-line;
}
.mouseover {
  cursor: pointer;
}
</style>
