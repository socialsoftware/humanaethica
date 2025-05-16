// src/router/index.ts
import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router';
import { useMainStore } from '@/store/useMainStore'; // assumes Pinia store is already set up

import RegistrationConfirmationView from '@/views/user/RegistrationConfirmationView.vue';
import HomeView from '@/views/HomeView.vue';
import NotFoundView from '@/views/NotFoundView.vue';
import LoginView from '@/views/user/LoginView.vue';
import AdminUsersView from '@/views/admin/AdminUsersView.vue';
import AdminInstitutionsView from '@/views/admin/AdminInstitutionsView.vue';
import AdminThemesView from '@/views/admin/AdminThemesView.vue';
import AdminView from '@/views/admin/AdminView.vue';
import RegisterInstitutionView from '@/views/member/RegisterInstitutionView.vue';
import RegisterVolunteerView from '@/views/volunteer/RegisterVolunteerView.vue';
import RegisterMemberView from '@/views/member/RegisterMemberView.vue';
import InstitutionActivitiesView from '@/views/member/InstitutionActivitiesView.vue';
import AdminActivitiesView from '@/views/admin/AdminActivitiesView.vue';
import VolunteerActivitiesView from '@/views/volunteer/VolunteerActivitiesView.vue';
import VolunteerAssessmentsView from '@/views/volunteer/VolunteerAssessmentsView.vue';
import InstitutionThemeView from '@/views/member/InstitutionThemeView.vue';
import MemberView from '@/views/member/MemberView.vue';
import VolunteerView from '@/views/volunteer/VolunteerView.vue';
import InstitutionActivityEnrollmentsView from '@/views/member/InstitutionActivityEnrollmentsView.vue';
import InstitutionAssessmentsView from '@/views/member/InstitutionAssessmentsView.vue';
import VolunteerEnrollmentsView from '@/views/volunteer/VolunteerEnrollmentsView.vue';

const APP_NAME = import.meta.env.VITE_APP_NAME || '';

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'home',
    component: HomeView,
    meta: { title: APP_NAME, requiredAuth: 'None' },
  },
  {
    path: '/login/user',
    name: 'login-user',
    component: LoginView,
    meta: { title: `${APP_NAME} - Login External`, requiredAuth: 'None' },
  },
  {
    path: '/register/institution',
    name: 'register-institution',
    component: RegisterInstitutionView,
    meta: { title: `${APP_NAME} - Institution Registration`, requiredAuth: 'None' },
  },
  {
    path: '/register/volunteer',
    name: 'register-volunteer',
    component: RegisterVolunteerView,
    meta: { title: `${APP_NAME} - Volunteer Registration`, requiredAuth: 'None' },
  },
  {
    path: '/register/confirmation',
    name: 'registration-confirmation',
    component: RegistrationConfirmationView,
    meta: { title: `${APP_NAME} - Registration Confirmation`, requiredAuth: 'None' },
  },
  {
    path: '/admin',
    name: 'admin',
    component: AdminView,
    children: [
      {
        path: 'users',
        name: 'admin-users',
        component: AdminUsersView,
        meta: { title: `${APP_NAME} - Manage Users`, requiredAuth: 'Admin' },
      },
      {
        path: 'institutions',
        name: 'admin-institutions',
        component: AdminInstitutionsView,
        meta: { title: `${APP_NAME} - Manage Institutions`, requiredAuth: 'Admin' },
      },
      {
        path: 'themes',
        name: 'admin-themes',
        component: AdminThemesView,
        meta: { title: `${APP_NAME} - Manage Theme`, requiredAuth: 'Admin' },
      },
      {
        path: 'activities',
        name: 'admin-activities',
        component: AdminActivitiesView,
        meta: { title: `${APP_NAME} - Manage Activities`, requiredAuth: 'Admin' },
      },
    ],
  },
  {
    path: '/member',
    name: 'member',
    component: MemberView,
    children: [
      {
        path: 'register',
        name: 'register-member',
        component: RegisterMemberView,
        meta: { title: `${APP_NAME} - Member Registration`, requiredAuth: 'None' },
      },
      {
        path: 'activities',
        name: 'institution-activities',
        component: InstitutionActivitiesView,
        meta: { title: `${APP_NAME} - Manage Activities - Member`, requiredAuth: 'None' },
      },
      {
        path: 'enrollments',
        name: 'activity-enrollments',
        component: InstitutionActivityEnrollmentsView,
        meta: { title: `${APP_NAME} - Manage Enrollments - Member`, requiredAuth: 'None' },
      },
      {
        path: 'assessments',
        name: 'institution-assessments',
        component: InstitutionAssessmentsView,
        meta: { title: `${APP_NAME} - Manage Assessments - Member`, requiredAuth: 'None' },
      },
      {
        path: 'themes',
        name: 'institution-themes',
        component: InstitutionThemeView,
        meta: { title: `${APP_NAME} - Theme`, requiredAuth: 'None' },
      },
    ],
  },
  {
    path: '/volunteer',
    name: 'volunteer',
    component: VolunteerView,
    children: [
      {
        path: 'activities',
        name: 'volunteer-activities',
        component: VolunteerActivitiesView,
        meta: { title: `${APP_NAME} - Manage Volunteer Activities`, requiredAuth: 'None' },
      },
      {
        path: 'enrollments',
        name: 'volunteer-enrollments',
        component: VolunteerEnrollmentsView,
        meta: { title: `${APP_NAME} - Manage Volunteer Enrollments`, requiredAuth: 'None' },
      },
      {
        path: 'assessments',
        name: 'volunteer-assessments',
        component: VolunteerAssessmentsView,
        meta: { title: `${APP_NAME} - Manage Volunteer Assessments`, requiredAuth: 'None' },
      },
    ],
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'not-found',
    component: NotFoundView,
    meta: { title: 'Page Not Found', requiredAuth: 'None' },
  },
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
});

// ✅ Route guards (adapted from Vuex to Pinia)
router.beforeEach((to, from, next) => {
  const store = useMainStore();

  const required = to.meta?.requiredAuth;

  if (required === 'None') {
    return next();
  }
  if (required === 'Admin' && store.isAdmin) {
    return next();
  }
  if (required === 'Member' && store.isMember) {
    return next();
  }
  if (required === 'Volunteer' && store.isVolunteer) {
    return next();
  }

  return next('/');
});

// ✅ Update page title & clear loading after navigation
router.afterEach(async (to) => {
  const store = useMainStore();
  document.title = to.meta?.title || APP_NAME;
  store.clearLoading?.(); // guard in case it's optional
});

export default router;
