<template>
  <VDialog v-model="dialogModel" max-width="75%" max-height="80%">
    <VCard>
      <VForm ref="form" v-model="valid" lazy-validation>
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
          :menu-props="{ offsetY: true, class: 'left-text' }"
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
            :rules="[(value) => !!value || 'Name is required']"
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
          <VBtn color="primary" @click="emit('close-dialog')" data-cy="cancelButton">Close</VBtn>
          <VBtn color="primary" @click="submit" data-cy="saveButton">Add</VBtn>
        </VCardActions>
      </VForm>
    </VCard>
  </VDialog>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import RemoteServices from '@/services/RemoteServices'
import Theme from '@/models/theme/Theme'

const props = defineProps<{ dialog: boolean }>()
const emit = defineEmits(['close-dialog', 'theme-created'])

const valid = ref(true)
const success = ref(false)
const theme = ref<Theme>(new Theme())
const themes = ref<Theme[]>([])
const form = ref()
const dialogModel = ref(props.dialog)

watch(() => props.dialog, (val) => {
  dialogModel.value = val
  if (val) {
    theme.value = new Theme()
    fetchThemes()
    success.value = false
  }
})

watch(dialogModel, (val) => {
  emit('update:dialog', val)
})

async function fetchThemes() {
  themes.value = await RemoteServices.getThemesAvailable()
}

async function submit() {
  success.value = false
  if (!form.value || !(await form.value.validate())) return
  try {
    const newTheme = await RemoteServices.registerThemeInstitution(theme.value)
    emit('theme-created', newTheme)
    success.value = true
  } catch (error: any) {
    // Optionally handle error with store.setError(error.message)
  }
}

onMounted(() => {
  theme.value = new Theme()
  fetchThemes()
})
</script>

<style scoped>
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
