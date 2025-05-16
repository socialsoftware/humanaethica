<template>
  <v-card v-if="error === ''">
    <v-card-title>{{ title }}</v-card-title>
    <v-card-text v-if="!success">
      <form>
        <v-text-field
          v-model="passwordState.username"
          label="Username"
          disabled
          required
        />
        <v-text-field
          v-model="passwordState.password"
          :append-icon="showPassword ? 'mdi-eye' : 'mdi-eye-off'"
          :type="showPassword ? 'text' : 'password'"
          label="Password"
          required
          @click:append="showPassword = !showPassword"
        />
        <v-text-field
          v-model="passwordState.confirmPassword"
          :append-icon="showConfirmPassword ? 'mdi-eye' : 'mdi-eye-off'"
          :type="showConfirmPassword ? 'text' : 'password'"
          label="Confirm Password"
          required
          :rules="[
            (v) => v === passwordState.password || 'Passwords don\'t match',
          ]"
          @click:append="showConfirmPassword = !showConfirmPassword"
        />
        <v-btn
          color="blue darken-1"
          class="white--text"
          :disabled="
            !(
              passwordState.password === passwordState.confirmPassword &&
              passwordState.password !== ''
            )
          "
          @click="submit"
        >
          submit
        </v-btn>
      </form>
    </v-card-text>
    <v-card-text v-else>
      <span class="password-success">Success</span>
    </v-card-text>
  </v-card>
  <v-card v-else>
    <v-card-title>{{ error }}</v-card-title>
  </v-card>
</template>

<script lang="ts">
import { defineComponent } from 'vue';

export default defineComponent({
  name: 'PasswordCard',
  props: {
    title: {
      type: String,
      required: true,
    },
    username: {
      type: String,
      required: true,
    },
    error: {
      type: String,
      required: true,
    },
    success: {
      type: Boolean,
      required: true,
    },
  },
  data() {
    return {
      passwordState: {
        password: '',
        confirmPassword: '',
        username: this.username,
      },
      showPassword: false,
      showConfirmPassword: false,
    };
  },
  methods: {
    submit() {
      if (this.passwordState.password === this.passwordState.confirmPassword) {
        this.$emit('onSubmit', this.passwordState.password);
      }
    },
  },
});
</script>

<style scoped lang="scss">
.v-card {
  width: 650px;
  margin: auto;
}
.password-success {
  display: block;
  font-size: 1.5rem;
  margin: 20px 0;
}
</style>
