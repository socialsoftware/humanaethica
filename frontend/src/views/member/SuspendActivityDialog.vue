<template>
  <v-dialog v-model="dialog" persistent width="800">
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
                v-model="activity.suspensionJustification"
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
import { Vue, Component, Prop, Model } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import { ISOtoString } from '@/services/ConvertDateService';
import Activity from '@/models/activity/Activity';

@Component({
  methods: { ISOtoString },
})
export default class SuspendActivityDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Activity, required: true }) readonly activity!: Activity;

  get canSuspend(): boolean {
    return (
      !!this.activity.suspensionJustification &&
      this.activity.suspensionJustification.length >= 10 &&
      this.activity.suspensionJustification.length <= 250
    );
  }

  async suspendActivity() {
    if (
      this.activity.id !== null &&
      (this.$refs.form as Vue & { validate: () => boolean }).validate()
    ) {
      try {
        let result = await RemoteServices.suspendActivity(
          this.activity.id,
          this.activity.suspensionJustification,
        );
        this.$emit('suspend-activity', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>

<style scoped lang="scss"></style>
