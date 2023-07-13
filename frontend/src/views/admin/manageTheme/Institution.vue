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
          <span class="headline">Institutions</span>
        </v-card-title>

        <v-card-text style="height: 75px; overflow-y: auto">
          <v-chip
            v-for="institution in theme.institutions"
            :key="institution.id"
          >
            {{ institution.name }}
          </v-chip>
        </v-card-text>

        <v-card-actions>
          <v-spacer />
          <v-btn
            color="blue darken-1"
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
import RemoteServices from '@/services/RemoteServices';
import Theme from '@/models/theme/Theme';
@Component
export default class Institution extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Theme, required: true }) readonly theme!: Theme;
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
