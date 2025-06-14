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
        <div class="d-flex align-center">
          <v-tooltip bottom v-if="item.state === 'IN_REVIEW'">
            <template v-slot:activator="{ on }">
              <div v-on="on">
                <v-btn
                  icon
                  @click="upVote(item)"
                  :disabled="votedSuggestionIds.has(item.id) || item.volunteerId === currentUserId"
                  data-cy="upVoteButton"
                  style="font-size: 28px;"
                >
                  <v-icon :color="(votedSuggestionIds.has(item.id) || item.volunteerId === currentUserId) ? 'grey' : 'blue'">
                    mdi-arrow-up-bold
                  </v-icon>
                </v-btn>
              </div>
            </template>
            <span>
              {{ item.volunteerId === currentUserId ? 'Cannot vote on your own suggestion' : 'Upvote' }}
            </span>
          </v-tooltip>
          <span
            class="ml-2 font-weight-medium"
            style="font-size: 16px; color: #333;"
          >
            {{ item.numberVotes }}
          </span>
        </div>
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
  numberVotes: number | null = null;
  currentUserId: number | null = null;

  // IDs das sugestões que já foram votadas nesta sessão
  votedSuggestionIds: Set<number> = new Set();

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
    this.currentUserId = this.$store.getters.getUser.id;
    await this.$store.dispatch('loading');
    try {
      this.activitySuggestions = await RemoteServices.getAllActivitySuggestions();
      this.institutions = await RemoteServices.getInstitutions();
      this.numberVotes = 0;
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async upVote(activitySuggestion: ActivitySuggestion) {
    if (activitySuggestion.id !== null && !this.votedSuggestionIds.has(activitySuggestion.id)) {
      this.votedSuggestionIds.add(activitySuggestion.id);
      try {
        await RemoteServices.upvoteActivitySuggestion(activitySuggestion.id);
        // const updated = await RemoteServices.getActivitySuggestion(activitySuggestion.id);
        // activitySuggestion.numberVotes = updated.numberVotes;
        if (activitySuggestion.numberVotes == null) {
          activitySuggestion.numberVotes = 1;
        } else {
          activitySuggestion.numberVotes += 1;
        } 
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