<template>
  <v-card class="container" elevation="11">
    <h2>Institution Registration</h2>
    <v-form ref="form" lazy-validation>
      <v-text-field
        v-model="institutionName"
        label="Name"
        required
        :rules="[(v) => !!v || 'Institution name is required']"
      ></v-text-field>

      <v-text-field
        v-model="institutionEmail"
        label="E-mail"
        :rules="[(v) => !!v || 'Institution email is required']"
        required
      ></v-text-field>

      <v-text-field
        v-model="institutionNif"
        label="NIF"
        required
        :rules="[(v) => !!v || 'Institution NIF is required']"
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
        @change="handleInstitutionFileUpload($event)"
      ></v-file-input>

      <v-divider class="divider"></v-divider>

      <h2>Member Registration</h2>
      <v-btn @click="readFile"> Download Document </v-btn>

      <v-text-field
        v-model="memberUsername"
        :counter="10"
        label="Username"
        required
        :rules="[(v) => !!v || 'Member username is required']"
      ></v-text-field>

      <v-text-field
        v-model="memberEmail"
        label="E-mail"
        required
        :rules="[(v) => !!v || 'Member email is required']"
      ></v-text-field>

      <v-text-field
        v-model="memberName"
        label="Name"
        required
        :rules="[(v) => !!v || 'Member name is required']"
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
        @change="handleMemberFileUpload($event)"
      ></v-file-input>

      <v-btn
        class="mr-4"
        color="orange"
        @click="submit"
        :disabled="
          !(institutionName !== '' &&
            institutionEmail !== '' &&
            institutionNif !== '' &&
            institutionDoc !== null &&
            memberUsername !== '' &&
            memberEmail !== '' &&
            memberName !== '',
          memberDoc !== null)
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
export default class RegisterInstitutionView extends Vue {
  institutionName: string = '';
  institutionEmail: string = '';
  institutionNif: string = '';
  memberUsername: string = '';
  memberEmail: string = '';
  memberName: string = '';
  institutionDoc: File | null = null;
  memberDoc: File | null = null;

  async submit() {
    try {
      if (
        this.institutionName.length == 0 ||
        this.institutionEmail.length == 0 ||
        this.institutionNif.length == 0 ||
        this.memberUsername.length == 0 ||
        this.memberEmail.length == 0 ||
        this.memberName.length == 0
      ) {
        await this.$store.dispatch(
          'error',
          'Missing information, please check the form again',
        );
        return;
      } else if (this.institutionDoc != null && this.memberDoc != null) {
        await RemoteServices.registerInstitution(
          {
            institutionName: this.institutionName,
            institutionEmail: this.institutionEmail,
            institutionNif: this.institutionNif,
            memberUsername: this.memberUsername,
            memberEmail: this.memberEmail,
            memberName: this.memberName,
          },
          this.institutionDoc,
          this.memberDoc,
        );
        await this.$router.push({ name: 'home' });
      }
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async handleMemberFileUpload(event: File) {
    this.memberDoc = event;
  }

  async handleInstitutionFileUpload(event: File) {
    this.institutionDoc = event;
  }

  readFile() {
    RemoteServices.getForm();
  }

  clear() {
    this.institutionName = '';
    this.institutionEmail = '';
    this.institutionNif = '';
    this.memberUsername = '';
    this.memberEmail = '';
    this.memberName = '';
  }
}
</script>

<style lang="scss" scoped>
.container {
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
