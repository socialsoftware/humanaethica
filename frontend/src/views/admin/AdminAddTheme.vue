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
import { defineComponent, ref, onMounted } from 'vue';
import RemoteServices from '@/services/RemoteServices';
import Theme from '@/models/theme/Theme';

export default defineComponent({
  name: 'AdminAddTheme',
  props: {
    dialog: {
      type: Boolean,
      required: true,
    },
  },
  emits: ['close-dialog', 'theme-created'],
  setup(props, { emit }) {
    const valid = ref(true);
    const success = ref(false);
    const theme = ref<Theme>(new Theme());
    const themes = ref<Theme[]>([]);
    const form = ref();

    const fetchThemes = async () => {
      themes.value = await RemoteServices.getThemesAvailable();
    };

    const submit = async () => {
      success.value = false;

      if (!form.value?.validate?.()) {
        return;
      }

      try {
        const newTheme = await RemoteServices.registerTheme(theme.value);
        emit('theme-created', newTheme);
        success.value = true;
      } catch (error) {
        await (form.value as any)?.$store?.dispatch('error', error); // fallback if store is injected in form
      }
    };

    onMounted(() => {
      theme.value = new Theme();
      fetchThemes();
    });

    return {
      valid,
      success,
      theme,
      themes,
      form,
      submit,
    };
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
