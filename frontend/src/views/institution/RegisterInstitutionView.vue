<template>
  <div class="container">

    <h2>Institution Registration</h2>
    <v-form ref="form" lazy-validation>
      <v-text-field
        v-model="institutionName"
        label="Name"
        required
        @input="$v.institutionName.$touch()"
        @blur="$v.institutionName.$touch()"
      ></v-text-field>

      <v-text-field
        v-model="institutionEmail"
        label="E-mail"
        required
        @input="$v.institutionEmail.$touch()"
        @blur="$v.institutionEmail.$touch()"
      ></v-text-field>

      <v-text-field
        v-model="institutionNif"
        label="NIF"
        required
        @input="$v.institutionNif.$touch()"
        @blur="$v.institutionNif.$touch()"
      ></v-text-field>

      <v-file-input
        counter
        show-size
        truncate-length="7"
        label="Declaration"
        required
        dense
        small-chips
        @change="handleFileUpload($event)"
      ></v-file-input>

      <v-divider class="divider"></v-divider>

      <h2>Member Registration</h2>

      <v-text-field
        v-model="memberUsername"
        :counter="10"
        label="Username"
        required
        @input="$v.memberUsername.$touch()"
        @blur="$v.memberUsername.$touch()"
      ></v-text-field>

      <v-text-field
        v-model="memberEmail"
        label="E-mail"
        required
        @input="$v.memberEmail.$touch()"
        @blur="$v.memberEmail.$touch()"
      ></v-text-field>

      <v-text-field
        v-model="memberName"
        label="Name"
        required
        @input="$v.memberName.$touch()"
        @blur="$v.memberName.$touch()"
      ></v-text-field>

      <v-btn class="mr-4" @click="submit"> submit </v-btn>
      <v-btn @click="clear"> clear </v-btn>
    </v-form>
  </div>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import RegisterInstitution from '@/models/institution/RegisterInstitution';
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
  memberPassword: string = '';
  institutionDoc: File | null = null;

  async submit() {
    await this.$store.dispatch('loading');

    try {
      if (this.institutionDoc != null) {
        await RemoteServices.registerInstitution({
          institutionName: this.institutionName,
          institutionEmail: this.institutionEmail,
          institutionNif: this.institutionNif,
          memberUsername: this.memberUsername,
          memberEmail: this.memberEmail,
          memberName: this.memberName,
        }, this.institutionDoc);
      }
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async handleFileUpload(event: File) {
    this.institutionDoc = event;
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

h2{
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
