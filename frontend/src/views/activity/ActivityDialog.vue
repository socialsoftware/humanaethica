<template>
  <v-dialog
    :value="dialog"
    @input="$emit('close-dialog')"
    @keydown.esc="$emit('close-dialog')"
    max-width="50%"
    max-height="80%"
  >
    <v-card>
      <v-form ref="form" v-model="valid" lazy-validation>
        <v-card-title>
          <span class="headline">Activity Description</span>
        </v-card-title>

        <v-card-text class="text-left">
          <b>Activity:</b>
          {{ activity.name }}
          <br />
          <b>Description:</b>
          {{ activity.description }}
        </v-card-text>

        <v-card-actions>
          <v-spacer />
          <v-btn
            color="orange"
            @click="$emit('close-dialog')"
            data-cy="closeButton"
            >Close</v-btn
          >
        </v-card-actions>
      </v-form>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import Activity from '@/models/activity/Activity';

@Component
export default class ActivityDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Activity, required: true }) readonly activity!: Activity;

  valid = true;
  success = false;

  created() {}
}
</script>

<style scoped>
.add-user-feedback-container {
  height: 25px;
}
.add-user-feedback {
  font-size: 1.05rem;
  color: #1b5e20;
  text-transform: uppercase;
}
</style>
