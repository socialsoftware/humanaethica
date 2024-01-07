<template>
  <v-card class="container" elevation="11">
    <h2>Volunteer Registration</h2>

    <v-btn @click="readFile"> Download Document </v-btn>
    <v-form ref="form" lazy-validation>
      <v-text-field
        v-model="volunteerName"
        label="Name"
        required
        :rules="[(v) => !!v || 'Volunteer name is required']"
      ></v-text-field>

      <v-text-field
        v-model="volunteerEmail"
        label="E-mail"
        required
        :rules="[(v) => !!v || 'Volunteer email is required']"
      ></v-text-field>

      <v-text-field
        v-model="volunteerUsername"
        label="Username"
        required
        :rules="[(v) => !!v || 'Volunteer username is required']"
      ></v-text-field>

      <v-file-input
        counter
        show-size
        truncate-length="7"
        label="Declaration"
        required
        :rules="[(v) => !!v || 'Declaration is required']"
        dense
        small-chips
        accept=".pdf"
        @change="handleFileUpload($event)"
      ></v-file-input>

      <v-btn
        class="mr-4"
        color="orange"
        @click="submit"
        :disabled="
          !(
            volunteerUsername !== '' &&
            volunteerEmail !== '' &&
            volunteerName !== '' &&
            volunteerDoc !== null
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
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';

@Component({
  components: {},
})
export default class RegisterVolunteerView extends Vue {
  volunteerName: string = '';
  volunteerEmail: string = '';
  volunteerUsername: string = '';
  volunteerDoc: File | null = null;

  async submit() {
    try {
      if (
        this.volunteerName.length == 0 ||
        this.volunteerEmail.length == 0 ||
        this.volunteerUsername.length == 0
      ) {
        await this.$store.dispatch(
          'error',
          'Missing information, please check the form again',
        );
        return;
      } else if (this.volunteerDoc != null) {
        await RemoteServices.registerVolunteer(
          {
            volunteerName: this.volunteerName,
            volunteerEmail: this.volunteerEmail,
            volunteerUsername: this.volunteerUsername,
          },
          this.volunteerDoc,
        );
        await this.$router.push({ name: 'home' });
      }
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async handleFileUpload(event: File) {
    this.volunteerDoc = event;
  }

  readFile() {
    RemoteServices.getForm();
  }

  clear() {
    this.volunteerName = '';
    this.volunteerEmail = '';
    this.volunteerUsername = '';
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
