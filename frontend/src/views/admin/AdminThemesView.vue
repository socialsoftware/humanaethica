<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :items="themes"
      :search="search"
      disable-pagination
      :hide-default-footer="true"
      :mobile-breakpoint="0"
    >
      <template v-slot:top>
        <v-card-title>
          <v-text-field
            v-model="search"
            append-icon="search"
            label="Search"
            class="mx-2"
          />
          <v-spacer />
          <v-btn color="primary" dark @click="TreeView" data-cy="createButton"
            >Tree</v-btn
          >
          <v-btn color="primary" dark @click="newTheme" data-cy="createButton"
            >New Theme</v-btn
          >
        </v-card-title>
      </template>
      <template v-slot:[`item.institutions`]="{ item }">
        <v-for v-for="institution in item.institutions" :key="institution.name">
          <v-chip> {{ institution.name }} </v-chip>
        </v-for>
      </template>
      <template v-slot:[`item.parentTheme`]="{ item }">
        <v-chip v-if="item.parentTheme && item.parentTheme.completeName">
          {{ item.parentTheme.completeName }}
        </v-chip>
      </template>
      <template v-slot:[`item.state`]="{ item }">
        <span :class="getStateClass(item.state)">{{ item.state }}</span>
      </template>
      <template v-slot:[`item.action`]="{ item }">
        <v-tooltip
          bottom
          v-if="item.state == 'SUBMITTED' || item.state == 'APPROVED'"
        >
          <template v-slot:activator="{ on }">
            <v-icon
              class="mr-2 action-button"
              color="red"
              v-on="on"
              data-cy="deleteButton"
              @click="deleteTheme(item)"
              >delete</v-icon
            >
          </template>
          <span>Delete Theme</span>
        </v-tooltip>
        <v-tooltip
          bottom
          v-if="item.state == 'SUBMITTED' || item.state == 'DELETED'"
        >
          <template v-slot:activator="{ on }">
            <v-icon
              class="mr-2 action-button"
              color="green"
              v-on="on"
              data-cy="validateButton"
              @click="validateTheme(item)"
              >mdi-check-bold</v-icon
            >
          </template>
          <span>Validate Theme</span>
        </v-tooltip>
      </template>
    </v-data-table>
    <add-theme
      v-if="addTheme"
      v-model="addTheme"
      v-on:theme-created="onCreatedTheme"
      v-on:close-dialog="onCloseDialog"
    />
    <tree-view
      v-if="treeView"
      v-model="treeView"
      v-on:close-dialog="onCloseDialog"
    />
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import AdminAddTheme from '@/views/admin/AdminAddTheme.vue';
import ThemesTreeView from '@/views/admin/ThemesTreeView.vue';
import Theme from '@/models/theme/Theme';

@Component({
  components: {
    'add-theme': AdminAddTheme,
    'tree-view': ThemesTreeView,
  },
})
export default class AdminThemeView extends Vue {
  themes: Theme[] = [];
  addTheme: boolean = false;
  treeView: boolean = false;
  search: string = '';
  headers: object = [
    { text: 'Name', value: 'completeName', align: 'left', width: '5%' },
    {
      text: 'Parent',
      value: 'parentTheme',
      align: 'left',
      width: '10%',
    },
    {
      text: 'Institutions',
      value: 'institutions',
      align: 'left',
      width: '10%',
    },
    {
      text: 'State',
      value: 'state',
      align: 'left',
      width: '10%',
    },
    {
      text: 'Actions',
      value: 'action',
      align: 'left',
      sortable: false,
      width: '5%',
    },
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.themes = await RemoteServices.getThemes();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  newTheme() {
    this.addTheme = true;
  }

  TreeView() {
    this.treeView = true;
  }

  async onCreatedTheme() {
    this.themes = await RemoteServices.getThemes();
    this.addTheme = false;
  }

  onTreeView() {
    this.treeView = false;
  }

  onCloseDialog() {
    this.addTheme = false;
    this.treeView = false;
  }

  async deleteTheme(theme: Theme) {
    let themeId = theme.id;
    if (
      themeId !== null &&
      confirm('Are you sure you want to delete the theme?')
    ) {
      try {
        this.themes = await RemoteServices.deleteTheme(themeId);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  async validateTheme(theme: Theme) {
    let themeId = theme.id;
    if (
      themeId !== null &&
      confirm('Are you sure you want to validate this theme?')
    ) {
      try {
        await RemoteServices.validateTheme(themeId);
        this.themes = await RemoteServices.getThemes();
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  getStateClass(state: string): string {
    switch (state) {
      case 'SUBMITTED':
        return 'orange--text';
      case 'APPROVED':
        return 'green--text';
      case 'DELETED':
        return 'red--text';
      default:
        return '';
    }
  }
}
</script>

<style lang="scss" scoped>
.orange--text,
.green--text,
.red--text {
  font-weight: bold;
}

.orange--text {
  color: orange;
}

.green--text {
  color: green;
}

.red--text {
  color: red;
}
</style>
