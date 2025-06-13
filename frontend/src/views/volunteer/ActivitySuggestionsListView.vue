<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :items="activitySuggestions"
      :search="search"
      disable-pagination
      :hide-default-footer="true"
      :mobile-breakpoint="0"
      data-cy="communityActivitySuggestionsTable"
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
      <template v-slot:[`item.action`]="{ item }">
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <div class="d-flex align-center">
              <v-icon
                class="mr-2 action-button"
                color="blue"
                @click="upVote(item)"
                data-cy="upVoteButton"
                >fa-solid fa-up
              </v-icon>
              <span>Upvote</span>
              <span class="text-caption grey--text">{{ item.numberVotes }}</span>
            </div>
          </template>
        </v-tooltip>
      </template>
    </v-data-table>
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import ActivitySuggestion from '@/models/activitysuggestion/ActivitySuggestion';
import Institution from '@/models/institution/Institution';
import RemoteServices from '@/services/RemoteServices';
import { show } from 'cli-cursor';

@Component({
  methods: { show },
})

export default class VolunteerActivitySuggestionsView extends Vue {
  activitySuggestions: ActivitySuggestion[] = [];
  institutions: Institution[] = [];
  search: string = '';

  currentActivitySuggestion: ActivitySuggestion | null = null;

  headers: object = [
    {
      text: 'Name',
      value: 'name',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Institution',
      value: 'institution.name',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Description',
      value: 'description',
      align: 'left',
      width: '30%',
    },
    {
      text: 'Region',
      value: 'region',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Participants Limit',
      value: 'participantsNumberLimit',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Start Date',
      value: 'formattedStartingDate',
      align: 'left',
      width: '5%',
    },
    {
      text: 'End Date',
      value: 'formattedEndingDate',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Application Deadline',
      value: 'formattedApplicationDeadline',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Creation Date',
      value: 'creationDate',
      align: 'left',
      width: '5%',
    },
    {
      text: 'State',
      value: 'state',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Actions',
      value: 'action',
      align: 'left',
      width: '5%',
    },
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      let userId = this.$store.getters.getUser.id;
      this.activitySuggestions = await RemoteServices.getVolunteerActivitySuggestions(userId);
      this.institutions = await RemoteServices.getInstitutions();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async upVote(activitySuggestion: ActivitySuggestion) {
    if (activitySuggestion.id !== null) {
      try {
        await RemoteServices.upvoteActivitySuggestion(activitySuggestion.id);
        // const updated = await RemoteServices.getActivitySuggestion(activitySuggestion.id);
        // activitySuggestion.numberVotes = updated.numberVotes; 
        activitySuggestion.numberVotes++; //má prática
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.date-fields-container {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.date-fields-row {
  display: flex;
  gap: 16px;
  margin-top: 8px;
}
</style>