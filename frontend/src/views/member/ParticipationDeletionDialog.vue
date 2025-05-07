<template>
  <v-dialog
    :value="dialog"
    @input="$emit('update:dialog', $event)"
    persistent
    width="800"
  >
    <v-card>
      <v-card-title>
        <span class="headline"> Delete Participation? </span>
      </v-card-title>
      <v-card-text class="text-body-1 black--text">
        Are you sure you want to delete this participation?<br />
        WARNING: Deleting this participation will also delete any associated
        rating.
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
          color="red darken-1"
          dark
          variant="text"
          data-cy="deleteParticipationDialogButton"
          @click="deleteParticipation"
        >
          Confirm
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import RemoteServices from '@/services/RemoteServices';
import Participation from '@/models/participation/Participation';
import { ISOtoString } from '@/services/ConvertDateService';

export default defineComponent({
  name: 'ParticipationDeletionDialog',

  emits: [
    'update:dialog',
    'delete-participation',
    'close-participation-dialog',
  ],

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

  data(this: any) {
    return {
      editParticipation: new Participation(),
    };
  },

  created() {
    this.editParticipation = new Participation(this.participation);
  },

  methods: {
    ISOtoString,

    async deleteParticipation() {
      if (this.editParticipation && this.editParticipation.id !== null) {
        try {
          const result = await RemoteServices.deleteParticipation(
            this.editParticipation.id,
          );
          this.$emit('delete-participation', result);
          this.$emit('close-participation-dialog');
        } catch (error) {
          await this.$store.dispatch('error', error);
          console.error('Error deleting participation:', error);
        }
      }
    },
  },
});
</script>

<style scoped lang="scss"></style>
