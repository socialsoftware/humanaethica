<template>
  <v-dialog v-model="dialog" persistent width="800">
    <v-card>
      <v-card-title>
        <span class="headline">Reject Activity Suggestion</span>
      </v-card-title>
      <v-card-text>
        <v-form ref="form" lazy-validation>
          <v-row>
            <v-col cols="12">
              <v-textarea
                label="*Rejection reason"
                :rules="[
                  (v) => {
                    if (!v) {
                      return 'Rejection reason is required';
                    } else if (v.length <= 10) {
                      return 'Rejection reason must be 10 or more characters';
                    } else if (v.length >= 250) {
                      return 'Rejection reason must be less or equal to 250 characters';
                    }

                    return true;
                  },
                ]"
                required
                auto-grow
                rows="1"
                v-model="activitySuggestion.rejectionJustification"
                data-cy="rejectionReasonInput"
              ></v-textarea>
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
          v-if="canReject"
          color="blue-darken-1"
          variant="text"
          @click="rejectActivitySuggestion"
          data-cy="rejectActivitySuggestion"
        >
          Reject
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
<script lang="ts">
import { Vue, Component, Prop, Model } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import { ISOtoString } from '@/services/ConvertDateService';
import ActivitySuggestion from '@/models/activitysuggestion/ActivitySuggestion';
import Institution from '@/models/institution/Institution';

@Component({
  methods: { ISOtoString },
})
export default class RejectActivitySuggestionDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: ActivitySuggestion, required: true }) readonly activitySuggestion!: ActivitySuggestion;
  @Prop({ type: Array, required: true }) readonly activitySuggestions!: ActivitySuggestion[];
  @Prop({ type: Institution, required: true }) readonly institution!: Institution;

  get canReject(): boolean {
    return (
      !!this.activitySuggestion.rejectionJustification &&
      this.activitySuggestion.rejectionJustification.length >= 10 &&
      this.activitySuggestion.rejectionJustification.length <= 250
    );
  }

  async rejectActivitySuggestion(activitySuggestion: ActivitySuggestion) {
    if (this.activitySuggestion.id !== null && this.institution.id != null) {
      try {
        const result = await RemoteServices.rejectActivitySuggestion(this.activitySuggestion.id, this.institution.id, this.activitySuggestion.rejectionJustification);
        const index = this.activitySuggestions.findIndex((a) => a.id === this.activitySuggestion.id);
        if (index !== -1) this.$set(this.activitySuggestions, index, result);
        this.$emit('reject-activity-suggestion', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>

<style scoped lang="scss"></style>
