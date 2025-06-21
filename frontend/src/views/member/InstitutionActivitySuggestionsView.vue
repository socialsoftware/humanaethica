<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :items="activitySuggestions"
      :search="search"
      disable-pagination
      :hide-default-footer="true"
      :mobile-breakpoint="0"
      data-cy="memberActivitySuggestionsTable"
    >
      <template v-slot:item.institutionName="{ item }">
        {{ institutionName() }}
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
      <template v-slot:[`item.action`]="{ item }">
        <v-tooltip bottom v-if="item.state != 'APPROVED'">
          <template v-slot:activator="{ on }">
            <v-icon
              class="mr-2 action-button"
              color="green"
              @click="approveActivitySuggestion(item)"
              v-on="on"
              data-cy="approveActivitySuggestionButton"
              >mdi-thumb-up
            </v-icon>
          </template>
          <span>Approve Activity Suggestion</span>
        </v-tooltip>
        <v-tooltip bottom v-if="item.state != 'REJECTED'">
          <template v-slot:activator="{ on }">
            <v-icon
              class="mr-2 action-button"
              color="red"
              @click="rejectActivitySuggestion(item)"
              v-on="on"
              data-cy="rejectActivitySuggestion"
              >mdi-thumb-down
            </v-icon>
          </template>
          <span>Reject Activity Suggestion</span>
        </v-tooltip>
      </template>
    </v-data-table>
    <reject-activity-suggestion-dialog
      v-if="currentActivitySuggestion && rejectActivitySuggestionDialog"
      v-model="rejectActivitySuggestionDialog"
      :activitySuggestion="currentActivitySuggestion"
      :activitySuggestions="activitySuggestions"
      :institution="institution"
      v-on:reject-activity-suggestion="onRejectActivitySuggestion"
      v-on:close-activity-suggestion-dialog="onCloseRejectActivitySuggestionDialog"
    />
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import Institution from '@/models/institution/Institution';
import ActivitySuggestion from '@/models/activitysuggestion/ActivitySuggestion';
import RemoteServices from '@/services/RemoteServices';
import RejectActivitySuggestionDialog from '@/views/member/RejectActivitySuggestionDialog.vue';
import { show } from 'cli-cursor';

@Component({
  components: {
    'reject-activity-suggestion-dialog': RejectActivitySuggestionDialog,
  },
  methods: { show },
})
export default class InstitutionActivitySuggestionsView extends Vue {
  activitySuggestions: ActivitySuggestion[] = [];
  institution: Institution = new Institution();
  search: string = '';
  currentActivitySuggestion: ActivitySuggestion | null = null;
  rejectActivitySuggestionDialog: boolean = false;

  headers: object = [
    {
      text: 'Name',
      value: 'name',
      align: 'left',
      width: '10%',
    },
    {
      text: 'Institution',
      value: 'institutionName',
      align: 'left',
      width: '10%',
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
      text: 'Votes',
      value: 'numberVotes',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Actions',
      value: 'action',
      align: 'left',
      sortable: false,
      width: '5%',
    }
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      let userId = this.$store.getters.getUser.id;
      this.institution = await RemoteServices.getInstitution(userId);
      if (this.institution != null && this.institution.id != null)
        this.activitySuggestions = await RemoteServices.getActivitySuggestions(
            this.institution.id,
          );
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  institutionName() {
    return this.institution.name;
  }

  async approveActivitySuggestion(activitySuggestion: ActivitySuggestion) {
    if (activitySuggestion.id !== null && this.institution.id != null) {
      try {
        const result = await RemoteServices.approveActivitySuggestion(activitySuggestion.id, this.institution.id);
        const index = this.activitySuggestions.findIndex((a) => a.id === activitySuggestion.id);
        if (index !== -1) this.$set(this.activitySuggestions, index, result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  rejectActivitySuggestion(activitySuggestion: ActivitySuggestion) {
    this.currentActivitySuggestion = activitySuggestion;
    this.rejectActivitySuggestionDialog = true;
  }

  onCloseRejectActivitySuggestionDialog() {
    this.currentActivitySuggestion = null;
    this.rejectActivitySuggestionDialog = false;
  }

  async onRejectActivitySuggestion(activitySuggestion: ActivitySuggestion) {
    if (activitySuggestion.id !== null && this.institution.id != null) {
      this.currentActivitySuggestion = null;
      try {
        let userId = this.$store.getters.getUser.id;
        this.institution = await RemoteServices.getInstitution(userId);
        if (this.institution != null && this.institution.id != null)
          this.activitySuggestions = await RemoteServices.getActivitySuggestions(
              this.institution.id,
            );
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