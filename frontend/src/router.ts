import Vue from 'vue';
import Router from 'vue-router';
import Store from '@/store';

import RegistrationConfirmationView from '@/views/user/RegistrationConfirmationView.vue';

import HomeView from '@/views/HomeView.vue';

import NotFoundView from '@/views/NotFoundView.vue';

import LoginView from '@/views/user/LoginView.vue';
import UsersView from '@/views/admin/users/UsersView.vue';
import InstitutionsView from '@/views/admin/institutions/InstitutionsView.vue';
import ThemesView from '@/views/admin/manageTheme/ThemesView.vue';
import AdminView from '@/views/admin/AdminView.vue';
import RegisterInstitutionView from '@/views/institution/RegisterInstitutionView.vue';
import RegisterVolunteerView from '@/views/volunteer/RegisterVolunteerView.vue';
import RegisterMemberView from '@/views/member/RegisterMemberView.vue';
import RegisterActivityView from '@/views/activity/RegisterActivityView.vue';
import ActivitiesView from '@/views/admin/activity/ActivitiesView.vue';

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
      path: '/registration/confirmation',
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
          name: 'usersAdmin',
          component: UsersView,
          meta: {
            title: APP_NAME + ' - Manage Users',
            requiredAuth: 'Admin',
          },
        },
      ],
    },
    {
      path: '/admin',
      name: 'admin',
      component: AdminView,
      children: [
        {
          path: 'institutions',
          name: 'institutionsAdmin',
          component: InstitutionsView,
          meta: {
            title: APP_NAME + ' - Manage Institutions',
            requiredAuth: 'Admin',
          },
        },
      ],
    },
    {
      path: '/admin',
      name: 'admin',
      component: AdminView,
      children: [
        {
          path: 'manageTheme',
          name: 'manageThemeAdmin',
          component: ThemesView,
          meta: {
            title: APP_NAME + ' - Manage Theme',
            requiredAuth: 'Admin',
          },
        },
      ],
    },
    {
      path: '/admin',
      name: 'admin',
      component: AdminView,
      children: [
        {
          path: 'activities',
          name: 'activitiesAdmin',
          component: ActivitiesView,
          meta: {
            title: APP_NAME + ' - Manage Activities',
            requiredAuth: 'Admin',
          },
        },
      ],
    },
    {
      path: '/institution/register',
      name: 'register-institution',
      component: RegisterInstitutionView,
      meta: {
        requiredAuth: 'None',
        title: APP_NAME + ' - Institution Registration',
      },
    },
    {
      path: '/volunteer/register',
      name: 'register-volunteer',
      component: RegisterVolunteerView,
      meta: {
        requiredAuth: 'None',
        title: APP_NAME + ' - Volunteer Registration',
      },
    },
    {
      path: '/activity/register',
      name: 'register-activity',
      component: RegisterActivityView,
      meta: {
        requiredAuth: 'None',
        title: APP_NAME + ' - Activity Registration',
      },
    },
    {
      path: '/member/register',
      name: 'register-member',
      component: RegisterMemberView,
      meta: {
        requiredAuth: 'None',
        title: APP_NAME + ' - Member Registration',
      },
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

router.afterEach(async (to, from) => {
  document.title = to.meta?.title;
  await Store.dispatch('clearLoading');
});

export default router;
