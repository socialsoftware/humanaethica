<template>
  <v-card class="container" elevation="11">
    <h2>Activity Registration</h2>
    <v-form ref="form" lazy-validation>
      <v-text-field
        v-model="activityName"
        label="Name"
        required
        :rules="[(v) => !!v || 'Activity Name is required']"
        @input="$v.activityName.$touch()"
        @blur="$v.activityName.$touch()"
      ></v-text-field>

      <v-text-field
        v-model="activityRegion"
        label="Region"
        required
        :rules="[(v) => !!v || 'Activity Region is required']"
        @input="$v.activityRegion.$touch()"
        @blur="$v.activityRegion.$touch()"
      ></v-text-field>

      <v-text-field
        v-model="activityTheme"
        label="Theme"
        required
        :rules="[(v) => !!v || 'Activity Theme is required']"
        @input="$v.activityTheme.$touch()"
        @blur="$v.activityTheme.$touch()"
      ></v-text-field>
      <v-btn
        class="mr-4"
        color="orange"
        @click="submit"
        :disabled="
          !(
            activityName !== '' &&
            activityRegion !== '' &&
            activityTheme !== ''
          )
        "
      >
        submit
      </v-btn>
      <v-btn @click="clear"> clear </v-btn>
    </v-form>
  </v-card>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import RegisterActivity from '@/models/activity/RegisterActivity';
import RemoteServices from '@/services/RemoteServices';

@Component({
  components: {},
})
export default class RegisterActivityView extends Vue {
  activityName: string = '';
  activityRegion: string = '';
  activityTheme: string = '';
  async submit() {
    try {
      if (
        this.activityName.length == 0 ||
        this.activityRegion.length == 0 ||
        this.activityTheme.length == 0
      ) {
        await this.$store.dispatch(
          'error',
          'Missing information, please check the form again'
        );
        return;
      } else {
        await RemoteServices.registerActivity({
          activityName: this.activityName,
          activityRegion: this.activityRegion,
          activityTheme: this.activityTheme,
        });
        await this.$router.push({ name: 'home' });
      }
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }
  readFile() {
    RemoteServices.getForm();
  }

  clear() {
    this.activityName = '';
    this.activityRegion = '';
    this.activityTheme = '';
  }
}
</script>

<style lang="scss" scoped>
.container {
  background-color: grey;
  margin-top: 2rem !important;
  padding: 3rem !important;
  width: 60%;
  flex-direction: row;
  flex-wrap: wrap;
  justify-content: center;
  align-items: stretch;
  align-content: center;
  background-color: rgba(255, 255, 255);
}

.divider {
  margin-top: 2rem !important;
  margin-bottom: 2rem !important;
}

h2 {
  color: black;
  opacity: 80%;
  font-family: 'Open Sans', sans-serif;
  text-align: left;
  font-size: 20px;
  font-weight: 500;
  line-height: 40px;
  margin: 0 0 16px;
}
</style>
