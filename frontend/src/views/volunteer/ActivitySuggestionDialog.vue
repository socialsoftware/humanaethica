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
                v-model="newActivitySuggestion.name"
                data-cy="suggestionNameInput"
              ></v-text-field>
            </v-col>
            <v-col cols="12" sm="6" md="4">
              <v-select
                label="*Institution"
                :rules="[(v) => !!v || 'Institution name is required']"
                v-model="newActivitySuggestion.institution"
                :items="institutions"
                return-object
                item-text="name"
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
                    } else if (v.replace(/\s+/g, ' ').trim().length < 10) {
                      return 'Description must be 10 or more characters';
                    } 
                    return true;
                  },
                ]"
                required
                v-model="newActivitySuggestion.description"
                data-cy="suggestionDescriptionInput"
              ></v-text-field>
            </v-col>
            <v-col cols="12">
              <v-text-field
                label="*Region"
                :rules="[(v) => !!v || 'Region name is required']"
                required
                v-model="newActivitySuggestion.region"
                data-cy="suggestionRegionInput"
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
                v-model="newActivitySuggestion.participantsNumberLimit"
                data-cy="suggestionParticipantsNumberInput"
              ></v-text-field>
            </v-col>
            <v-col>
              <VueCtkDateTimePicker
                id="suggestionApplicationDeadlineInput"
                v-model="newActivitySuggestion.applicationDeadline"
                format="YYYY-MM-DDTHH:mm:ssZ"
                label="*Application Deadline"
              ></VueCtkDateTimePicker>
            </v-col>
            <v-col>
              <VueCtkDateTimePicker
                id="suggestionStartingDateInput"
                v-model="newActivitySuggestion.startingDate"
                format="YYYY-MM-DDTHH:mm:ssZ"
                label="*Starting Date"
              ></VueCtkDateTimePicker>
            </v-col>
            <v-col>
              <VueCtkDateTimePicker
                id="suggestionEndingDateInput"
                v-model="newActivitySuggestion.endingDate"
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
          v-if="canSave"
          color="blue-darken-1"
          variant="text"
          @click="registerActivitySuggestion"
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
  @Prop({ type: Array, required: true }) readonly institutions!: Institution[];

  newActivitySuggestion: ActivitySuggestion = new ActivitySuggestion();
  cypressCondition: boolean = false;

  async created() {
    this.newActivitySuggestion = new ActivitySuggestion(this.activitySuggestion);
  }

  isNumberValid(value: any) {
    if (!/^\d+$/.test(value)) return false;
    const parsedValue = parseInt(value);
    return parsedValue >= 1 && parsedValue <= 5;
  }

  get canSave(): boolean {
    return (
      this.cypressCondition ||
      (!!this.newActivitySuggestion.name &&
        !!this.newActivitySuggestion.region &&
        !!this.newActivitySuggestion.institution &&
        !!this.newActivitySuggestion.participantsNumberLimit &&
        !!this.newActivitySuggestion.description &&
        this.newActivitySuggestion.description.length >= 10 &&
        !!this.newActivitySuggestion.startingDate &&
        !!this.newActivitySuggestion.endingDate &&
        !!this.newActivitySuggestion.applicationDeadline)
    );
  }

  async registerActivitySuggestion() {
    if (
      this.newActivitySuggestion.institution !== null &&
      this.newActivitySuggestion.institution.id !== null &&
      (this.$refs.form as Vue & { validate: () => boolean }).validate()
    ) {
      try {
        const result = await RemoteServices.registerActivitySuggestion(
          this.newActivitySuggestion,
          this.newActivitySuggestion.institution.id);
        this.$emit('save-activity-suggestion', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>

<style scoped lang="scss"></style>