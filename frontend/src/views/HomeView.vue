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

    <div class="horizontal-btn-container" v-if="!isLoggedIn">
      <v-btn
        href="./login/user"
        depressed
        color="blue accent-1"
        data-cy="userLoginButton"
      >
        User Login <v-icon>fas fa-sign-in-alt</v-icon>
      </v-btn>
    </div>

    <div class="horizontal-btn-container" v-if="!isLoggedIn">
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

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import Store from '@/store';

@Component
export default class HomeView extends Vue {
  appName: string = process.env.VUE_APP_NAME || 'ENV FILE MISSING';

  get isLoggedIn() {
    return Store.state.token;
  }

  async demoVolunteer() {
    await this.$store.dispatch('loading');
    try {
      await this.$store.dispatch('demoVolunteerLogin');
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async demoMember() {
    await this.$store.dispatch('loading');
    try {
      await this.$store.dispatch('demoMemberLogin');
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async loginAdmin() {
    await this.$store.dispatch('loading');
    try {
      await this.$store.dispatch('adminLogin');
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }
}
</script>

<style lang="scss" scoped>
.container {
  height: 100%;
  display: flex;
  flex-direction: column;
  flex-wrap: nowrap;
  justify-content: center;
  align-items: center;

  #home-title {
    background-image: url('img/horizontal.jpg');
    box-sizing: border-box;
    color: rgb(255, 255, 255);
    min-height: auto;
    min-width: auto;
    text-align: center;
    text-decoration: none solid rgb(255, 255, 255);
    text-rendering: optimizelegibility;
    text-size-adjust: 100%;
    column-rule-color: rgb(255, 255, 255);
    perspective-origin: 229.922px 34px;
    transform-origin: 229.922px 34px;
    caret-color: rgb(255, 255, 255);
    border: 0 none rgb(255, 255, 255);
    font:
      normal normal 100 normal 45px / 48px Roboto,
      sans-serif !important;
    margin-bottom: 70px !important;
    outline: rgb(255, 255, 255) none 0;
    padding: 10px 20px;
  }

  .horizontal-btn-container {
    margin-top: 40px;
    padding-bottom: 30px;

    button,
    a {
      margin: 0 10px;
    }
  }

  .footer {
    background-color: rgba(0, 0, 0, 0) !important;
    display: flex; /* or inline-flex */
    flex-direction: row;
    flex-wrap: wrap;
    justify-content: space-between;
    align-items: center;
    width: 100%;
    max-height: 100px;
    position: absolute;
    bottom: 0;
    overflow: hidden;

    .logo {
      flex-shrink: 1;
      width: 20%;
      max-width: 200px;
      min-width: 100px;
      padding: 2%;
    }
  }
}
</style>
