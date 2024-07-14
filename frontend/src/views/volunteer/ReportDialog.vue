<template>
  <v-dialog v-model="dialog" persistent width="800">
    <v-card>
      <v-card-title>
        <span class="headline">
          {{ 'New Report' }}
        </span>
      </v-card-title>
      <v-card-text>
        <v-form ref="form" lazy-validation>
          <v-row>
            <v-col cols="12">
              <v-textarea
                label="*Justification"
                :rules="[(v) => !!v || 'Justification is required']"
                required
                v-model="newReport.justification"
                data-cy="justificationInput"
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
          @click="$emit('close-report-dialog')"
        >
          Close
        </v-btn>
        <v-btn
          v-if="canSave"
          color="blue-darken-1"
          variant="text"
          @click="createReport"
          data-cy="saveReport"
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
import Report from '@/models/report/Report';

@Component({
  methods: { ISOtoString },
})
export default class ReportDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Report, required: true }) readonly report!: Report;

  newReport: Report = new Report();

  async created() {
    this.newReport = new Report(this.report);
  }

  get canSave(): boolean {
    return (
      !!this.newReport.justification &&
      this.newReport.justification.length >= 10 &&
      this.newReport.justification.length <= 256
    );
  }

  async createReport() {
    if (
      this.newReport.activityId !== null &&
      (this.$refs.form as Vue & { validate: () => boolean }).validate()
    ) {
      try {
        const result = await RemoteServices.createReport(
          this.newReport.activityId,
          this.newReport,
        );
        this.$emit('save-report', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>

<style scoped lang="scss"></style>
