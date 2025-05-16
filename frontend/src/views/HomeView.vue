<template>
  <div class="container">
    <h1 id="home-title" class="display-2 font-weight-thin mb-3"></h1>

    <div class="image">
      <v-img
        contain
        src="/img/Logo_Vertical.png"
        height="500"
        width="350"
      />
    </div>

    <div class="horizontal-btn-container" v-if="!store.isLoggedIn">
      <v-btn
        href="./login/user"
        depressed
        color="blue accent-1"
        data-cy="userLoginButton"
      >
        <v-icon left>fas fa-sign-in-alt</v-icon>
        User Login
      </v-btn>
    </div>

    <div class="horizontal-btn-container" v-if="!store.isLoggedIn">
      <v-btn
        depressed
        color="blue accent-1"
        @click="demoVolunteer"
        data-cy="demoVolunteerLoginButton"
      >
        <v-icon left>fa fa-graduation-cap</v-icon>
        Demo as volunteer
      </v-btn>
      <v-btn
        depressed
        color="blue accent-1"
        @click="demoMember"
        data-cy="demoMemberLoginButton"
      >
        <v-icon left>fa fa-graduation-cap</v-icon>
        Demo as member
      </v-btn>
      <v-btn
        depressed
        color="blue accent-1"
        @click="loginAdmin"
        data-cy="demoAdminLoginButton"
      >
        <v-icon left>fa fa-graduation-cap</v-icon>
        Login as admin
      </v-btn>
    </div>
  </div>

  <v-footer class="footer">
    © {{ new Date().getFullYear() }} — HumanaEthica
  </v-footer>
</template>

<script setup lang="ts">
import { useMainStore } from '@/store/useMainStore';

const store = useMainStore();

const withLoading = async (fn: () => Promise<void>) => {
  store.setLoading();
  try {
    await fn();
  } catch (error: unknown) {
    store.setError(error instanceof Error ? error.message : 'Unknown error');
  } finally {
    store.clearLoading();
  }
};

const demoVolunteer = () => withLoading(store.demoVolunteerLogin);
const demoMember = () => withLoading(store.demoMemberLogin);
const loginAdmin = () => withLoading(store.adminLogin);
</script>

<style scoped lang="scss">
.container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: calc(100vh - 48px); // ajusta conforme a altura do footer
  padding: 2rem;
  text-align: center;
  background-color: #f9f9f9;
}

.image {
  margin-bottom: 2rem;
}

.horizontal-btn-container {
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
  justify-content: center;
  margin-top: 1rem;
}

.v-btn {
  min-width: 200px;
  font-weight: 500;
}

.footer {
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  font-size: 0.9rem;
  background-color: #eceff1;
  color: #555;
}
</style>
