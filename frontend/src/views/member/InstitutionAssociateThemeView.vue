<template>
  <VDialog
    v-model="dialogModel"
    max-width="75%"
    max-height="80%"
    @keydown.esc="emit('update:dialog', false)"
  >
    <VCard>
      <VForm ref="form" v-model="valid" lazy-validation>
        <VCardTitle>
          <span class="headline">Associate Theme</span>
        </VCardTitle>
        <VSelect
          v-model="theme"
          label="Theme"
          :items="themes"
          return-object
          item-value="id"
          item-title="completeName"
          required
          :menu-props="{ offsetY: true, class: 'left-text' }"
          class="move-right"
        >
          <template #item="{ item }">
            <div class="left-text">
              <span class="indentation">{{ item.completeName }}</span>
            </div>
          </template>
        </VSelect>
        <VCardActions>
          <VSpacer />
          <VBtn
            color="primary"
            @click="emit('update:dialog', false)"
            data-cy="cancelButton"
          >Close</VBtn>
          <VBtn
            color="primary"
            @click="submit"
            data-cy="saveButton"
          >Add</VBtn>
        </VCardActions>
      </VForm>
    </VCard>
  </VDialog>
</template>

<script lang="ts" setup>
import { ref, watch, onMounted } from 'vue'
import RemoteServices from '@/services/RemoteServices'
import Theme from '@/models/theme/Theme'
import { useMainStore } from '@/store/useMainStore'

const props = defineProps<{
  dialog: boolean
}>()
const emit = defineEmits(['update:dialog', 'theme-associated'])

const dialogModel = ref(props.dialog)
const valid = ref(true)
const themes = ref<Theme[]>([])
const theme = ref<Theme | null>(null)
const form = ref()

watch(
  () => props.dialog,
  (val) => {
    dialogModel.value = val
    if (val) fetchThemes()
  }
)

watch(dialogModel, (val) => {
  emit('update:dialog', val)
})

async function fetchThemes() {
  themes.value = await RemoteServices.getThemesAvailableforInstitution()
}

async function submit() {
  if (!form.value || !(await form.value.validate()) || !theme.value) return
  try {
    await RemoteServices.addThemetoInstitution(theme.value)
    emit('theme-associated')
  } catch (error: any) {
    const store = useMainStore()
    store.setError(error.message)
  }
}

onMounted(() => {
  if (props.dialog) fetchThemes()
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
