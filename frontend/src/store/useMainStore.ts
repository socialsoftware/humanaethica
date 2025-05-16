// src/store/useMainStore.ts
import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import RemoteServices from '@/services/RemoteServices';
import type TokenAuthUser from '@/models/user/TokenAuthUser';
import type AuthUser from '@/models/user/AuthUser';
import type RegisterUser from '@/models/user/RegisterUser';
import type Activity from '@/models/activity/Activity';

export const useMainStore = defineStore('main', () => {
  // --- STATE ---
  const token = ref(localStorage.getItem('token') || '');
  const user = ref<AuthUser | null>(
    localStorage.getItem('user') ? JSON.parse(localStorage.getItem('user')!) : null,
  );
  const activity = ref<Activity | null>(
    localStorage.getItem('activity') ? JSON.parse(localStorage.getItem('activity')!) : null,
  );

  const error = ref(false);
  const errorMessage = ref('');
  const notification = ref(false);
  const notificationMessageList = ref<string[]>([]);
  const loading = ref(false);

  // --- GETTERS ---
  const isLoggedIn = computed(() => !!token.value);
  const isAdmin = computed(() => user.value?.role === 'ADMIN');
  const isMember = computed(() => user.value?.role === 'MEMBER');
  const isVolunteer = computed(() => user.value?.role === 'VOLUNTEER');

  // --- ACTIONS ---
  function setActivity(newActivity: Activity) {
    activity.value = newActivity;
    localStorage.setItem('activity', JSON.stringify(newActivity));
  }

  function clearActivity() {
    activity.value = null;
    localStorage.removeItem('activity');
  }

  function login(authResponse: TokenAuthUser) {
    token.value = authResponse.token;
    user.value = authResponse.user;
    localStorage.setItem('token', authResponse.token);
    localStorage.setItem('user', JSON.stringify(authResponse.user));
  }

  function logout() {
    token.value = '';
    user.value = null;
    clearActivity();
    localStorage.setItem('token', '');
    localStorage.setItem('user', '');
  }

  function setError(message: string) {
    error.value = true;
    errorMessage.value = message;
  }

  function clearError() {
    error.value = false;
    errorMessage.value = '';
  }

  function notify(messages: string[]) {
    notification.value = true;
    notificationMessageList.value = messages;
  }

  function clearNotification() {
    notification.value = false;
    notificationMessageList.value = [];
  }

  function setLoading() {
    loading.value = true;
  }

  function clearLoading() {
    loading.value = false;
  }

  async function userLogin(userData: RegisterUser) {
    const authResponse = await RemoteServices.userLogin(userData.username, userData.password);
    login(authResponse);
  }

  async function demoVolunteerLogin() {
    const authResponse = await RemoteServices.demoVolunteerLogin();
    login(authResponse);
  }

  async function demoMemberLogin() {
    const authResponse = await RemoteServices.demoMemberLogin();
    login(authResponse);
  }

  async function adminLogin() {
    const authResponse = await RemoteServices.userLogin('ars', 'ars');
    login(authResponse);
  }

  return {
    // state
    token,
    user,
    activity,
    error,
    errorMessage,
    notification,
    notificationMessageList,
    loading,

    // getters
    isLoggedIn,
    isAdmin,
    isMember,
    isVolunteer,

    // actions
    login,
    logout,
    setActivity,
    clearActivity,
    setError,
    clearError,
    notify,
    clearNotification,
    setLoading,
    clearLoading,
    userLogin,
    demoVolunteerLogin,
    demoMemberLogin,
    adminLogin,
  };
});
