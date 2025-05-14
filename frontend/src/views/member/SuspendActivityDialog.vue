<template>
  <v-dialog
    :value="dialog"
    @input="$emit('update:dialog', $event)"
    persistent
    width="800"
  >
    <v-card>
      <v-card-title>
        <span class="headline">Suspend Activity</span>
      </v-card-title>
      <v-card-text>
        <v-form ref="form" lazy-validation>
          <v-row>
            <v-col cols="12">
              <v-textarea
                label="*Suspension reason"
                :rules="[
                  (v) => {
                    if (!v) {
                      return 'Suspension reason is required';
                    } else if (v.length <= 10) {
                      return 'Suspension reason must be 10 or more characters';
                    } else if (v.length >= 250) {
                      return 'Suspension reason must be less or equal to 250 characters';
                    }
                    return true;
                  },
                ]"
                required
                auto-grow
                rows="1"
                v-model="editActivity.suspensionJustification"
                data-cy="suspensionReasonInput"
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
          @click="$emit('close-activity-dialog')"
        >
          Close
        </v-btn>
        <v-btn
          v-if="canSuspend"
          color="blue-darken-1"
          variant="text"
          @click="suspendActivity"
          data-cy="suspendActivity"
        >
          Suspend
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import RemoteServices from '@/services/RemoteServices';
import { ISOtoString } from '@/services/ConvertDateService';
import Activity from '@/models/activity/Activity';

export default defineComponent({
  name: 'SuspendActivityDialog',
  props: {
    dialog: {
      type: Boolean,
      required: true,
    },
    activity: {
      type: Object as () => Activity,
      required: true,
    },
  },
  emits: ['update:dialog', 'close-activity-dialog', 'suspend-activity'],
  data() {
    return {
      ISOtoString,
      editActivity: new Activity(this.activity),
    };
  },
  computed: {
    canSuspend(): boolean {
      const justification = this.editActivity.suspensionJustification;
      return (
        !!justification &&
        justification.length >= 10 &&
        justification.length <= 250
      );
    },
  },
  methods: {
    async suspendActivity() {
      const form = this.$refs.form as { validate: () => boolean } | undefined;
      if (this.editActivity.id != null && form?.validate()) {
        try {
          const result = await RemoteServices.suspendActivity(
            this.editActivity.id,
            this.editActivity.suspensionJustification,
          );
          this.$emit('suspend-activity', result);
        } catch (error) {
          await this.$store.dispatch('error', error);
        }
      }
    },
  },
});
</script>

<style scoped lang="scss"></style>
