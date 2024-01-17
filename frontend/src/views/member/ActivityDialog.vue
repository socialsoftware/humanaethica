<template>
  <v-dialog v-model="dialog" persistent width="1024">
    <v-card>
      <v-card-title>
        <span class="headline">
          {{
            editActivity && editActivity.id === null
              ? 'New Activity'
              : 'Edit Activity'
          }}
        </span>
      </v-card-title>
      <v-card-text>
        <v-container>
          <v-row>
            <v-col cols="12" sm="6" md="4">
              <v-text-field
                label="Name"
                required
                v-model="editActivity.name"
              ></v-text-field>
            </v-col>
            <v-col cols="12">
              <v-text-field
                label="Region"
                required
                v-model="editActivity.region"
              ></v-text-field>
            </v-col>
            <v-col cols="12" sm="6">
              <v-select
                label="Themes"
                v-model="editActivity.themes"
                :items="themes"
                multiple
                return-object
                item-text="completeName"
                item-value="id"
                required
              />
            </v-col>
            <v-col cols="12">
              <v-text-field
                label="Description"
                required
                v-model="editActivity.description"
              ></v-text-field>
            </v-col>
            <div class="date-fields-container">
              <span class="text-h6">Starting Date</span>
              <div class="date-fields-row">
                <v-text-field
                  v-model="startDay"
                  label="Day"
                  data-cy="activitySDayInput"
                  :rules="[
                    (value) => !!value || 'Starting Date Day is required',
                  ]"
                  required
                />
                <v-text-field
                  v-model="startMonth"
                  label="Month"
                  data-cy="activitySMonthInput"
                  :rules="[
                    (value) => !!value || 'Starting Date Month is required',
                  ]"
                  required
                />
                <v-text-field
                  v-model="startYear"
                  label="Year"
                  data-cy="activitySYearInput"
                  :rules="[
                    (value) => !!value || 'Starting Date Year is required',
                  ]"
                  required
                />
                <v-text-field
                  v-model="startHour"
                  label="Hour"
                  data-cy="activitySHourInput"
                  :rules="[
                    (value) => !!value || 'Starting Date Hour is required',
                  ]"
                  required
                />
              </div>
            </div>
            <div class="date-fields-container">
              <span class="text-h6">Ending Date</span>
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
                  :rules="[
                    (value) => !!value || 'Ending Date Month is required',
                  ]"
                  required
                />
                <v-text-field
                  v-model="endYear"
                  label="Year"
                  data-cy="activityEYearInput"
                  :rules="[
                    (value) => !!value || 'Ending Date Year is required',
                  ]"
                  required
                />
                <v-text-field
                  v-model="endHour"
                  label="Hour"
                  data-cy="activityEHourInput"
                  :rules="[
                    (value) => !!value || 'Ending Date Hour is required',
                  ]"
                  required
                />
              </div>
            </div>
          </v-row>
        </v-container>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn
          color="blue-darken-1"
          variant="text"
          @click="$emit('close-activity-dialog')"
        >
          Close
        </v-btn>
        <v-btn color="blue-darken-1" variant="text" @click="updateActivity">
          Save
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
<script lang="ts">
import { Vue, Component, Prop, Model } from 'vue-property-decorator';
import Activity from '@/models/activity/Activity';
import Theme from '@/models/theme/Theme';
import RemoteServices from '@/services/RemoteServices';

@Component({})
export default class ActivityDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Activity, required: true }) readonly activity!: Activity;
  @Prop({ type: Array, required: true }) readonly themes!: Theme[];

  editActivity: Activity = new Activity();

  startDay: string = '';
  startMonth: string = '';
  startYear: string = '';
  startHour: string = '';
  endDay: string = '';
  endMonth: string = '';
  endYear: string = '';
  endHour: string = '';

  async created() {
    this.editActivity = new Activity(this.activity);
  }

  async updateActivity() {
    if (this.editActivity.id !== null) {
      try {
        this.editActivity.startingDate =
          this.startYear +
          '-' +
          this.startMonth +
          '-' +
          this.startDay +
          'T' +
          this.startHour +
          ':00:00Z';
        this.editActivity.endingDate =
          this.endYear +
          '-' +
          this.endMonth +
          '-' +
          this.endDay +
          'T' +
          this.endHour +
          ':00:00Z';
        const result = await RemoteServices.updateActivityAsMember(
          this.editActivity.id,
          this.editActivity,
        );
        this.$emit('save-activity', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>

<style scoped lang="scss"></style>
