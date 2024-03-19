<template>
  <v-dialog v-model="dialog" persistent width="1300">
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
        <v-form ref="form" lazy-validation>
          <v-row>
            <v-col cols="12" sm="6" md="4">
              <v-text-field
                label="*Name"
                :rules="[(v) => !!v || 'Activity name is required']"
                required
                v-model="editActivity.name"
                data-cy="nameInput"
              ></v-text-field>
            </v-col>
            <v-col cols="12">
              <v-text-field
                label="*Region"
                :rules="[(v) => !!v || 'Region name is required']"
                required
                v-model="editActivity.region"
                data-cy="regionInput"
              ></v-text-field>
            </v-col>
            <v-col cols="12" sm="6" md="4">
              <v-text-field
                label="*Number of Participants"
                :rules="[
                  (v) =>
                    isNumberValid(v) ||
                    'Number of participants between 1 and 5 is required',
                ]"
                required
                v-model="editActivity.participantsNumberLimit"
                data-cy="participantsNumberInput"
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
                label="*Description"
                :rules="[(v) => !!v || 'Description is required']"
                required
                v-model="editActivity.description"
                data-cy="descriptionInput"
              ></v-text-field>
            </v-col>
            <v-col>
              <VueCtkDateTimePicker
                id="applicationDeadlineInput"
                v-model="editActivity.applicationDeadline"
                format="YYYY-MM-DDTHH:mm:ssZ"
                label="*Application Deadline"
              ></VueCtkDateTimePicker>
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
          </v-row>
        </v-form>
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
        <v-btn
          color="blue-darken-1"
          variant="text"
          @click="updateActivity"
          data-cy="saveActivity"
        >
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

  cypressCondition: boolean = false;

  async created() {
    this.editActivity = new Activity(this.activity);
  }

  isNumberValid(value: any) {
    if (!/^\d+$/.test(value)) return false;
    const parsedValue = parseInt(value);
    return parsedValue >= 1 && parsedValue <= 5;
  }

  get canSave(): boolean {
    return (
      this.cypressCondition ||
      (!!this.editActivity.name &&
        !!this.editActivity.region &&
        !!this.editActivity.participantsNumberLimit &&
        !!this.editActivity.description &&
        !!this.editActivity.startingDate &&
        !!this.editActivity.endingDate &&
        !!this.editActivity.applicationDeadline)
    );
  }

  async updateActivity() {
    if ((this.$refs.form as Vue & { validate: () => boolean }).validate()) {
      try {
        const result =
          this.editActivity.id !== null
            ? await RemoteServices.updateActivityAsMember(
                this.editActivity.id,
                this.editActivity,
              )
            : await RemoteServices.registerActivity(this.editActivity);
        this.$emit('save-activity', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>

<style scoped lang="scss"></style>
