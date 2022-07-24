<template>
  <v-card>
    <v-card-title>Login</v-card-title>
    <v-card-text>
      <form>
        <v-text-field
          v-model="username"
          label="Username"
          required
          data-cy="usernameField"
        ></v-text-field>
        <v-text-field
          v-model="password"
          :append-icon="showPassword ? 'mdi-eye' : 'mdi-eye-off'"
          :type="showPassword ? 'text' : 'password'"
          label="Password"
          required
          @click:append="showPassword = !showPassword"
          data-cy="passwordField"
        ></v-text-field>
        <v-btn
          class="white--text"
          color="orange"
          :disabled="!(username !== '' && password !== '')"
          @click="submit"
          data-cy="submitButton"
          >login</v-btn
        >
      </form>
    </v-card-text>
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';

@Component
export default class LoginCard extends Vue {
  username: string = '';
  password: string = '';
  showPassword: boolean = false;

  submit() {
    this.$emit('onSubmit', this.username, this.password);
  }
}
</script>

<style scoped lang="scss">
.v-card {
  width: 650px;
  margin: 32px auto;
}
</style>
