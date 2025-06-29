<template>
  <VDialog
    v-model="dialogModel"
    max-width="90%"
    max-height="90%"
    @keydown.esc="emit('close-dialog')"
  >
    <VCard>
      <VCardTitle>
        <span class="headline">Themes</span>
      </VCardTitle>
      <VCardText class="theme-list">
        <VList>
          <VListItemGroup v-model="selectedTheme">
            <VListItem
              v-for="theme in themes"
              :key="theme.id"
              :value="theme.name"
            >
              <VListItemContent>
                <div
                  class="left-text"
                  :style="{ color: getThemeColor(theme.state) }"
                >
                  <span class="indentation">{{ getIndentedDisplayName(theme.level) }}</span>
                  &#9658;{{ theme.name }}
                </div>
              </VListItemContent>
            </VListItem>
          </VListItemGroup>
        </VList>
      </VCardText>
      <VCardActions>
        <VSpacer />
        <VBtn
          color="primary"
          @click="emit('close-dialog')"
          data-cy="cancelButton"
        >Close</VBtn>
      </VCardActions>
    </VCard>
  </VDialog>
</template>

<script lang="ts" setup>
import { ref, watch, onMounted } from 'vue'
import Theme from '@/models/theme/Theme'
import RemoteServices from '@/services/RemoteServices'

const props = defineProps<{
  dialog: boolean
}>()

const emit = defineEmits(['close-dialog', 'tree-error'])

const dialogModel = ref(props.dialog)
const themes = ref<Theme[]>([])
const selectedTheme = ref<string | null>(null)

watch(
  () => props.dialog,
  (val) => {
    dialogModel.value = val
    if (val) loadThemes()
  }
)

watch(dialogModel, (val) => {
  if (!val) emit('close-dialog')
})

async function loadThemes() {
  try {
    themes.value = await RemoteServices.getThemes()
  } catch (error: any) {
    emit('tree-error', error.message || 'Unable to connect to server')
    dialogModel.value = false
  }
}

function getIndentedDisplayName(level?: number): string {
  const tabChar = '\u00A0'
  return tabChar.repeat((level ?? 0) * 10)
}

function getThemeColor(state: string): string {
  switch (state) {
    case 'SUBMITTED':
      return 'orange'
    case 'APPROVED':
      return 'green'
    case 'DELETED':
      return 'red'
    default:
      return ''
  }
}

onMounted(() => {
  if (props.dialog) loadThemes()
})
</script>

<style scoped>
.theme-list {
  max-height: 500px;
  overflow-y: auto;
}
.left-text {
  text-align: left;
}
.indentation {
  margin-right: 5px;
}
</style>
