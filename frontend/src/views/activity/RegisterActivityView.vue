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
          label="Themes"
          multiple
          return-object
          item-text="name"
          item-value="name"
          required
        />
        <v-text-field
          v-model="activity.description"
          label="Description"
          data-cy="activityDescInput"
          :rules="[(value) => !!value || 'Description is required']"
          required
        />
        <div class="date-fields-container">
          <span class="headline">Starting Date</span>
          <div class="date-fields-row">
            <v-text-field
              v-model="startDay"
              label="Day"
              data-cy="activitySDayInput"
              :rules="[(value) => !!value || 'Starting Date Day is required']"
              required
            />
            <v-text-field
              v-model="startMonth"
              label="Month"
              data-cy="activitySMonthInput"
              :rules="[(value) => !!value || 'Starting Date Month is required']"
              required
            />
            <v-text-field
              v-model="startYear"
              label="Year"
              data-cy="activitySYearInput"
              :rules="[(value) => !!value || 'Starting Date Year is required']"
              required
            />
            <v-text-field
              v-model="startHour"
              label="Hour"
              data-cy="activitySHourInput"
              :rules="[(value) => !!value || 'Starting Date Hour is required']"
              required
            />
          </div>
        </div>
        <div class="date-fields-container">
          <span class="headline">Ending Date</span>
          <div class="date-fields-row">
            <v-text-field
              v-model="endDay"
              label="Day"
              data-cy="activityEDayInput"
              :rules="[(value) => !!value || 'Ending Date Day is required']"
              required
            />
            <v-text-field
              v-model="endMonth"
              label="Month"
              data-cy="activityEMonthInput"
              :rules="[(value) => !!value || 'Ending Date Month is required']"
              required
            />
            <v-text-field
              v-model="endYear"
              label="Year"
              data-cy="activityEYearInput"
              :rules="[(value) => !!value || 'Ending Date Year is required']"
              required
            />
            <v-text-field
              v-model="endHour"
              label="Hour"
              data-cy="activityEHourInput"
              :rules="[(value) => !!value || 'Ending Date Hour is required']"
              required
            />
          </div>
        </div>
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
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Activity from '@/models/activity/Activity';
import Theme from '@/models/theme/Theme';
import VueCtkDateTimePicker from 'vue-ctk-date-time-picker';

Vue.component('VueCtkDateTimePicker', VueCtkDateTimePicker);

@Component({
  components: {},
})
export default class RegisterActivityView extends Vue {
  activity: Activity = new Activity();
  themes: Theme[] | [] = [];
  startDay: string = '';
  startMonth: string = '';
  startYear: string = '';
  startHour: string = '';
  endDay: string = '';
  endMonth: string = '';
  endYear: string = '';
  endHour: string = '';

  async created() {
    this.themes = await RemoteServices.getThemesAvailable();
  }
  async submit() {
    try {
      this.activity.startingDate =
        this.startYear +
        '-' +
        this.startMonth +
        '-' +
        this.startDay +
        'T' +
        this.startHour +
        ':00:00Z';
      this.activity.endingDate =
        this.endYear +
        '-' +
        this.endMonth +
        '-' +
        this.endDay +
        'T' +
        this.endHour +
        ':00:00Z';
      await RemoteServices.registerActivity(
        this.$store.getters.getUser.id,
        this.activity,
      );
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

.date-fields-container {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.date-fields-row {
  display: flex;
  gap: 16px;
  margin-top: 8px;
}
</style>
