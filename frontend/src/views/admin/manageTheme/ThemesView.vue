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
        </v-card-title>
      </template>
    </v-data-table>
    <add-theme
      v-if="addTheme"
      v-model="addTheme"
      v-on:user-created="onCreatedTheme"
      v-on:close-dialog="onCloseDialog"
    />
    <!--<template v-slot:[`item.action`]="{ item }">
                <v-tooltip bottom v-if="item.state != 'DELETED'">
                    <template v-slot:activator="{ on }">
                        <v-icon
                            class="mr-2 action-button"
                            color="red"
                            v-on="on"
                            data-cy="deleteButton"
                            @click="deleteUser(item)"
                        >delete</v-icon
                        >
                    </template>
                    <span>Delete user</span>
                </v-tooltip>
                <v-tooltip bottom>
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
                            @click="validateUser(item)"
                        >mdi-check-bold</v-icon
                        >
                    </template>
                    <span>Validate user</span>
                </v-tooltip>
            </template>
        </v-data-table>

        <add-user-dialog
            v-if="addUserDialog"
            v-model="addUserDialog"
            v-on:user-created="onCreatedUser"
            v-on:close-dialog="onCloseDialog"
        />-->
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import AddTheme from '@/views/admin/manageTheme/AddTheme.vue';
import Theme from '@/models/theme/Theme';

@Component({
  components: { 'add-theme': AddTheme },
})
export default class ThemeView extends Vue {
  themes: Theme[] = [];
  addTheme: boolean = false;
  search: string = '';
  headers: object = [
    {
      text: 'Id',
       value: 'id',
      align: 'left',
      width: '15%',
    },
    { text: 'Name', value: 'name', align: 'left', width: '15%' },
    {
      text: 'State',
      value: 'state',
      align: 'left',
      width: '10%',
    },
    /*{
            text: 'Role',
            value: 'role',
            align: 'left',
            width: '5%',
        },
        {
            text: 'Institution',
            value: 'institutionName',
            align: 'left',
            width: '15%',
        },
        {
            text: 'Active',
            value: 'active',
            align: 'left',
            width: '15%',
        },
        {
            text: 'State',
            value: 'state',
            align: 'left',
            width: '10%',
        },
        {
            text: 'Type',
            value: 'type',
            align: 'left',
            width: '10%',
        },
        {
            text: 'Creation Date',
            value: 'creationDate',
            align: 'left',
            width: '10%',
        },
        {
            text: 'Last Access',
            value: 'lastAccess',
            align: 'left',
            width: '30%',
        },
        {
            text: 'Delete',
            value: 'action',
            align: 'left',
            sortable: false,
            width: '5%',
        },*/
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

  onCreatedTheme(theme: Theme) {
    this.themes.unshift(theme);
    this.addTheme = false;
  }

  onCloseDialog() {
    this.addTheme = false;
  }

  /*async deleteUser(user: User) {
        let userId = user.id;
        if (
            userId !== null &&
            confirm('Are you sure you want to delete the user?')
        ) {
            try {
                //this.users = await RemoteServices.deleteUser(userId);
            } catch (error) {
                await this.$store.dispatch('error', error);
            }
        }
    }

    async getDocument(user: User) {
        let userId = user.id;
        if (userId !== null) {
            try {
                await RemoteServices.getUserDocument(userId);
            } catch (error) {
                await this.$store.dispatch('error', error);
            }
        }
    }

    async validateUser(user: User) {
        let userId = user.id;
        if (
            userId !== null &&
            confirm('Are you sure you want to validate this user?')
        ) {
            try {
                await RemoteServices.validateUser(userId);
                //this.users = await RemoteServices.getUsers();
            } catch (error) {
                await this.$store.dispatch('error', error);
            }
        }
    }*/
}
</script>

<style lang="scss" scoped></style>
