<template>
  <div class="container">
    <login-card @onSubmit="login"></login-card>
  </div>
</template>

<script lang="ts">
import LoginCard from '@/components/auth/LoginCard.vue';
import RegisterUser from '@/models/user/RegisterUser';

export default {
  name: 'LoginView',

  components: {
    LoginCard,
  },

  methods: {
    async login(this: any, username: string, password: string) {
      const user = new RegisterUser();
      user.username = username;
      user.password = password;

      await this.$store.dispatch('loading');
      try {
        await this.$store.dispatch('userLogin', user);
        await this.$router.push({ name: 'admin' }).catch(() => {});
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
      await this.$store.dispatch('clearLoading');
    },
  },
};
</script>

<style lang="scss" scoped></style>
