<template>
  <nav>
    <VAppBar app clipped-left color="white">
      <VAppBarNavIcon
        aria-label="Menu"
        class="hidden-md-and-up"
        @click.stop="drawer = !drawer"
      />

      <VToolbarTitle data-cy="homeLink">
        <RouterLink to="/">
          <VImg
            cover
            src="/img/logo_horizontal.png"
            height="40"
            width="350"
          />
        </RouterLink>
      </VToolbarTitle>

      <VSpacer />

      <VMenu v-if="isVolunteer" offset-y open-on-hover>
        <template #activator="{ props }">
          <div v-bind="props">
            <VBtn variant="text" color="orange" data-cy="volunteerActivities" @click="volunteerActivities">
              Activities
              <VIcon icon="fas fa-user" />
            </VBtn>
            <VBtn variant="text" color="orange" data-cy="volunteerEnrollments" @click="volunteerEnrollments">
              Enrollments
              <VIcon icon="fas fa-user" />
            </VBtn>
            <VBtn variant="text" color="orange" data-cy="volunteerAssessments" @click="volunteerAssessments">
              Assessments
              <VIcon icon="fas fa-user" />
            </VBtn>
          </div>
        </template>
      </VMenu>

      <VMenu v-if="isMember" offset-y open-on-hover>
        <template #activator="{ props }">
          <VBtn variant="text" color="orange" v-bind="props" data-cy="institution">Institution</VBtn>
        </template>
        <VList density="compact">
          <VListItem to="/member/register" data-cy="members"><VListItemTitle>Register Member</VListItemTitle></VListItem>
          <VListItem to="/member/themes" data-cy="themes"><VListItemTitle>Themes</VListItemTitle></VListItem>
          <VListItem to="/member/activities" data-cy="activities"><VListItemTitle>Activities</VListItemTitle></VListItem>
          <VListItem to="/member/assessments" data-cy="assessments"><VListItemTitle>Assessments</VListItemTitle></VListItem>
        </VList>
      </VMenu>

      <VMenu v-if="isAdmin" offset-y open-on-hover>
        <template #activator="{ props }">
          <VBtn variant="text" color="orange" v-bind="props" data-cy="admin">Administration</VBtn>
        </template>
        <VList density="compact">
          <VListItem to="/admin/users" data-cy="adminUsers"><VListItemAction><VIcon icon="fas fa-users" /></VListItemAction><VListItemTitle>Users</VListItemTitle></VListItem>
          <VListItem to="/admin/institutions" data-cy="adminInstitutions"><VListItemAction><VIcon icon="fas fa-users" /></VListItemAction><VListItemTitle>Institutions</VListItemTitle></VListItem>
          <VListItem to="/admin/themes" data-cy="adminThemes"><VListItemAction><VIcon icon="fas fa-users" /></VListItemAction><VListItemTitle>Themes</VListItemTitle></VListItem>
          <VListItem to="/admin/activities" data-cy="adminActivities"><VListItemAction><VIcon icon="fas fa-users" /></VListItemAction><VListItemTitle>Activities</VListItemTitle></VListItem>
        </VList>
      </VMenu>

      <VToolbarItems class="hidden-sm-and-down">
        <VBtn v-if="!isLoggedIn" variant="text" color="orange" @click="registerInstitution">Register Institution</VBtn>
        <VBtn v-if="!isLoggedIn" variant="text" color="orange" @click="registerVolunteer">Register Volunteer</VBtn>
        <VBtn v-if="!isLoggedIn" variant="text" color="orange" @click="login">
          Login
          <VIcon icon="fas fa-sign-in-alt" />
        </VBtn>
        <VBtn v-if="isLoggedIn" variant="text" color="orange" data-cy="logoutButton" @click="logout">
          Logout
          <VIcon icon="fas fa-sign-out-alt" />
        </VBtn>
      </VToolbarItems>
    </VAppBar>

    <VNavigationDrawer v-model="drawer" absolute app temporary>
      <VToolbar flat>
        <VList><VListItemTitle class="title">Menu</VListItemTitle></VList>
      </VToolbar>

      <VList density="compact" class="pt-0">
        <VListGroup v-if="isVolunteer" :value="false" prepend-icon="fas fa-user">
          <template #activator><VListItemTitle>Volunteer</VListItemTitle></template>
          <VListItem @click="volunteerActivities"><VListItemTitle>Activities</VListItemTitle></VListItem>
          <VListItem @click="volunteerEnrollments"><VListItemTitle>Enrollments</VListItemTitle></VListItem>
          <VListItem @click="volunteerAssessments"><VListItemTitle>Assessments</VListItemTitle></VListItem>
        </VListGroup>

        <VListGroup v-if="isMember" :value="false" prepend-icon="fas fa-building">
          <template #activator><VListItemTitle>Institution</VListItemTitle></template>
          <VListItem to="/member/register"><VListItemTitle>Register Member</VListItemTitle></VListItem>
          <VListItem to="/member/themes"><VListItemTitle>Themes</VListItemTitle></VListItem>
          <VListItem to="/member/activities"><VListItemTitle>Activities</VListItemTitle></VListItem>
          <VListItem to="/member/assessments"><VListItemTitle>Assessments</VListItemTitle></VListItem>
        </VListGroup>

        <VListGroup v-if="isAdmin" :value="false" prepend-icon="fas fa-cog">
          <template #activator><VListItemTitle>Administration</VListItemTitle></template>
          <VListItem to="/admin/users"><VListItemTitle>Users</VListItemTitle></VListItem>
          <VListItem to="/admin/institutions"><VListItemTitle>Institutions</VListItemTitle></VListItem>
          <VListItem to="/admin/themes"><VListItemTitle>Themes</VListItemTitle></VListItem>
          <VListItem to="/admin/activities"><VListItemTitle>Activities</VListItemTitle></VListItem>
        </VListGroup>

        <VListItem v-if="!isLoggedIn" @click="login">
          <VListItemAction><VIcon icon="fas fa-sign-in-alt" /></VListItemAction>
          <VListItemTitle>Login</VListItemTitle>
        </VListItem>

        <VListItem v-if="isLoggedIn" @click="logout">
          <VListItemAction><VIcon icon="fas fa-sign-out-alt" /></VListItemAction>
          <VListItemTitle>Logout</VListItemTitle>
        </VListItem>
      </VList>
    </VNavigationDrawer>
  </nav>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue';
import { useRouter } from 'vue-router';
import { useMainStore } from '@/store/useMainStore';

const drawer = ref(false);
const router = useRouter();
const store = useMainStore();

const isLoggedIn = computed(() => store.isLoggedIn);
const isMember = computed(() => store.isMember);
const isAdmin = computed(() => store.isAdmin);
const isVolunteer = computed(() => store.isVolunteer);

const logout = async () => {
  await store.logout();
  await router.push({ name: 'home' }).catch(() => {});
};

const login = () => router.push({ name: 'login-user' });
const registerInstitution = () => router.push({ name: 'register-institution' });
const registerVolunteer = () => router.push({ name: 'register-volunteer' });
const volunteerActivities = () => router.push({ name: 'volunteer-activities' });
const volunteerEnrollments = () => router.push({ name: 'volunteer-enrollments' });
const volunteerAssessments = () => router.push({ name: 'volunteer-assessments' });

watch(() => router.currentRoute.value.fullPath, () => {
  drawer.value = false;
});
</script>

<style scoped lang="scss">
.no-active::before {
  opacity: 0 !important;
}

nav {
  z-index: 0;
}
</style>
