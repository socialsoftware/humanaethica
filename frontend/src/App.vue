<template>
  <v-app id="app">
    <TopBar />
    <v-main>
      <ErrorMessage />
      <AppNotification />
      <AppLoading />
      <router-view />
    </v-main>
  </v-app>
</template>

<script setup lang="ts">
import { onMounted } from 'vue';
import axios from 'axios';

import TopBar from '@/components/TopBar.vue';
import ErrorMessage from '@/components/ErrorMessage.vue';
import AppNotification from '@/components/Notification.vue';
import AppLoading from '@/components/Loading.vue';

import { useMainStore } from '@/store/useMainStore';

import '@/assets/css/_global.scss';

const store = useMainStore();

onMounted(() => {
  axios.interceptors.response.use(undefined, (err) => {
    return new Promise((_, reject) => {
      if (
        err?.response?.status === 401 &&
        err.config &&
        !err.config.__isRetryRequest
      ) {
        store.logout();
      }
      reject(err);
    });
  });
});
</script>

<style>
#app {
  background-color: white;
  background-position: 0 0;
  background-repeat: no-repeat;
  background-size: 100% 100%;
  height: 100%;
  min-height: 100vh;
  width: 100vw;
  color: #2c3e50;
  content: ' ';
  display: flex;
  flex-direction: column;
  left: 0;
  margin: 0 !important;
  text-align: center;
  top: 0;
}

.application--wrap {
  min-height: initial !important;
}
</style>
