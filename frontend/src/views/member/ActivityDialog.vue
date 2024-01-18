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
            <v-col>
              <VueCtkDateTimePicker
                id="startingDateInput"
                v-model="editActivity.startingDate"
                format="YYYY-MM-DDTHH:mm:ssZ"
                label="*Starting Date"
              ></VueCtkDateTimePicker>
            </v-col>
            <v-col>
              <VueCtkDateTimePicker
                id="endingDateInput"
                v-model="editActivity.endingDate"
                format="YYYY-MM-DDTHH:mm:ssZ"
                label="*Ending Date"
              ></VueCtkDateTimePicker>
            </v-col>
            <v-col>
              <VueCtkDateTimePicker
                id="ApplicationDeadlineInput"
                v-model="editActivity.applicationDeadline"
                format="YYYY-MM-DDTHH:mm:ssZ"
                label="*Application Deadline"
              ></VueCtkDateTimePicker>
            </v-col>
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
import VueCtkDateTimePicker from 'vue-ctk-date-time-picker';
import 'vue-ctk-date-time-picker/dist/vue-ctk-date-time-picker.css';
import { ISOtoString } from '@/services/ConvertDateService';

Vue.component('VueCtkDateTimePicker', VueCtkDateTimePicker);
@Component({
  methods: { ISOtoString },
})
export default class ActivityDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Activity, required: true }) readonly activity!: Activity;
  @Prop({ type: Array, required: true }) readonly themes!: Theme[];

  editActivity: Activity = new Activity();

  async created() {
    this.editActivity = new Activity(this.activity);
  }

  async updateActivity() {
    try {
      console.log(this.editActivity);
      const result =
        this.editActivity.id !== null
          ? await RemoteServices.updateActivityAsMember(
              this.editActivity.id,
              this.editActivity,
            )
          : await RemoteServices.registerActivity(
              this.$store.getters.getUser.id,
              this.editActivity,
            );
      this.$emit('save-activity', result);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }
}
</script>

<style scoped lang="scss"></style>
