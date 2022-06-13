<template>
  <div class="container">
    <v-form ref="form" lazy-validation>
      <v-text-field
        v-model="name"
        :counter="10"
        label="Name"
        required
        @input="$v.name.$touch()"
        @blur="$v.name.$touch()"
      ></v-text-field>

      <v-text-field
        v-model="email"
        label="E-mail"
        required
        @input="$v.email.$touch()"
        @blur="$v.email.$touch()"
      ></v-text-field>

      <v-text-field
        v-model="nif"
        label="NIF"
        required
        @input="$v.nif.$touch()"
        @blur="$v.nif.$touch()"
      ></v-text-field>

      <v-file-input
        counter
        multiple
        show-size
        truncate-length="7"
      ></v-file-input>

      <v-btn class="mr-4" @click="submit"> submit </v-btn>
      <v-btn @click="clear"> clear </v-btn>
    </v-form>
  </div>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import RegisterUser from '@/models/institution/Institution';
import Institution from '@/models/institution/Institution';
import RemoteServices from '@/services/RemoteServices';

@Component({
  components: {},
})
export default class RegisterInstitutionView extends Vue {
  name: string = '';
  email: string = '';
  nif: string = '';
  async submit() {
    await this.$store.dispatch('loading');
    try {
      let institution: Institution = await RemoteServices.registerInstitution({
        name: this.name,
        email: this.email,
        nif: this.nif,
        id: 1,
        valid: true,
      });
      await this.$store.dispatch('institution', institution);
      await this.$router.push({
        name: 'solve-quiz',
      });
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  clear() {
    this.name = '';
    this.email = '';
    this.nif = '';
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
</style>
