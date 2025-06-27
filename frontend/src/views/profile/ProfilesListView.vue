<template>
  <div>
    <v-card class="table">
      <v-card-title>
        <h2>Volunteer Profiles</h2>
      </v-card-title>
      <v-data-table
        :headers="headersVolunteerProfile"
        :items="volunteerProfiles"
        :search="search"
        disable-pagination
        :hide-default-footer="true"
        :mobile-breakpoint="0"
        data-cy="volunteerProfilesTable"
      >
        <template v-slot:item.volunteer.creationDate="{ item }">
          {{ ISOtoString(item.volunteer.creationDate) }}
        </template>
        <template v-slot:item.volunteer.lastAccess="{ item }">
          {{ ISOtoString(item.volunteer.lastAccess) }}
        </template>
        <template v-slot:[`item.action`]="{ item }">
          <v-tooltip bottom>
            <template v-slot:activator="{ on }">
              <v-icon
                class="mr-2 action-button"
                @click="viewVolunteerProfile(item)"
                v-on="on"
                >fa-solid fa-eye
              </v-icon>
            </template>
            <span>View volunteer profile</span>
          </v-tooltip>
        </template>
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
      </v-data-table>
    </v-card>
    <!-- Institution Profiles -->
    <v-card class="table">
      <v-card-title>
        <h2>Institution Profiles</h2>
      </v-card-title>
      <v-data-table
        :headers="headersInstitutionProfile"
        :items="institutionProfiles"
        :search="search"
        disable-pagination
        :hide-default-footer="true"
        :mobile-breakpoint="0"
        data-cy="institutionProfilesTable"
      >
        <template v-slot:item.institution.creationDate="{ item }">
          {{ ISOtoString(item.institution.creationDate) }}
        </template>
        <template v-slot:[`item.action`]="{ item }">
          <v-tooltip bottom>
            <template v-slot:activator="{ on }">
              <v-icon
                class="mr-2 action-button"
                @click="viewProfile(item)"
                v-on="on"
                data-cy="showInstitutionProfile"
                >fa-solid fa-eye
              </v-icon>
            </template>
          <span>View institution profile</span>
        </v-tooltip>
        </template>
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
      </v-data-table>
    </v-card>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import { ISOtoString } from "../../services/ConvertDateService";
import VolunteerProfile from '@/models/volunteerProfile/VolunteerProfile';
import RemoteServices from '@/services/RemoteServices';
import InstitutionProfile from '@/models/profile/InstitutionProfile';

@Component({
  methods: { ISOtoString }
})
export default class ProfilesListView extends Vue {
  volunteerProfiles: VolunteerProfile[] = []; 
  institutionProfiles: InstitutionProfile[] = []; 

  search: string = '';

  headersVolunteerProfile: object = [
    { text: 'Name', value: 'volunteer.name', align: 'left', width: '10%' },
    {
      text: 'Short Bio',
      value: 'shortBio',
      align: 'left',
      width: '40%',
    },
    {
      text: 'Registration Date',
      value: 'volunteer.creationDate',
      align: 'left',
      width: '10%',
    },
    {
      text: 'Last Access',
      value: 'volunteer.lastAccess',
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

  headersInstitutionProfile: object = [
    { text: 'Name', value: 'institution.name', align: 'left', width: '10%' },
    {
      text: 'Short Description',
      value: 'shortDescription',
      align: 'left',
      width: '40%',
    },
    {
      text: 'Registration Date',
      value: 'institution.creationDate',
      align: 'left',
      width: '10%',
    },
    {
      text: 'Active',
      value: 'institution.active',
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
      this.volunteerProfiles = await RemoteServices.getListVolunteerProfile();
      this.institutionProfiles = await RemoteServices.getInstitutionsProfiles();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async viewVolunteerProfile(volunteerProfile: VolunteerProfile){
    await this.$store.dispatch('setVolunteerProfile', volunteerProfile);
    await this.$router.push({ name: 'volunteer-profile' , 
    params: { id: volunteerProfile!.volunteer!.id!.toString()}});
  }

  async viewProfile(institutionProfile: InstitutionProfile){
    await this.$store.dispatch('setInstitutionProfile', institutionProfile);
    await this.$router.push({ name: 'institution-profile' , 
    params: { id: institutionProfile!.institution!.id!.toString()}});
  }
}
</script>

<style lang="scss" scoped></style>
