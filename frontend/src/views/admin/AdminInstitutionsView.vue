<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :items="institutions"
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
        </v-card-title>
      </template>
      <template v-slot:[`item.themes`]="{ item }">
        <v-chip v-for="theme in item.themes" v-bind:key="theme.id">
          {{ theme.name }}
        </v-chip>
      </template>

      <template v-slot:[`item.action`]="{ item }">
        <v-tooltip bottom v-if="item.active && !isDemoInstitution(item)">
          <template v-slot:activator="{ on }">
            <v-icon
              class="mr-2 action-button"
              color="red"
              v-on="on"
              data-cy="deleteButton"
              @click="deleteInstitution(item)"
              >delete</v-icon
            >
          </template>
          <span>Delete institution</span>
        </v-tooltip>
        <v-tooltip bottom v-if="!isDemoInstitution(item)">
          <template v-slot:activator="{ on }">
            <v-icon
              class="mr-2 action-button"
              color="black"
              v-on="on"
              data-cy="documentButton"
              @click="getDocument(item)"
              >description</v-icon
            >
          </template>
          <span>See document</span>
        </v-tooltip>
        <v-tooltip bottom v-if="!item.active && !isDemoInstitution(item)">
          <template v-slot:activator="{ on }">
            <v-icon
              class="mr-2 action-button"
              color="green"
              v-on="on"
              data-cy="validateButton"
              @click="validateInstitution(item)"
              >mdi-check-bold</v-icon
            >
          </template>
          <span>Validate institution</span>
        </v-tooltip>
      </template>
    </v-data-table>
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Institution from '@/models/institution/Institution';

@Component({
  components: {},
})
export default class AdminInstitutionsView extends Vue {
  institutions: Institution[] = [];
  search: string = '';
  headers: object = [
    { text: 'Name', value: 'name', align: 'left', width: '25%' },
    {
      text: 'Themes',
      value: 'themes',
      align: 'left',
      width: '10%',
    },
    {
      text: 'Email',
      value: 'email',
      align: 'left',
      width: '10%',
    },
    {
      text: 'NIF',
      value: 'nif',
      align: 'left',
      width: '10%',
    },
    {
      text: 'Active',
      value: 'active',
      align: 'center',
      width: '5%',
    },
    {
      text: 'Creation Date',
      value: 'creationDate',
      align: 'center',
      width: '10%',
    },
    {
      text: 'Delete',
      value: 'action',
      align: 'center',
      sortable: false,
      width: '5%',
    },
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.institutions = await RemoteServices.getInstitutions();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async deleteInstitution(institution: Institution) {
    let institutionId = institution.id;
    if (
      institutionId !== null &&
      confirm('Are you sure you want to delete the institution?')
    ) {
      try {
        this.institutions =
          await RemoteServices.deleteInstitution(institutionId);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  async validateInstitution(institution: Institution) {
    let institutionId = institution.id;
    if (
      institutionId !== null &&
      confirm('Are you sure you want to validate this institution?')
    ) {
      try {
        this.institutions =
          await RemoteServices.validateInstitution(institutionId);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  async getDocument(institution: Institution) {
    let institutionId = institution.id;
    if (institutionId !== null) {
      try {
        await RemoteServices.getInstitutionDocument(institutionId);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  isDemoInstitution(institution: Institution) {
    return institution.name === 'DEMO INSTITUTION';
  }
}
</script>

<style lang="scss" scoped></style>
