<template>
  <v-dialog
    :value="dialog"
    @input="$emit('update:dialog', $event)"
    persistent
    width="800"
  >
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
import Vue from 'vue';
import RemoteServices from '@/services/RemoteServices';
import Participation from '@/models/participation/Participation';

export default Vue.extend({
  name: 'ParticipationDialog',

  props: {
    dialog: {
      type: Boolean,
      required: true,
    },
    participation: {
      type: Object as () => Participation,
      required: true,
    },
    is_update: {
      type: Boolean,
      required: true,
    },
  },

  data() {
    return {
      editParticipation: new Participation(this.participation),
    };
  },

  computed: {
    isReviewValid(): boolean {
      const review = this.editParticipation.volunteerReview;
      return !!review && review.length >= 10 && review.length < 100;
    },
    isRatingValid(): boolean {
      return !!this.editParticipation.volunteerRating;
    },
  },

  methods: {
    isNumberValid(value: any): boolean {
      if (value === null || value === undefined || value === '') return true;
      if (!/^\d+$/.test(value)) return false;
      const parsed = parseInt(value, 10);
      return parsed >= 1 && parsed <= 5;
    },

    async updateParticipation() {
      const form = this.$refs.form as { validate: () => boolean } | undefined;
      if (!form || !form.validate()) return;

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
    },
  },
});
</script>

<style scoped lang="scss"></style>
