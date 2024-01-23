<template>
  <nav>
    <v-app-bar clipped-left color="white">
      <v-app-bar-nav-icon
        aria-label="Menu"
        class="hidden-md-and-up"
        @click.stop="drawer = !drawer"
      />

      <v-toolbar-title data-cy="homeLink">
        <a href="/">
          <v-img
            contain
            src="../assets/img/logo_horizontal.png"
            height="40"
            width="350"
          />
        </a>
      </v-toolbar-title>

      <v-spacer />

      <v-menu v-if="isVolunteer" offset-y sopen-on-hover>
        <template v-slot:activator="{ on }">
          <v-btn
            text
            color="orange"
            v-on="on"
            data-cy="volunteerActivities"
            @click="manageActivities"
          >
            Activities
            <v-icon>fas fa-user</v-icon>
          </v-btn>
        </template>
      </v-menu>

      <v-menu v-if="isMember" offset-y open-on-hover>
        <template v-slot:activator="{ on }">
          <v-btn color="orange" text v-on="on" data-cy="institution">
            Institution
          </v-btn>
        </template>
        <v-list dense>
          <v-list-item to="/member/register" data-cy="members">
            <v-list-item-content>
              <v-list-item-title>Register Member</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
          <v-list-item to="/member/theme" data-cy="themes">
            <v-list-item-content>
              <v-list-item-title>Themes</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
          <v-list-item to="/member/manageActivities" data-cy="activities">
            <v-list-item-content>
              <v-list-item-title>Activities</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
        </v-list>
      </v-menu>

      <v-menu v-if="isAdmin" offset-y open-on-hover>
        <template v-slot:activator="{ on }">
          <v-btn color="orange" text v-on="on" data-cy="admin">
            Administration
          </v-btn>
        </template>
        <v-list dense>
          <v-list-item to="/admin/users" data-cy="adminUsers">
            <v-list-item-action>
              <v-icon>fas fa-users</v-icon>
            </v-list-item-action>
            <v-list-item-content>
              <v-list-item-title>Users</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
          <v-list-item to="/admin/institutions" data-cy="adminInstitutions">
            <v-list-item-action>
              <v-icon>fas fa-users</v-icon>
            </v-list-item-action>
            <v-list-item-content>
              <v-list-item-title>Institutions</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
          <v-list-item to="/admin/manageTheme" data-cy="themeManageTheme">
            <v-list-item-action>
              <v-icon>fas fa-users</v-icon>
            </v-list-item-action>
            <v-list-item-content>
              <v-list-item-title>Themes</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
          <v-list-item to="/admin/activities" data-cy="adminActivities">
            <v-list-item-action>
              <v-icon>fas fa-users</v-icon>
            </v-list-item-action>
            <v-list-item-content>
              <v-list-item-title>Activities</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
        </v-list>
      </v-menu>

      <v-toolbar-items class="hidden-sm-and-down" hide-details>
        <v-btn
          v-if="!isLoggedIn"
          text
          color="orange"
          @click="registerInstitution"
        >
          Register Institution
        </v-btn>
        <v-btn
          v-if="!isLoggedIn"
          text
          color="orange"
          @click="registerVolunteer"
        >
          Register Volunteer
        </v-btn>
        <v-btn v-if="!isLoggedIn" text color="orange" @click="login">
          Login
          <v-icon>fas fa-sign-in-alt</v-icon>
        </v-btn>

        <v-btn
          v-if="isLoggedIn"
          text
          color="orange"
          data-cy="logoutButton"
          @click="logout"
        >
          Logout
          <v-icon>fas fa-sign-out-alt</v-icon>
        </v-btn>
      </v-toolbar-items>
    </v-app-bar>

    <!-- Start of mobile side menu -->
    <v-navigation-drawer v-model="drawer" absolute app dark temporary>
      <v-toolbar flat>
        <v-list>
          <v-list-item>
            <v-list-item-title class="title">Menu</v-list-item-title>
          </v-list-item>
        </v-list>
      </v-toolbar>

      <v-list class="pt-0" dense>
        <!-- Member Group-->
        <v-list-group
          v-if="isMember"
          :value="false"
          prepend-icon="account_circle"
        >
          <template v-slot:activator>
            <v-list-item-title>Member</v-list-item-title>
          </template>
        </v-list-group>

        <!-- Volunteer Group-->
        <v-list-group
          v-if="isVolunteer"
          :value="false"
          prepend-icon="account_circle"
        >
          <template v-slot:activator>
            <v-list-item-title>Volunteer</v-list-item-title>
          </template>
        </v-list-group>

        <!-- Administration Group-->
        <v-list-group
          v-if="isAdmin"
          :value="false"
          prepend-icon="fas fa-file-alt"
        >
          <template v-slot:activator>
            <v-list-item-title>Administration</v-list-item-title>
          </template>
          <v-list-item to="/admin/users">
            <v-list-item-action>
              <v-icon>fas fa-users</v-icon>
            </v-list-item-action>
            <v-list-item-content>
              <v-list-item-title>Manage Users</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
          <v-list-item to="/admin/institutions">
            <v-list-item-action>
              <v-icon>fas fa-users</v-icon>
            </v-list-item-action>
            <v-list-item-content>
              <v-list-item-title>Manage Institutions</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
          <v-list-item to="/admin/members" data-cy="adminMembers">
            <v-list-item-action>
              <v-icon>fas fa-users</v-icon>
            </v-list-item-action>
            <v-list-item-content>
              <v-list-item-title>Manage Members</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
        </v-list-group>
      </v-list>

      <v-list-item v-if="!isLoggedIn" @click="login">
        <v-list-item-action>
          <v-icon>fas fa-sign-out-alt</v-icon>
        </v-list-item-action>
        <v-list-item-content>Login</v-list-item-content>
      </v-list-item>
      <v-list-item v-if="isLoggedIn" @click="logout">
        <v-list-item-action>
          <v-icon>fas fa-sign-out-alt</v-icon>
        </v-list-item-action>
        <v-list-item-content>Logout</v-list-item-content>
      </v-list-item>
    </v-navigation-drawer>
    <!-- End of mobile side menu -->
  </nav>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';

@Component
export default class TopBar extends Vue {
  appName: string = process.env.VUE_APP_NAME || 'ENV FILE MISSING';
  drawer: boolean = false;

  get isLoggedIn() {
    return this.$store.getters.isLoggedIn;
  }

  get isMember() {
    return this.$store.getters.isMember;
  }

  get isAdmin() {
    return this.$store.getters.isAdmin;
  }

  get isVolunteer() {
    return this.$store.getters.isVolunteer;
  }

  async login() {
    await this.$router.push({ name: 'login-user' }).catch(() => {});
  }

  async registerInstitution() {
    await this.$router.push({ name: 'register-institution' }).catch(() => {});
  }

  async registerVolunteer() {
    await this.$router.push({ name: 'register-volunteer' }).catch(() => {});
  }

  async manageActivities() {
    await this.$router.push({ name: 'manage-activities' }).catch(() => {});
  }

  async logout() {
    await this.$store.dispatch('logout');
    await this.$router.push({ name: 'home' }).catch(() => {});
  }
}
</script>

<style lang="scss" scoped>
.no-active::before {
  opacity: 0 !important;
}

nav {
  z-index: 400;
}
</style>
