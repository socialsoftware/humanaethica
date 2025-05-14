<template>
  <div class="container">
    <password-card
      :title="TITLE"
      :username="username"
      :error="errorMsg"
      :success="success"
      @onSubmit="confirmRegistration"
    ></password-card>
  </div>
</template>

<script lang="ts">
import PasswordCard from '@/components/auth/PasswordCard.vue';
import RegisterUser from '@/models/user/RegisterUser';
import RemoteServices from '@/services/RemoteServices';

export default {
  name: 'RegistrationConfirmationView',

  components: {
    PasswordCard,
  },

  data() {
    return {
      TITLE: 'Registration Confirmation',
      username: '',
      token: '',
      errorMsg: '',
      success: false,
    };
  },

  async created(this: any) {
    this.username = this.$route.query.username as string;
    this.token = this.$route.query.token as string;
    this.errorMsg = this.username && this.token ? '' : 'Invalid query';
  },

  methods: {
    async confirmRegistration(this: any, password: string) {
      const externalUser = new RegisterUser();
      externalUser.username = this.username;
      externalUser.password = password;
      externalUser.confirmationToken = this.token;

      try {
        const user = await RemoteServices.confirmRegistration(externalUser);
        if (user.active) {
          this.success = true;
        } else {
          this.errorMsg = 'Confirmation link has expired. A new email was sent';
        }
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    },
  },
};
</script>

<style lang="scss" scoped></style>
