<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :items="institution.activities"
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
        </v-card-title>
      </template>
      <template v-slot:[`item.themes`]="{ item }">
        <v-chip v-for="theme in item.themes">
          {{ theme.name }}
        </v-chip>
      </template>
      <template v-slot:[`item.volunteers`]="{ item }">
        <v-chip v-for="volunteer in item.volunteers">
          {{ volunteer.name }}
        </v-chip>
      </template>
      <template v-slot:[`item.action`]="{ item }">
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              class="mr-2 action-button"
              color="black"
              v-on="on"
              @click="dialogEditActivity = true"
            >
              description
            </v-icon>
          </template>
          <v-row justify="center">
            <v-dialog v-model="dialogEditActivity" persistent width="1024">
              <v-card>
                <v-card-title>
                  <span class="text-h5">Edit Activity</span>
                </v-card-title>
                <v-card-text>
                  <v-container>
                    <v-row>
                      <v-col cols="12" sm="6" md="4">
                        <v-text-field
                          label="Name"
                          required
                          v-model="item.name"
                        ></v-text-field>
                      </v-col>
                      <v-col cols="12">
                        <v-text-field
                          label="Region"
                          required
                          v-model="item.region"
                        ></v-text-field>
                      </v-col>
                      <v-col cols="12" sm="6">
                        <v-select
                          label="Themes"
                          v-model="item.themes"
                          :items="themes"
                          multiple
                          return-object
                          item-text="name"
                          item-value="name"
                          required
                        />
                      </v-col>
                    </v-row>
                  </v-container>
                </v-card-text>
                <v-card-actions>
                  <v-spacer></v-spacer>
                  <v-btn
                    color="blue-darken-1"
                    variant="text"
                    @click="dialogEditActivity = false"
                  >
                    Close
                  </v-btn>
                  <v-btn
                    color="blue-darken-1"
                    variant="text"
                    @click="updateActivity(item)"
                  >
                    Save
                  </v-btn>
                </v-card-actions>
              </v-card>
            </v-dialog>
          </v-row>
          <span>Edit Activity</span>
        </v-tooltip>
      </template>
    </v-data-table>
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Theme from '@/models/theme/Theme';
import Institution from '@/models/institution/Institution';
import Member from '@/models/member/Member';
import User from '@/models/user/User';
import Activity from '@/models/activity/Activity';

@Component({
  components: {},
})
export default class ManageOwnActivitiesView extends Vue {
  institution: Institution = new Institution();
  themes: Theme[] = [];
  search: string = '';
  dialogEditActivity: boolean = false;
  headers: object = [
    {
      text: 'Name',
      value: 'name',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Region',
      value: 'region',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Themes',
      value: 'themes',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Volunteers',
      value: 'volunteers',
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
      text: 'Creation Date',
      value: 'creationDate',
      align: 'left',
      width: '5%',
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
      let userId = this.$store.getters.getUser.id;
      this.institution = await RemoteServices.getInstitution(userId);
      this.themes = await RemoteServices.getThemes();
      console.log(this.institution.name);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async updateActivity(activity: Activity) {
    let activityId = activity.id;
    if (
      activityId !== null &&
      confirm('Are you sure you want to edit this activity?')
    ) {
      try {
        await RemoteServices.updateActivityAsMember(activityId, activity);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>

<style lang="scss" scoped></style>
