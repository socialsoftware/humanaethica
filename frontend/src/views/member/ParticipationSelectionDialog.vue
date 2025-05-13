<template>
  <v-dialog 
  :value="dialog"
  @input="$emit('update:dialog', $event)"
  persistent 
  width="800"
  >
    <v-card>
      <v-card-title>
        <span class="headline">
          {{
            editParticipation && editParticipation.id === null
              ? 'Create Participation'
              : 'Your Rating'
          }}
        </span>
      </v-card-title>
      <v-card-text>
        <v-form ref="form" lazy-validation>
          <v-row>
            <v-col cols="12" class="d-flex align-center">
              <v-text-field
                label="Rating"
                :rules="[(v) => isNumberValid(v) || 'Rating between 1 and 5']"
                v-model="editParticipation.memberRating"
                data-cy="participantsNumberInput"
              ></v-text-field>
            </v-col>
            <v-col cols="12">
              <v-textarea
                label="Review"
                v-model="editParticipation.memberReview"
                :rules="[(v) => !!v || 'Review is required']"
                data-cy="participantsReviewInput"
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
          color="primary"
          dark
          variant="text"
          @click="$emit('close-participation-dialog')"
        >
          Close
        </v-btn>
        <v-btn
          v-if="isReviewValid && isRatingValid"
          color="primary"
          dark
          variant="text"
          @click="createUpdateParticipation"
          data-cy="createParticipation"
        >
          Save
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { defineComponent, ref, computed, watch, onMounted } from 'vue';
import RemoteServices from '@/services/RemoteServices';
import Participation from '@/models/participation/Participation';

export default defineComponent({
  name: 'ParticipationSelectionDialog',
  props: {
    dialog: {
      type: Boolean,
      required: true,
    },
    participation: {
      type: Object as () => Participation,
      required: true,
    },
  },
  emits: ['update:dialog', 'close-participation-dialog', 'save-participation'],
  setup(props, { emit }) {
    const dialogRef = ref(props.dialog);
    const form = ref();
    const editParticipation = ref(new Participation(props.participation));
    const participations = ref<Participation[]>([]);

    const isReviewValid = computed(() => {
      const review = editParticipation.value.memberReview;
      return !!review && review.length >= 10 && review.length < 100;
    });

    const isRatingValid = computed(() => {
      return !!editParticipation.value.memberRating;
    });

    const isNumberValid = (value: any): boolean => {
      if (value === null || value === undefined || value === '') return true;
      if (!/^\d+$/.test(value)) return false;
      const parsed = parseInt(value);
      return parsed >= 1 && parsed <= 5;
    };

    const createUpdateParticipation = async () => {
      const valid = (form.value as any)?.validate?.();
      if (valid) {
        try {
          const result =
            editParticipation.value.id !== null
              ? await RemoteServices.updateParticipationMember(
                  editParticipation.value.id,
                  editParticipation.value
                )
              : await RemoteServices.createParticipation(
                  editParticipation.value.activityId!,
                  editParticipation.value
                );

          emit('save-participation', result);
          emit('close-participation-dialog');
        } catch (error) {
          await (window as any).$store.dispatch('error', error);
        }
      }
    };

    onMounted(async () => {
      participations.value = await RemoteServices.getActivityParticipations(
        editParticipation.value.activityId
      );
    });

    // Sync prop -> ref and emit back (v-model binding)
    watch(
      () => props.dialog,
      (val) => {
        dialogRef.value = val;
      }
    );

    watch(dialogRef, (val) => {
      emit('update:dialog', val);
    });

    return {
      dialogRef,
      form,
      editParticipation,
      participations,
      isReviewValid,
      isRatingValid,
      isNumberValid,
      createUpdateParticipation,
    };
  },
});
</script>

<style scoped lang="scss"></style>
