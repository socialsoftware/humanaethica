<template>
  <div class="center-parent">
    <VCard class="container" elevation="11">
      <h2>Volunteer Registration</h2>
      <VBtn class="mb-4" @click="readFile">Download Document</VBtn>
      <VForm ref="form" v-model="valid" lazy-validation>
        <VTextField
          v-model="volunteerName"
          label="Name"
          required
          :rules="[v => !!v || 'Volunteer name is required']"
        />
        <VTextField
          v-model="volunteerEmail"
          label="E-mail"
          required
          :rules="[v => !!v || 'Volunteer email is required']"
        />
        <VTextField
          v-model="volunteerUsername"
          label="Username"
          required
          :rules="[v => !!v || 'Volunteer username is required']"
        />
        <VFileInput
          counter
          show-size
          truncate-length="7"
          label="Declaration"
          required
          :rules="[v => !!v || 'Declaration is required']"
          dense
          small-chips
          accept=".pdf"
          v-model="volunteerDoc"
        />
        <VBtn
          class="mr-4"
          color="primary"
          @click="submit"
          :disabled="!canSubmit"
        >
          Submit
        </VBtn>
        <VBtn @click="clear">Clear</VBtn>
      </VForm>
    </VCard>
  </div>
</template>

<script lang="ts" setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useMainStore } from '@/store/useMainStore'
import RemoteServices from '@/services/RemoteServices'

const store = useMainStore()
const router = useRouter()

const volunteerName = ref('')
const volunteerEmail = ref('')
const volunteerUsername = ref('')
const volunteerDoc = ref<File | null>(null)
const valid = ref(true)
const form = ref()

const canSubmit = computed(() =>
  volunteerName.value !== '' &&
  volunteerEmail.value !== '' &&
  volunteerUsername.value !== '' &&
  volunteerDoc.value !== null
)

async function submit() {
  if (!canSubmit.value) {
    store.setError('Missing information, please check the form again')
    return
  }
  store.setLoading()
  try {
    await RemoteServices.registerVolunteer(
      {
        volunteerName: volunteerName.value,
        volunteerEmail: volunteerEmail.value,
        volunteerUsername: volunteerUsername.value,
      },
      volunteerDoc.value
    )
    router.push({ name: 'home' })
  } catch (error: any) {
    store.setError(error.message)
  }
  store.clearLoading()
}

function readFile() {
  RemoteServices.getForm()
}

function clear() {
  volunteerName.value = ''
  volunteerEmail.value = ''
  volunteerUsername.value = ''
  volunteerDoc.value = null
}
</script>

<style lang="scss" scoped>
.container {
  background-color: grey;
  margin-top: 2rem !important;
  padding: 3rem !important;
  width: 60%;
  flex-direction: row;
  flex-wrap: wrap;
  justify-content: center;
  align-items: stretch;
  align-content: center;
  background-color: rgba(255, 255, 255);
}

.divider {
  margin-top: 2rem !important;
  margin-bottom: 2rem !important;
}

h2 {
  color: black;
  opacity: 80%;
  font-family: 'Open Sans', sans-serif;
  text-align: left;
  font-size: 20px;
  font-weight: 500;
  line-height: 40px;
  margin: 0 0 16px;
}

.center-parent {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
}
</style>
