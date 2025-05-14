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
import Theme from '@/models/theme/Theme';
import RemoteServices from '@/services/RemoteServices';

export default {
  name: 'ThemesTreeView',

  props: {
    dialog: {
      type: Boolean,
      required: true,
    },
  },

  data() {
    return {
      themes: [] as Theme[],
      selectedTheme: null as string | null,
    };
  },

  watch: {
    dialog(this: any, newVal: boolean) {
      if (newVal) {
        this.loadThemes();
      }
    },
  },

  created(this: any) {
    if (this.dialog) {
      this.loadThemes();
    }
  },

  methods: {
    async loadThemes(this: any) {
      this.themes = await RemoteServices.getThemes();
    },

    getIndentedDisplayName(level?: number): string {
      const tabChar = '\u00A0';
      return tabChar.repeat((level ?? 0) * 10);
    },

    getThemeColor(state: string): string {
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
    },
  },
};
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
