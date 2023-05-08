<template>
  <v-card>
    <v-form ref="form" lazy-validation>
      <v-card-title>
        <span class="headline">Create a new Activity</span>
      </v-card-title>
      <v-card-text class="text-left">
        <v-text-field
          v-model="activity.name"
          label="Name"
          data-cy="activityNameInput"
          :rules="[(value) => !!value || 'Name is required']"
          required
        />
        <v-text-field
          v-model="activity.region"
          label="Region"
          data-cy="activityRegionInput"
          :rules="[(value) => !!value || 'Region is required']"
          required
        />
        <v-select
          v-model="activity.themes"
          :items="themes"
          multiple
          return-object
          item-text="name"
          item-value="name"
          required
        />
      </v-card-text>
      <v-card-actions>
        <v-spacer />
        <v-btn color="blue darken-1" @click="clear" data-cy="clearButton"
          >Clear</v-btn
        >
        <v-btn color="blue darken-1" @click="submit" data-cy="submitButton"
          >Add</v-btn
        >
      </v-card-actions>
    </v-form>
  </v-card>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Activity from '@/models/activity/Activity';
import Theme from '@/models/theme/Theme';

@Component({
  components: {},
})
export default class RegisterActivityView extends Vue {
  activity: Activity = new Activity();
  themes: Theme[] | [] = [];
  async created() {
    this.themes = await RemoteServices.getThemes();
    console.log(this.themes.length);
    console.log(this.themes[0].name);
  }
  async submit() {
    console.log(this.activity.name);
    console.log(this.activity.themes[0].name);
    try {
      await RemoteServices.registerActivity(this.activity);
      await this.$router.push({ name: 'home' });
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  clear() {
    this.activity.name = '';
    this.activity.region = '';
    this.activity.themes = [];
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
