<template v-if="theme">
  <VDialog
    v-model="dialogModel"
    @keydown.esc="closeDialog"
    max-width="75%"
    max-height="80%"
  >
    <VCard>
      <VForm ref="form" v-model="valid">
        <VCardTitle>
          <span class="headline">Add Theme</span>
        </VCardTitle>
        <VSelect
          v-model="theme.parentTheme"
          label="Parent Theme (Optional)"
          :items="themes"
          return-object
          item-value="id"
          item-title="completeName"
          :menu-props="{ offsetY: true, nudgeLeft: 0, class: 'left-text' }"
          class="move-right"
        >
          <template #item="{ item }">
            <div class="left-text">
              <span class="indentation">{{ item.completeName }}</span>
            </div>
          </template>
        </VSelect>

        <VCardText class="text-left">
          <VTextField
            v-model="theme.name"
            label="Name"
            data-cy="themeNameInput"
            :rules="[v => !!v || 'Name is required']"
            required
          />
          <div class="add-theme-feedback-container">
            <span class="add-theme-feedback" v-if="success">
              {{ theme.name }} added
            </span>
          </div>
        </VCardText>

        <VCardActions>
          <VSpacer />
          <VBtn
            color="primary"
            @click="closeDialog"
            data-cy="cancelButton"
            >Close</VBtn
          >
          <VBtn color="primary" @click="submit" data-cy="saveButton"
            >Add</VBtn
          >
        </VCardActions>
      </VForm>
    </VCard>
  </VDialog>
</template>

<script lang="ts" setup>
import { ref, onMounted, watch } from 'vue'
import { useMainStore } from '@/store/useMainStore'
import RemoteServices from '@/services/RemoteServices'
import Theme from '@/models/theme/Theme'

const props = defineProps<{ dialog: boolean }>()
const emit = defineEmits(['close-dialog', 'theme-created'])

const mainStore = useMainStore()

const dialogModel = ref(props.dialog)
const valid = ref(true)
const success = ref(false)
const theme = ref<Theme | null>(null)
const themes = ref<Theme[]>([])
const form = ref()

const closeDialog = () => emit('close-dialog')

watch(
  () => props.dialog,
  (val) => {
    dialogModel.value = val
    if (val) {
      theme.value = new Theme()
      fetchThemes()
      success.value = false
    }
  }
)

watch(dialogModel, (val) => {
  if (!val) emit('close-dialog')
})

const fetchThemes = async () => {
  themes.value = await RemoteServices.getThemesAvailable()
}

const submit = async () => {
  success.value = false
  if (!form.value || !(await form.value.validate())) return
  try {
    const result = await RemoteServices.registerThemeInstitution(theme.value)
    emit('theme-created', result)
    success.value = true
  } catch (error:any) {
    mainStore.setError(error.message)
  }
}

onMounted(() => {
  theme.value = new Theme()
  fetchThemes()
})
</script>

<style lang="scss" scoped>
.add-theme-feedback-container {
  height: 25px;
}
.add-theme-feedback {
  font-size: 1.05rem;
  color: #1b5e20;
  text-transform: uppercase;
}
.left-text {
  text-align: left;
}
.move-right {
  margin-left: 20px;
  margin-right: 20px;
}
</style>
