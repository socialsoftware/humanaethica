<template>
  <VCard>
    <VCardTitle>Login</VCardTitle>
    <VCardText>
      <form @submit.prevent="submit">
        <VTextField
          v-model="username"
          label="Username"
          required
          data-cy="usernameField"
        />
        <VTextField
          v-model="password"
          :append-icon="showPassword ? 'mdi-eye' : 'mdi-eye-off'"
          :type="showPassword ? 'text' : 'password'"
          label="Password"
          required
          @click:append="togglePassword"
          data-cy="passwordField"
        />
        <VBtn
          class="white--text"
          color="primary"
          :disabled="username === '' || password === ''"
          @click="submit"
          data-cy="submitButton"
        >
          login
        </VBtn>
      </form>
    </VCardText>
  </VCard>
</template>

<script setup lang="ts">
import { ref, defineEmits } from 'vue';

const username = ref('');
const password = ref('');
const showPassword = ref(false);

const emit = defineEmits<{
  (e: 'login', username: string, password: string): void;
}>();

function togglePassword() {
  showPassword.value = !showPassword.value;
}

function submit() {
  emit('login', username.value, password.value);
}
</script>

<style scoped lang="scss">
.v-card {
  width: 650px;
  margin: 32px auto;
}
</style>
