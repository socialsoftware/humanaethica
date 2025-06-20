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
                  @click="toggleUpvote(item, true)"
                  :disabled="item.volunteerId === currentUserId || item.state !== 'IN_REVIEW'"
                  data-cy="upVoteButton"
                >
                <v-icon
                  class="vote-icon"
                  :color="getVoteColor(item, true)"
                  :style="getVoteIconStyle(item)"
                >
                  {{ getVoteIcon(item, true) }}
                </v-icon>
                </v-btn>
              </div>
            </template>
            <span>
              {{
                getVoteTooltip(item, true)
              }}
            </span>
          </v-tooltip>
          <span
            class="ml-2 font-weight-medium"
            style="font-size: 16px; color: #333;"
          >
            {{ item.numberVotes }}
          </span>
          <v-tooltip bottom>
            <template v-slot:activator="{ on }">
              <div v-on="on">
                <v-btn
                  class="downvote-btn"
                  icon
                  @click="toggleDownvote(item)"
                  :disabled="item.volunteerId === currentUserId || item.state !== 'IN_REVIEW'"
                  data-cy="downVoteButton"
                >
                  <v-icon
                    class="vote-icon"
                    :color="getVoteColor(item, false)"
                    :style="getVoteIconStyle(item)"
                  >
                    {{ getVoteIcon(item, false) }}
                  </v-icon>
                </v-btn>
              </div>
            </template>
            <span>{{ getVoteTooltip(item, false) }}</span>
          </v-tooltip>
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
  currentUserId: number | null = null;
  isVotesLoaded: boolean = false;

  // Activity Suggestion IDs that have already been voted
  votedUpSuggestionIds: number[] = [];
  votedDownSuggestionIds: number[] = [];

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

      const upvoted = await RemoteServices.getUpvotedActivitySuggestions();
      const downvoted = await RemoteServices.getDownvotedActivitySuggestions();

      upvoted.forEach((s: ActivitySuggestion) => {
        if (s.id !== null && !this.votedUpSuggestionIds.includes(s.id)) {
          this.votedUpSuggestionIds.push(s.id);
        }
      });

      downvoted.forEach((s: ActivitySuggestion) => {
        if (s.id !== null && !this.votedDownSuggestionIds.includes(s.id)) {
          this.votedDownSuggestionIds.push(s.id);
        }
      });

      this.isVotesLoaded = true;
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async upVote(activitySuggestion: ActivitySuggestion) {
    if (activitySuggestion.id !== null && !this.votedUpSuggestionIds.includes(activitySuggestion.id)) {
      this.votedUpSuggestionIds.push(activitySuggestion.id);
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
    if (activitySuggestion.id !== null && this.votedUpSuggestionIds.includes(activitySuggestion.id)) {
      try {
        await RemoteServices.removeUpvoteActivitySuggestion(activitySuggestion.id);
        this.votedUpSuggestionIds = this.votedUpSuggestionIds.filter(id => id !== activitySuggestion.id);
        if (activitySuggestion.numberVotes != null) {
          activitySuggestion.numberVotes -= 1;
        }
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  async downVote(activitySuggestion: ActivitySuggestion) {
    if (activitySuggestion.id !== null && !this.votedDownSuggestionIds.includes(activitySuggestion.id)) {
      this.votedDownSuggestionIds.push(activitySuggestion.id);
      try {
        await RemoteServices.downvoteActivitySuggestion(activitySuggestion.id);
        if (activitySuggestion.numberVotes == null) {
          activitySuggestion.numberVotes = -1;
        } else {
          activitySuggestion.numberVotes -= 1;
        }
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  async removeDownVote(activitySuggestion: ActivitySuggestion) {
    if (activitySuggestion.id !== null && this.votedDownSuggestionIds.includes(activitySuggestion.id)) {
      try {
        await RemoteServices.removeDownvoteActivitySuggestion(activitySuggestion.id);
        this.votedDownSuggestionIds = this.votedDownSuggestionIds.filter(id => id !== activitySuggestion.id);
        if (activitySuggestion.numberVotes != null) {
          activitySuggestion.numberVotes += 1;
        }
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  toggleUpvote(activitySuggestion: ActivitySuggestion) {
    if (!activitySuggestion.id) return;
    const id = activitySuggestion.id;

    const votedUp = this.votedUpSuggestionIds.includes(id);
    const votedDown = this.votedDownSuggestionIds.includes(id);

    if (votedUp) {
      this.removeUpVote(activitySuggestion);
    } else {
      if (votedDown) {
        this.removeDownVote(activitySuggestion); // removes the previous downvote
      }
      this.upVote(activitySuggestion);
    }
  }

  toggleDownvote(activitySuggestion: ActivitySuggestion) {
    if (!activitySuggestion.id) return;
    const id = activitySuggestion.id;

    const votedDown = this.votedDownSuggestionIds.includes(id);
    const votedUp = this.votedUpSuggestionIds.includes(id);

    if (votedDown) {
      this.removeDownVote(activitySuggestion);
    } else {
      if (votedUp) {
        this.removeUpVote(activitySuggestion); // removes the previous upvote
      }
      this.downVote(activitySuggestion);
    }
  }

  getVoteIcon(item: ActivitySuggestion, isUpvote: boolean): string {
    if (!item.id) return 'disabled-icon';
    const votedUp = this.votedUpSuggestionIds.includes(item.id);
    const votedDown = this.votedDownSuggestionIds.includes(item.id);

    if (isUpvote) {
      return votedUp ? 'mdi-arrow-up-bold' : 'mdi-arrow-up-bold-outline';
    } else {
      return votedDown ? 'mdi-arrow-down-bold' : 'mdi-arrow-down-bold-outline';
    }
  }

  getVoteColor(item: ActivitySuggestion, isUpvote: boolean): string {
    if (!item.id) return 'blue';
    const votedUp = this.votedUpSuggestionIds.includes(item.id);
    const votedDown = this.votedDownSuggestionIds.includes(item.id);

    if (isUpvote && votedUp) return 'blue';
    if (!isUpvote && votedDown) return 'red';

    return 'black';
  }

  getVoteTooltip(item: ActivitySuggestion, isUpvote: boolean): string {
    if (item.volunteerId === this.currentUserId) return 'Cannot vote on your own suggestion';
    if (item.state !== 'IN_REVIEW') return 'Suggestion is no longer in review';

    const isVoted = isUpvote
      ? this.votedUpSuggestionIds.includes(item.id!)
      : this.votedDownSuggestionIds.includes(item.id!);

    return isVoted ? (isUpvote ? 'Remove Upvote' : 'Remove Downvote') : (isUpvote ? 'Upvote' : 'Downvote');
  }

  getVoteIconStyle(item: ActivitySuggestion) {
    if (!item.id) return {};

    const isVoted = this.votedUpSuggestionIds.includes(item.id) || this.votedDownSuggestionIds.includes(item.id);
    const isOwn = item.volunteerId === this.currentUserId;
    const isInactive = isVoted || isOwn || item.state !== 'IN_REVIEW';

    // Apply a slightly larger scale to filled and disabled icons to match the clicked button
    const scale = isInactive ? 1.2 : 1.0;

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

.downvote-btn {
  transition: border-color 0.3s ease;
}

.downvote-btn:hover:not([disabled]) .vote-icon {
  color: #FF5252 !important;  /* red accent from Vuetify */
}
</style>
