<template>
  <v-dialog
    :value="dialog"
    @input="$emit('close-dialog')"
    @keydown.esc="$emit('close-dialog')"
    max-width="90%"
    max-height="90%"
  >
    <v-card>
      <v-card-title>
        <span class="headline">Themes</span>
      </v-card-title>
      <v-card-text class="theme-list">
        <v-list>
          <v-list-item-group v-model="selectedTheme">
            <v-list-item
              v-for="theme in themes"
              :key="theme.name"
              :value="theme.name"
            >
              <v-list-item-content>
                <div
                  class="left-text"
                  :style="{ color: getThemeColor(theme.state) }"
                >
                  <span class="indentation">{{
                    getIndentedDisplayName(theme.level)
                  }}</span
                  >&#9658;{{ theme.name }}
                </div>
              </v-list-item-content>
            </v-list-item>
          </v-list-item-group>
        </v-list>
      </v-card-text>
      <v-card-actions>
        <v-spacer />
        <v-btn
          color="blue darken-1"
          @click="$emit('close-dialog')"
          data-cy="cancelButton"
          >Close</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { defineComponent, ref, onMounted, computed } from 'vue';
import Theme from '@/models/theme/Theme';
import RemoteServices from '@/services/RemoteServices';

export default defineComponent({
  name: 'ThemesTreeView',
  props: {
    dialog: {
      type: Boolean,
      required: true,
    },
  },
  emits: ['close-dialog'],
  setup(props, { emit }) {
    const themes = ref<Theme[]>([]);
    const selectedTheme = ref<string | null>(null);

    const getIndentedDisplayName = (level: number) => {
      const tabChar = '\u00A0'; // non-breaking space
      return tabChar.repeat(level * 10);
    };

    const getThemeColor = (state: string) => {
      switch (state) {
        case 'SUBMITTED':
          return 'orange';
        case 'APPROVED':
          return 'green';
        case 'DELETED':
          return 'red';
        default:
          return '';
      }
    };

    onMounted(async () => {
      themes.value = await RemoteServices.getThemes();
    });

    return {
      themes,
      selectedTheme,
      getIndentedDisplayName,
      getThemeColor,
    };
  },
});
</script>

<style scoped>
.theme-list {
  max-height: 500px;
  overflow-y: auto;
}

.left-text {
  text-align: left;
}

.indentation {
  margin-right: 5px;
}
</style>
