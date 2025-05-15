<script setup lang="ts">
import { useMainStore } from '@/store/useMainStore';

const store = useMainStore();

const demoVolunteer = async () => {
  store.setLoading();
  try {
    await store.demoVolunteerLogin();
  } catch (error) {
    store.setError(error as string);
  }
  store.clearLoading();
};

const demoMember = async () => {
  store.setLoading();
  try {
    await store.demoMemberLogin();
  } catch (error) {
    store.setError(error as string);
  }
  store.clearLoading();
};

const loginAdmin = async () => {
  store.setLoading();
  try {
    await store.adminLogin();
  } catch (error) {
    store.setError(error as string);
  }
  store.clearLoading();
};
</script>

<template>
  <div class="container">
    <h1 id="home-title" class="display-2 font-weight-thin mb-3"></h1>
    <div class="image">
      <v-img
        contain
        src="../assets/img/Logo_Vertical.png"
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
        User Login <v-icon>fas fa-sign-in-alt</v-icon>
      </v-btn>
    </div>

    <div class="horizontal-btn-container" v-if="!store.isLoggedIn">
      <v-btn
        depressed
        color="blue accent-1"
        @click="demoVolunteer"
        data-cy="demoVolunteerLoginButton"
      >
        <i class="fa fa-graduation-cap" />Demo as volunteer
      </v-btn>
      <v-btn
        depressed
        color="blue accent-1"
        @click="demoMember"
        data-cy="demoMemberLoginButton"
      >
        <i class="fa fa-graduation-cap" />Demo as member
      </v-btn>
      <v-btn
        depressed
        color="blue accent-1"
        @click="loginAdmin"
        data-cy="demoAdminLoginButton"
      >
        <i class="fa fa-graduation-cap" />Login as admin
      </v-btn>
    </div>

    <v-footer class="footer"> </v-footer>
  </div>
</template>
