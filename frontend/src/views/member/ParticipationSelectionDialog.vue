<template>
  <v-dialog v-model="dialog" persistent width="800">
    <v-card>
      <v-card-title>
        <span class="headline">
          {{
            editParticipation && editParticipation.id === null
              ? 'Create Participation'
              : 'Edit Participation'
          }}
        </span>
      </v-card-title>
      <v-card-text>
        <v-form ref="form" lazy-validation>
          <v-row>
            <v-col cols="12">
              <v-text-field
                label="Rating"
                :rules="[(v) => isNumberValid(v) || 'Rating between 1 and 5']"
                v-model="editParticipation.rating"
                data-cy="participantsNumberInput"
              ></v-text-field>
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
        <v-btn
          color="primary"
          dark
          variant="text"
          @click="createUpdateParticipation"
          data-cy="createParticipation"
        >
          {{ buttonText }}
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
export default class ParticipationSelectionDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Participation, required: true })
  readonly participation!: Participation;

  editParticipation: Participation = new Participation();

  async created() {
    this.editParticipation = new Participation(this.participation);
    this.editParticipation.activityId = this.participation.activityId;
    this.editParticipation.volunteerId = this.participation.volunteerId;
  }

  get buttonText(): string {
    return this.editParticipation && this.editParticipation.id
      ? 'Edit Participant'
      : 'Make Participant';
  }

  isNumberValid(value: any) {
    if (value === null || value === undefined || value === '') return true;
    if (!/^\d+$/.test(value)) return false;
    const parsedValue = parseInt(value);
    return parsedValue >= 1 && parsedValue <= 5;
  }

  async createUpdateParticipation() {
    if ((this.$refs.form as Vue & { validate: () => boolean }).validate()) {
      try {
        const result =
          this.editParticipation.id !== null
            ? await RemoteServices.updateParticipation(
                this.editParticipation.id,
                this.editParticipation,
              )
            : await RemoteServices.createParticipation(
                this.editParticipation.activityId!,
                this.editParticipation,
              );
        this.$emit('save-participation', result);
        this.$emit('close-participation-dialog');
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>

<style scoped lang="scss"></style>
