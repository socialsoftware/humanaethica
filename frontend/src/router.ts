import Vue from 'vue';
import Router from 'vue-router';
import Store from '@/store';

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

Vue.use(Router);

const APP_NAME = process.env.VUE_APP_NAME || '';

const router = new Router({
  mode: 'history',
  base: process.env.BASE_URL,
  routes: [
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
      meta: {
        requiredAuth: 'None',
        title: APP_NAME + ' - Login External',
      },
    },
    {
      path: '/register/institution',
      name: 'register-institution',
      component: RegisterInstitutionView,
      meta: {
        requiredAuth: 'None',
        title: APP_NAME + ' - Institution Registration',
      },
    },
    {
      path: '/register/volunteer',
      name: 'register-volunteer',
      component: RegisterVolunteerView,
      meta: {
        requiredAuth: 'None',
        title: APP_NAME + ' - Volunteer Registration',
      },
    },
    {
      path: '/register/confirmation',
      name: 'registration-confirmation',
      component: RegistrationConfirmationView,
      meta: {
        title: APP_NAME + ' - Registration Confirmation',
        requiredAuth: 'None',
      },
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
          meta: {
            title: APP_NAME + ' - Manage Users',
            requiredAuth: 'Admin',
          },
        },
        {
          path: 'institutions',
          name: 'admin-institutions',
          component: AdminInstitutionsView,
          meta: {
            title: APP_NAME + ' - Manage Institutions',
            requiredAuth: 'Admin',
          },
        },
        {
          path: 'themes',
          name: 'admin-themes',
          component: AdminThemesView,
          meta: {
            title: APP_NAME + ' - Manage Theme',
            requiredAuth: 'Admin',
          },
        },
        {
          path: 'activities',
          name: 'admin-activities',
          component: AdminActivitiesView,
          meta: {
            title: APP_NAME + ' - Manage Activities',
            requiredAuth: 'Admin',
          },
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
          meta: {
            requiredAuth: 'None',
            title: APP_NAME + ' - Member Registration',
          },
        },
        {
          path: 'activities',
          name: 'institution-activities',
          component: InstitutionActivitiesView,
          meta: {
            requiredAuth: 'None',
            title: APP_NAME + ' - Manage Activities - Member',
          },
        },
        {
          path: 'enrollments',
          name: 'activity-enrollments',
          component: InstitutionActivityEnrollmentsView,
          meta: {
            requiredAuth: 'None',
            title: APP_NAME + ' - Manage Enrollments - Member',
          },
        },
        {
          path: 'assessments',
          name: 'institution-assessments',
          component: InstitutionAssessmentsView,
          meta: {
            requiredAuth: 'None',
            title: APP_NAME + ' - Manage Assessments - Member',
          },
        },
        {
          path: 'themes',
          name: 'institution-themes',
          component: InstitutionThemeView,
          meta: {
            requiredAuth: 'None',
            title: APP_NAME + ' - Theme',
          },
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
          meta: {
            requiredAuth: 'None',
            title: APP_NAME + ' - Manage Volunteer Activities',
          },
        },
        {
          path: 'enrollments',
          name: 'volunteer-enrollments',
          component: VolunteerEnrollmentsView,
          meta: {
            requiredAuth: 'None',
            title: APP_NAME + ' - Manage Volunteer Enrollments',
          },
        },
        {
          path: 'assessments',
          name: 'volunteer-assessments',
          component: VolunteerAssessmentsView,
          meta: {
            requiredAuth: 'None',
            title: APP_NAME + ' - Manage Volunteer Assessments',
          },
        },
      ],
    },
    {
      path: '**',
      name: 'not-found',
      component: NotFoundView,
      meta: { title: 'Page Not Found', requiredAuth: 'None' },
    },
  ],
});

router.beforeEach(async (to, from, next) => {
  if (to.meta?.requiredAuth == 'None') {
    next();
  } else if (to.meta?.requiredAuth == 'Admin' && Store.getters.isAdmin) {
    next();
  } else if (to.meta?.requiredAuth == 'Volunteer' && Store.getters.isMember) {
    next();
  } else if (to.meta?.requiredAuth == 'Member' && Store.getters.isVolunteer) {
    next();
  } else {
    next('/');
  }
});

router.afterEach(async (to) => {
  document.title = to.meta?.title;
  await Store.dispatch('clearLoading');
});

export default router;
