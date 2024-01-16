<template>
  <v-card class="container" elevation="11">
    <h2>Member Registration</h2>
    <v-btn @click="readFile"> Download Document </v-btn>
    <v-form ref="form" lazy-validation>
      <v-text-field
        v-model="memberName"
        label="Name"
        required
        :rules="[(v) => !!v || 'Member name is required']"
      ></v-text-field>

      <v-text-field
        v-model="memberEmail"
        label="E-mail"
        required
        :rules="[(v) => !!v || 'Member email is required']"
      ></v-text-field>

      <v-text-field
        v-model="memberUsername"
        label="Username"
        required
        :rules="[(v) => !!v || 'Member username is required']"
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
            memberUsername !== '' &&
            memberEmail !== '' &&
            memberName !== '' &&
            memberDoc !== null
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
export default class RegisterMemberView extends Vue {
  memberName: string = '';
  memberEmail: string = '';
  memberUsername: string = '';
  memberDoc: File | null = null;

  async submit() {
    try {
      if (
        this.memberName.length == 0 ||
        this.memberEmail.length == 0 ||
        this.memberUsername.length == 0
      ) {
        await this.$store.dispatch(
          'error',
          'Missing information, please check the form again',
        );
      } else if (this.memberDoc != null) {
        await RemoteServices.registerMember(
          {
            memberName: this.memberName,
            memberEmail: this.memberEmail,
            memberUsername: this.memberUsername,
          },
          this.memberDoc,
        );
        await this.$router.push({ name: 'home' });
      }
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async handleFileUpload(event: File) {
    this.memberDoc = event;
  }

  readFile() {
    RemoteServices.getForm();
  }

  clear() {
    this.memberName = '';
    this.memberEmail = '';
    this.memberUsername = '';
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
