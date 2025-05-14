<template v-if="theme">
  <v-dialog
    :value="dialog"
    @input="$emit('close-dialog')"
    @keydown.esc="$emit('close-dialog')"
    max-width="75%"
    max-height="80%"
  >
    <v-card>
      <v-form ref="form" v-model="valid" lazy-validation>
        <v-card-title>
          <span class="headline">Add Theme</span>
        </v-card-title>
        <v-select
          v-model="theme.parentTheme"
          label="Parent Theme (Optional)"
          :items="themes"
          return-object
          item-value="id"
          item-text="completeName"
          required
          :menu-props="{ offsetY: true, nudgeLeft: 0, class: 'left-text' }"
          class="move-right"
        >
          <template v-slot:item="{ item }">
            <div class="left-text">
              <span class="indentation">{{ item.completeName }}</span>
            </div>
          </template>
        </v-select>

        <v-card-text class="text-left">
          <v-text-field
            v-model="theme.name"
            label="Name"
            data-cy="themeNameInput"
            :rules="[(value) => !!value || 'Name is required']"
            required
          />
          <div class="add-theme-feedback-container">
            <span class="add-theme-feedback" v-if="success">
              {{ theme.name }} added</span
            >
          </div>
        </v-card-text>

        <v-card-actions>
          <v-spacer />
          <v-btn
            color="blue darken-1"
            @click="$emit('close-dialog')"
            data-cy="cancelButton"
            >Close</v-btn
          >
          <v-btn color="blue darken-1" @click="submit" data-cy="saveButton"
            >Add</v-btn
          >
        </v-card-actions>
      </v-form>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import RemoteServices from '@/services/RemoteServices';
import Theme from '@/models/theme/Theme';

export default defineComponent({
  name: 'InstitutionAddTheme',
  props: {
    dialog: {
      type: Boolean,
      required: true,
    },
  },
  data(this: any) {
    return {
      valid: true,
      success: false,
      theme: new Theme(),
      themes: [] as Theme[],
    };
  },
  async created(this: any) {
    this.theme = new Theme();
    this.themes = await RemoteServices.getThemesAvailable();
  },
  methods: {
    async submit(this: any) {
      this.success = false;

      const form = this.$refs.form as any;
      if (!form || !form.validate()) return;

      try {
        const result = await RemoteServices.registerThemeInstitution(
          this.theme,
        );
        this.$emit('theme-created', result);
        this.success = true;
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    },
  },
});
</script>

<style scoped>
.add-theme-feedback-container {
  height: 25px;
}
.add-theme-feedback {
  font-size: 1.05rem;
  color: #1b5e20;
  text-transform: uppercase;
}

.left-text {
  text-align: left;
}

.move-right {
  margin-left: 20px;
  margin-right: 20px;
}
</style>
