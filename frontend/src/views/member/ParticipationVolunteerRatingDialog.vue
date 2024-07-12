<template>
  <v-dialog v-model="dialog" persistent width="800">
    <v-card>
      <v-card-title>
        <span class="headline">Volunteer Rating</span>
      </v-card-title>
      <v-card-text>
        <v-form ref="form" lazy-validation>
          <v-row>
            <v-col cols="12" class="d-flex align-center">
              <v-rating
                v-model="editParticipation.volunteerRating"
                length="5"
                color="yellow"
                data-cy="ratingInput"
                half-increments
                readonly
              ></v-rating>
              <span class="ml-2">{{ editParticipation.volunteerRating }}/5</span>
            </v-col>
            <v-col cols="12">
              <v-textarea
                v-model="editParticipation.volunteerReview"
                label="Review"
                data-cy="reviewInput"
                auto-grow
                rows="1"
                readonly
              ></v-textarea>
            </v-col>
          </v-row>
        </v-form>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn
          color="primary"
          dark
          variant="text"
          @click="$emit('close-participation-dialog')"
        >
          Close
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
<script lang="ts">
import { Vue, Component, Prop, Model } from 'vue-property-decorator';
import { ISOtoString } from '@/services/ConvertDateService';
import Participation from '@/models/participation/Participation';

@Component({
  methods: { ISOtoString },
})
export default class ParticipationVolunteerRatingDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Participation, required: true })
  readonly participation!: Participation;

  editParticipation: Participation = new Participation();

  async created() {
    this.editParticipation = new Participation(this.participation);
    this.editParticipation.activityId = this.participation.activityId;
    this.editParticipation.volunteerId = this.participation.volunteerId;
  }
}
</script>

<style scoped lang="scss"></style>
