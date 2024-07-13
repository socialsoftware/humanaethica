<template>
  <v-dialog v-model="dialog" persistent width="800">
    <v-card>
      <v-card-title>
        <span class="headline"> Write Rating </span>
      </v-card-title>
      <v-card-text>
        <v-form ref="form" lazy-validation>
          <v-row>
            <v-col cols="12" class="d-flex align-center">
              <v-text-field
                label="Rating"
                :rules="[(v) => isNumberValid(v) || 'Rating between 1 and 5']"
                v-model="editParticipation.volunteerRating"
                data-cy="ratingInput"
              ></v-text-field>
            </v-col>
            <v-col cols="12">
              <v-textarea
                label="Review"
                :rules="[(v) => !!v || 'Review is required']"
                required
                v-model="editParticipation.volunteerReview"
                data-cy="reviewInput"
                auto-grow
                rows="1"
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
          @click="$emit('close-participation-dialog')"
        >
          Close
        </v-btn>
        <v-btn
          v-if="isReviewValid && isRatingValid"
          color="blue-darken-1"
          variant="text"
          @click="updateParticipation"
          data-cy="saveParticipation"
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
import Participation from '@/models/participation/Participation';

@Component({
  methods: { ISOtoString },
})
export default class ParticipationDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Participation, required: true })
  readonly participation!: Participation;
  @Prop({ type: Boolean, required: true }) readonly is_update!: Boolean;

  editParticipation: Participation = new Participation();

  async created() {
    this.editParticipation = new Participation(this.participation);
  }

  get isReviewValid(): boolean {
    return (
      !!this.editParticipation.volunteerReview &&
      this.editParticipation.volunteerReview.length >= 10 &&
      this.editParticipation.volunteerReview.length < 100
    );
  }

  get isRatingValid(): boolean {
    return !!this.editParticipation.volunteerRating;
  }

  isNumberValid(value: any) {
    if (value === null || value === undefined || value === '') return true;
    if (!/^\d+$/.test(value)) return false;
    const parsedValue = parseInt(value);
    return parsedValue >= 1 && parsedValue <= 5;
  }

  async updateParticipation() {
    if ((this.$refs.form as Vue & { validate: () => boolean }).validate()) {
      try {
        if (this.editParticipation.id !== null) {
          const result = await RemoteServices.updateParticipationVolunteer(
            this.editParticipation.id,
            this.editParticipation,
          );
          this.$emit('save-participation', result);
          this.$emit('close-participation-dialog');
        } else {
          throw new Error('Participation ID is required for updating');
        }
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>

<style scoped lang="scss"></style>
