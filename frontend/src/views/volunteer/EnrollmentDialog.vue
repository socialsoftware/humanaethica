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
            editEnrollment && editEnrollment.id === null
              ? 'New Application'
              : 'Edit Application'
          }}
        </span>
      </v-card-title>
      <v-card-text>
        <v-form ref="form" lazy-validation>
          <v-row>
            <v-col cols="12">
              <v-textarea
                label="*Motivation"
                :rules="[(v) => !!v || 'Motivation is required']"
                required
                v-model="editEnrollment.motivation"
                data-cy="motivationInput"
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
          @click="$emit('close-enrollment-dialog')"
        >
          Close
        </v-btn>
        <v-btn
          v-if="canSave"
          color="blue-darken-1"
          variant="text"
          @click="updateEnrollment"
          data-cy="saveEnrollment"
        >
          Save
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import RemoteServices from '@/services/RemoteServices';
import { ISOtoString } from '@/services/ConvertDateService';
import Enrollment from '@/models/enrollment/Enrollment';

export default {
  name: 'EnrollmentDialog',

  props: {
    dialog: {
      type: Boolean,
      required: true,
    },
    enrollment: {
      type: Enrollment,
      required: true,
    },
  },

  data(this: any) {
    return {
      editEnrollment: new Enrollment(this.enrollment),
    };
  },

  computed: {
    canSave(this: any): boolean {
      return (
        !!this.editEnrollment.motivation &&
        this.editEnrollment.motivation.length >= 10
      );
    },
  },

  methods: {
    ISOtoString,

    async updateEnrollment(this: any) {
      const form = this.$refs.form as Vue & { validate: () => boolean };

      const isValid = form && form.validate();
      const enrollment = this.editEnrollment;

      if (enrollment.id !== null && isValid) {
        try {
          const result = await RemoteServices.editEnrollment(
            enrollment.id,
            enrollment,
          );
          this.$emit('update-enrollment', result);
        } catch (error) {
          await this.$store.dispatch('error', error);
        }
      } else if (enrollment.activityId !== null && isValid) {
        try {
          const result = await RemoteServices.createEnrollment(
            enrollment.activityId,
            enrollment,
          );
          this.$emit('save-enrollment', result);
        } catch (error) {
          await this.$store.dispatch('error', error);
        }
      }
    },
  },
};
</script>

<style scoped lang="scss"></style>
