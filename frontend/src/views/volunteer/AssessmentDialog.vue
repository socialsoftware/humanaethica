<template>
  <v-dialog v-model="dialog" persistent width="800">
    <v-card>
      <v-card-title>
        <span class="headline">
          {{
            editAssessment && editAssessment.id === null
              ? 'New Assessment'
              : 'Edit Assessment'
          }}
        </span>
      </v-card-title>
      <v-card-text>
        <v-form ref="form" lazy-validation>
          <v-row>
            <v-col cols="12">
              <v-text-field
                label="*Review"
                :rules="[(v) => !!v || 'Review is required']"
                required
                v-model="editAssessment.review"
                data-cy="reviewInput"
              ></v-text-field>
            </v-col>
          </v-row>
        </v-form>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn
          color="blue-darken-1"
          variant="text"
          @click="$emit('close-assessment-dialog')"
        >
          Close
        </v-btn>
        <v-btn
          v-if="canSave"
          color="blue-darken-1"
          variant="text"
          @click="updateAssessment"
          data-cy="saveAssessment"
        >
          Save
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
<script lang="ts">
import { Vue, Component, Prop, Model } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import { ISOtoString } from '@/services/ConvertDateService';
import Assessment from '@/models/assessment/Assessment';

@Component({
  methods: { ISOtoString },
})
export default class AssessmentDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Assessment, required: true }) readonly assessment!: Assessment;
  @Prop({ type: Boolean, required: true }) readonly is_update!: Boolean;

  editAssessment: Assessment = new Assessment();

  async created() {
    this.editAssessment = new Assessment(this.assessment);
  }

  get canSave(): boolean {
    return (
      !!this.editAssessment.review && this.editAssessment.review.length >= 10
    );
  }

  async updateAssessment() {
    if (
      this.editAssessment.institutionId !== null &&
      (this.$refs.form as Vue & { validate: () => boolean }).validate()
    ) {
      try {
        let result;
        if (this.is_update) {
          result = await RemoteServices.updateAssessment(this.editAssessment);
        } else {
          result = await RemoteServices.createAssessment(
            this.editAssessment.institutionId,
            this.editAssessment,
          );
        }

        this.$emit('save-assessment', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>

<style scoped lang="scss"></style>
