<template>
  <LoginCard @login="login" />
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import { useRouter } from 'vue-router';
import { useMainStore } from '@/store/useMainStore';
import LoginCard from '@/components/auth/LoginCard.vue';
import RegisterUser from '@/models/user/RegisterUser';

export default defineComponent({
  name: 'LoginView',

  components: {
    LoginCard,
  },

  setup() {
    const router = useRouter();
    const store = useMainStore();

    const login = async (username: string, password: string) => {
      const user = new RegisterUser();
      user.username = username;
      user.password = password;

      store.setLoading();
      try {
        await store.userLogin(user);
        router.push({ name: 'admin' }).catch(() => {});
      } catch (error: any) {
        store.setError(error.message);
      } finally {
        store.clearLoading();
      }
    };

    return {
      login,
    };
  },
});
</script>
