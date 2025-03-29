<template>
  <v-dialog v-model="dialog" persistent width="1300">
    <v-card>
      <v-card-title>
        <span class="headline">New Activity Suggestion</span>
      </v-card-title>
      <v-card-text>
        <v-form ref="form" lazy-validation>
          <v-row>
            <v-col cols="12" sm="6" md="4">
              <v-text-field
                label="*Name"
                :rules="[(v) => !!v || 'Activity Suggestion name is required']"
                required
                v-model="editActivitySuggestion.name"
                data-cy="nameInput"
              ></v-text-field>
            </v-col>
            <v-col cols="12" sm="6" md="4">
              <v-select
                label="Institution"
                v-model="this.editActivitySuggestion.institutionId"
                :items="[]"
                return-object
                item-text="completeName"
                item-value="id"
                required
              />
            </v-col>
            <v-col cols="12">
              <v-text-field
                label="*Description"
                :rules="[
                  (v) => {
                    if (!v) {
                      return 'Description is required';
                    } else if (v.length <= 10) {
                      return 'Description must be 10 or more characters';
                    } 
                    return true;
                  },
                ]"
                required
                v-model="editActivitySuggestion.description"
                data-cy="descriptionInput"
              ></v-text-field>
            </v-col>
            <v-col cols="12">
              <v-text-field
                label="*Region"
                :rules="[(v) => !!v || 'Region name is required']"
                required
                v-model="editActivitySuggestion.region"
                data-cy="regionInput"
              ></v-text-field>
            </v-col>
            <v-col cols="12">
              <v-text-field
                label="*Number of Participants"
                :rules="[
                  (v) =>
                    isNumberValid(v) ||
                    'Number of participants between 1 and 5 is required',
                ]"
                required
                v-model="editActivitySuggestion.participantsNumberLimit"
                data-cy="participantsNumberInput"
              ></v-text-field>
            </v-col>
            <v-col>
              <VueCtkDateTimePicker
                id="applicationDeadlineInput"
                v-model="editActivitySuggestion.applicationDeadline"
                format="YYYY-MM-DDTHH:mm:ssZ"
                label="*Application Deadline"
              ></VueCtkDateTimePicker>
            </v-col>
            <v-col>
              <VueCtkDateTimePicker
                id="startingDateInput"
                v-model="editActivitySuggestion.startingDate"
                format="YYYY-MM-DDTHH:mm:ssZ"
                label="*Starting Date"
              ></VueCtkDateTimePicker>
            </v-col>
            <v-col>
              <VueCtkDateTimePicker
                id="endingDateInput"
                v-model="editActivitySuggestion.endingDate"
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
          @click="$emit('close-activity-suggestion-dialog')"
        >
          Close
        </v-btn>
        <v-btn
          color="blue-darken-1"
          variant="text"
          @click="createActivitySuggestion"
          data-cy="saveActivitySuggestion"
        >
          Save
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
<script lang="ts">
import { Vue, Component, Prop, Model } from 'vue-property-decorator';
import ActivitySuggestion from '@/models/activitysuggestion/ActivitySuggestion';
import RemoteServices from '@/services/RemoteServices';
import VueCtkDateTimePicker from 'vue-ctk-date-time-picker';
import 'vue-ctk-date-time-picker/dist/vue-ctk-date-time-picker.css';
import { ISOtoString } from '@/services/ConvertDateService';
import Institution from '@/models/institution/Institution';

Vue.component('VueCtkDateTimePicker', VueCtkDateTimePicker);
@Component({
  methods: { ISOtoString },
})
export default class ActivitySuggestionDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: ActivitySuggestion, required: true }) readonly activitySuggestion!: ActivitySuggestion;
  @Prop({ type: Institution, required: true }) readonly institution!: Institution;

  // <!-- TODO lista de instituins como? -->
  // será necessário esta institution? se calhar vale mais só a pena receber uma lista de institutions do pai?
  // TODO falta fazer aquilo do botão save só aparecer quando deve

  editActivitySuggestion: ActivitySuggestion = new ActivitySuggestion();

  cypressCondition: boolean = false;

  async created() {
    this.editActivitySuggestion = new ActivitySuggestion(this.activitySuggestion);
  }

  isNumberValid(value: any) {
    if (!/^\d+$/.test(value)) return false;
    const parsedValue = parseInt(value);
    return parsedValue >= 1 && parsedValue <= 5;
  }

  get canSave(): boolean {
    return (
      this.cypressCondition ||
      (!!this.activitySuggestion.name &&
        !!this.activitySuggestion.region &&
        !!this.activitySuggestion.participantsNumberLimit &&
        !!this.activitySuggestion.description &&
        this.activitySuggestion.description.length >= 10 &&
        !!this.activitySuggestion.startingDate &&
        !!this.activitySuggestion.endingDate &&
        !!this.activitySuggestion.applicationDeadline)
    );
  }

  // created e createActivitySuggestion? TOASK

  async createActivitySuggestion() {
    if (
      this.editActivitySuggestion.institutionId !== null &&
      (this.$refs.form as Vue & { validate: () => boolean }).validate()
    ) {
      try {
        const result = await RemoteServices.registerActivitySuggestion(
          this.editActivitySuggestion,
          this.editActivitySuggestion.institutionId);
        this.$emit('save-activity-suggestion', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>

<style scoped lang="scss"></style>