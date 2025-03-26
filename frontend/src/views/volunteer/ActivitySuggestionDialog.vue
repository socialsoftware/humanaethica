<template>
  <v-dialog v-model="dialog" persistent width="1300">
    <v-card>
      <v-card-title>
        <span class="headline">
          {{
            editActivitySuggestion && editActivitySuggestion.id === null
              ? 'New Activity Suggestion'
              : 'Edit Activity Suggestion'
          }}
        </span>
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
            <v-col cols="12">
              <v-text-field
                label="*Description"
                :rules="[(v) => !!v || 'Description is required']"
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
            <v-col cols="12" sm="6" md="4">
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

Vue.component('VueCtkDateTimePicker', VueCtkDateTimePicker);
@Component({
  methods: { ISOtoString },
})
export default class ActivitySuggestionDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: ActivitySuggestion, required: true }) readonly activitySuggestion!: ActivitySuggestion;

  // TODO -> adicionar institutions aqui no dialog
  // TODO -> colocar as datas na linha a seguir ao Number em vez de estar à direita

  editActivitySuggestion: ActivitySuggestion = new ActivitySuggestion();

  cypressCondition: boolean = false;

  async created() {
    this.editActivitySuggestion = new ActivitySuggestion(this.activitySuggestion);
  }


</script>

<style scoped lang="scss"></style>