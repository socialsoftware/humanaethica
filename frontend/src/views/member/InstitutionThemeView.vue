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
          <v-btn color="primary" dark @click="newTheme" data-cy="createButton"
            >New Theme</v-btn
          >
          <v-btn
            color="primary"
            dark
            @click="AssociateTheme"
            data-cy="createButton"
            >Associate Theme</v-btn
          >
        </v-card-title>
      </template>
      <template v-slot:[`item.action`]="{ item }">
        <v-tooltip bottom>
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
      </template>
    </v-data-table>
    <register-theme
      v-if="addTheme"
      v-model="addTheme"
      v-on:theme-created="onCreatedTheme"
      v-on:close-dialog="onCloseDialog"
    />
    <associate-theme
      v-if="associateTheme"
      v-model="associateTheme"
      v-on:theme-associated="onAssociateTheme"
      v-on:close-dialog="onCloseDialog"
    />
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Theme from '@/models/theme/Theme';
import RegisterTheme from '@/views/member/InstitutionAddTheme.vue';
import InstitutionAssociateThemeView from '@/views/member/InstitutionAssociateThemeView.vue';

@Component({
  components: {
    'register-theme': RegisterTheme,
    'associate-theme': InstitutionAssociateThemeView,
  },
})
export default class InstitutionThemeView extends Vue {
  themes: Theme[] = [];
  addTheme: boolean = false;
  associateTheme: boolean = false;
  search: string = '';
  headers: object = [
    { text: 'Name', value: 'completeName', align: 'left', width: '15%' },
    {
      text: 'Delete',
      value: 'action',
      align: 'left',
      sortable: false,
      width: '5%',
    },
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.themes = await RemoteServices.getThemesbyInstitution();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  newTheme() {
    this.addTheme = true;
  }

  AssociateTheme() {
    this.associateTheme = true;
  }

  async onAssociateTheme() {
    this.themes = await RemoteServices.getThemesbyInstitution();
    this.associateTheme = false;
  }

  onCreatedTheme() {
    this.addTheme = false;
  }

  onCloseDialog() {
    this.addTheme = false;
    this.associateTheme = false;
  }

  async deleteTheme(theme: Theme) {
    let themeId = theme.id;
    if (
      themeId !== null &&
      confirm('Are you sure you want to delete the theme?')
    ) {
      try {
        await RemoteServices.removeThemetoInstitution(themeId);
        this.themes = this.themes =
          await RemoteServices.getThemesbyInstitution();
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>

<style lang="scss" scoped></style>
