<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :items="users"
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
          <v-btn color="primary" dark @click="newUser" data-cy="createButton"
            >New User</v-btn
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
              @click="deleteUser(item)"
              >delete</v-icon
            >
          </template>
          <span>Delete user</span>
        </v-tooltip>
        <v-tooltip bottom v-if="item.state=='SUBMITTED'">
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
    />
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import AddUserDialog from '@/views/admin/users/AddUserDialog.vue';
import User from '@/models/user/User';

@Component({
  components: {
    'add-user-dialog': AddUserDialog,
  },
})
export default class UsersView extends Vue {
  users: User[] = [];
  addUserDialog: boolean = false;
  search: string = '';
  headers: object = [
    {
      text: 'Username',
      value: 'username',
      align: 'left',
      width: '15%',
    },
    { text: 'Name', value: 'name', align: 'left', width: '15%' },
    {
      text: 'Email',
      value: 'email',
      align: 'left',
      width: '10%',
    },
    {
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
    },
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.users = await RemoteServices.getUsers();
      this.users.forEach(user => { 
        if (user.institutionName == null) 
          user.institutionName = 'None';  
      })
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  newUser() {
    this.addUserDialog = true;
  }

  onCreatedUser(user: User) {
    this.users.unshift(user);
    this.addUserDialog = false;
  }

  onCloseDialog() {
    this.addUserDialog = false;
  }

  async deleteUser(user: User) {
    let userId = user.id;
    if (
      userId !== null &&
      confirm('Are you sure you want to delete the user?')
    ) {
      try {
        await RemoteServices.deleteUser(userId);
        this.users = this.users.filter((user) => user.id != userId);
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
        this.users = await RemoteServices.getUsers();
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>

<style lang="scss" scoped></style>
