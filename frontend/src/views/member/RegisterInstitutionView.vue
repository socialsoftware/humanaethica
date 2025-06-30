<template>
  <div class="center-parent">
    <v-card class="container" elevation="11">
      <h2>Institution Registration</h2>
      <v-form ref="form" v-model="valid" lazy-validation>
        <v-text-field
          v-model="institutionName"
          label="Name"
          required
          :rules="[(v) => !!v || 'Institution name is required']"
        ></v-text-field>
        <v-text-field
          v-model="institutionEmail"
          label="E-mail"
          :rules="[(v) => !!v || 'Institution email is required']"
          required
        ></v-text-field>

        <v-text-field
          v-model="institutionNif"
          label="NIF"
          required
          :rules="[(v) => !!v || 'Institution NIF is required']"
        ></v-text-field>

        <v-file-input
          counter
          show-size
          truncate-length="7"
          label="Declaration"
          required
          :rules="[(v) => !!v || 'Declaration is required']"
          dense
          small-chips
          accept=".pdf"
          v-model="institutionDoc"
        ></v-file-input>

        <v-divider class="divider"></v-divider>

        <h2>Member Registration</h2>
        <v-btn class="mb-4" @click="readFile"> Download Document </v-btn>

        <v-text-field
          v-model="memberUsername"
          :counter="10"
          label="Username"
          required
          :rules="[(v) => !!v || 'Member username is required']"
        ></v-text-field>

        <v-text-field
          v-model="memberEmail"
          label="E-mail"
          required
          :rules="[(v) => !!v || 'Member email is required']"
        ></v-text-field>

        <v-text-field
          v-model="memberName"
          label="Name"
          required
          :rules="[(v) => !!v || 'Member name is required']"
        ></v-text-field>

        <v-file-input
          counter
          show-size
          truncate-length="7"
          label="Declaration"
          required
          :rules="[(v) => !!v || 'Declaration is required']"
          dense
          small-chips
          accept=".pdf"
          v-model="memberDoc"
        ></v-file-input>

        <v-btn
          class="mr-4"
          color="primary"
          @click="submit"
          :disabled="!canSubmit"
        >
          submit
        </v-btn>
        <v-btn @click="clear"> clear </v-btn>
      </v-form>
    </v-card>
  </div>
</template>

<script lang="ts" setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useMainStore } from '@/store/useMainStore'
import RemoteServices from '@/services/RemoteServices'

const store = useMainStore()
const router = useRouter()

const institutionName = ref('')
const institutionEmail = ref('')
const institutionNif = ref('')
const memberUsername = ref('')
const memberEmail = ref('')
const memberName = ref('')
const institutionDoc = ref<File | null>(null)
const memberDoc = ref<File | null>(null)
const form = ref()
const valid = ref(true)

const canSubmit = computed(() =>
  institutionName.value !== '' &&
  institutionEmail.value !== '' &&
  institutionNif.value !== '' &&
  institutionDoc.value !== null &&
  memberUsername.value !== '' &&
  memberEmail.value !== '' &&
  memberName.value !== '' &&
  memberDoc.value !== null
)

async function submit() {
  if (!canSubmit.value) {
    store.setError('Missing information, please check the form again')
    return
  }
  store.setLoading()
  try {
    await RemoteServices.registerInstitution(
      {
        institutionName: institutionName.value,
        institutionEmail: institutionEmail.value,
        institutionNif: institutionNif.value,
        memberUsername: memberUsername.value,
        memberEmail: memberEmail.value,
        memberName: memberName.value,
      },
      institutionDoc.value,
      memberDoc.value,
    )
    router.push({ name: 'home' })
  } catch (error: any) {
    store.setError(error.message || 'Registration failed')
  }
  store.clearLoading()
}

function readFile() {
  RemoteServices.getForm()
}

function clear() {
  institutionName.value = ''
  institutionEmail.value = ''
  institutionNif.value = ''
  memberUsername.value = ''
  memberEmail.value = ''
  memberName.value = ''
  institutionDoc.value = null
  memberDoc.value = null
}

</script>

<style lang="scss" scoped>
.center-parent {
  margin-top: 6rem !important;
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
}

.container {
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
</style>
