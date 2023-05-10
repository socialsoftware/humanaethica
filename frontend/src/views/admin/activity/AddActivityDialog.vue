<template v-if="activity">
  <v-dialog
    :value="dialog"
    @input="$emit('close-dialog')"
    @keydown.esc="$emit('close-dialog')"
    max-width="75%"
    max-height="80%"
  >
    <v-card>
      <v-form ref="form" v-model="valid" lazy-validation>
        <v-card-title>
          <span class="headline">Add Activity</span>
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
            data-cy="userUsernameInput"
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
            data-cy="activityThemeSelect"
            label="Themes"
          ></v-select>
          <div class="add-user-feedback-container">
            <span class="add-user-feedback" v-if="success"
              >{{ activity.themes }} {{ activity.name }} added</span
            >
          </div>
        </v-card-text>

        <v-card-actions>
          <v-spacer />
          <v-btn
            color="blue darken-1"
            @click="$emit('close-dialog')"
            data-cy="cancelButton"
            >Close</v-btn
          >
          <v-btn color="blue darken-1" @click="addActivity" data-cy="saveButton"
            >Add</v-btn
          >
        </v-card-actions>
      </v-form>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Activity from '@/models/activity/Activity';
import Theme from '@/models/theme/Theme';

@Component
export default class AddActivityDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;

  themes: Theme[] | [] = [];
  activity: Activity = new Activity();
  valid = true;
  success = false;

  async created() {
    this.activity = new Activity();
    this.themes = await RemoteServices.getThemes();
  }

  async addActivity() {
    let activity: Activity;
    this.success = false;

    if (!(this.$refs.form as Vue & { validate: () => boolean }).validate())
      return;

    try {
      activity = await RemoteServices.registerActivity(this.activity);
      this.$emit('activity-created', activity);
      this.success = true;
      await this.$router.push({ name: 'home' });
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }
}
</script>

<style scoped>
.add-user-feedback-container {
  height: 25px;
}
.add-user-feedback {
  font-size: 1.05rem;
  color: #1b5e20;
  text-transform: uppercase;
}
</style>
