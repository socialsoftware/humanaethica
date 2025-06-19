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
          <v-tooltip bottom>
            <template v-slot:activator="{ on }">
              <div v-on="on">
                <v-btn
                  class="upvote-btn"
                  v-if="isVotesLoaded"
                  icon
                  @click="toggleVote(item)"
                  :disabled="item.volunteerId === currentUserId || item.state !== 'IN_REVIEW'"
                  data-cy="upVoteButton"
                >
                <v-icon
                  class="vote-icon"
                  :color="getVoteColor(item)"
                  :style="getVoteIconStyle(item)"
                >
                  {{ getVoteIcon(item) }}
                </v-icon>
                </v-btn>
              </div>
            </template>
            <span>
              {{
                getVoteTooltip(item)
              }}
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
  isVotesLoaded: boolean = false;

  // Activity Suggestion IDs that have already been voted
  votedSuggestionIds: number[] = [];

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

      let votedSuggestions = await RemoteServices.getVotedActivitySuggestions();

      votedSuggestions.forEach((suggestion: ActivitySuggestion) => {
        if (suggestion.id !== null && !this.votedSuggestionIds.includes(suggestion.id)) {
          this.votedSuggestionIds.push(suggestion.id);
        }
      });
      this.isVotesLoaded = true;
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async upVote(activitySuggestion: ActivitySuggestion) {
    if (activitySuggestion.id !== null && !this.votedSuggestionIds.includes(activitySuggestion.id)) {
      this.votedSuggestionIds.push(activitySuggestion.id);
      try {
        await RemoteServices.upvoteActivitySuggestion(activitySuggestion.id);
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

  async removeUpVote(activitySuggestion: ActivitySuggestion) {
    if (activitySuggestion.id !== null && this.votedSuggestionIds.includes(activitySuggestion.id)) {
      try {
        await RemoteServices.removeUpvoteActivitySuggestion(activitySuggestion.id);
        this.votedSuggestionIds = this.votedSuggestionIds.filter(id => id !== activitySuggestion.id);
        if (activitySuggestion.numberVotes != null && activitySuggestion.numberVotes > 0) {
          activitySuggestion.numberVotes -= 1;
        }
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  toggleVote(activitySuggestion: ActivitySuggestion) {
    if (!activitySuggestion.id) return;

    const hasVoted = this.votedSuggestionIds.includes(activitySuggestion.id);

    if (hasVoted) {
      this.removeUpVote(activitySuggestion);
    } else {
      this.upVote(activitySuggestion);
    }
  }

  getVoteIcon(item: ActivitySuggestion): string {
    if (item.id === null) return 'disabled-icon';
    const isVoted = this.votedSuggestionIds.includes(item.id);
    const isOwn = item.volunteerId === this.currentUserId;
    const isInactive = isVoted || isOwn || item.state !== 'IN_REVIEW';

    if (isInactive && isVoted) return 'mdi-arrow-up-bold';
    if (!isInactive) return 'mdi-arrow-up-bold-outline';
    return 'mdi-arrow-up-bold';
  }

  getVoteColor(item: ActivitySuggestion): string {
    if (item.id === null) return 'blue';
    const isVoted = this.votedSuggestionIds.includes(item.id);
    const isOwn = item.volunteerId === this.currentUserId;
    const isInactive = isVoted || isOwn || item.state !== 'IN_REVIEW';

    if (isVoted) return 'blue';
    if (!isInactive) return 'black';
    return 'blue';
  }

  getVoteTooltip(item: ActivitySuggestion): string {
    if (item.volunteerId === this.currentUserId) {
      return 'Cannot vote on your own suggestion';
    }
    if (item.state !== 'IN_REVIEW') {
      return 'Suggestion is no longer in review';
    }
    if (item.id && this.votedSuggestionIds.includes(item.id)) {
      return 'Remove Upvote';
    }
    return 'Upvote';
  }

  getVoteIconStyle(item: ActivitySuggestion) {
    if (!item.id) return {};

    const isVoted = this.votedSuggestionIds.includes(item.id);
    const isOwn = item.volunteerId === this.currentUserId;
    const isInactive = isVoted || isOwn || item.state !== 'IN_REVIEW';

    // Apply a slightly larger scale to filled and disabled icons to match the clicked button
    const scale = isVoted || isInactive ? 1.1 : 1.0;

    return {
      transform: `scale(${scale})`,
      transition: 'transform 0.2s ease',
    };
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

.upvote-btn {
  transition: border-color 0.3s ease;
}

.upvote-btn:hover:not([disabled]) .vote-icon {
  color: #1976D2 !important;  /* blue from Vuetify */
}
</style>