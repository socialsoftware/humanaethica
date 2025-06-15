<template>
  <v-dialog v-model="dialog" max-width="700" persistent>
    <v-card>
      <v-card-title class="headline">Notifications</v-card-title>
      <v-divider></v-divider>
      <v-card-text>
        <v-list v-if="notifications.length">
          <v-list-item v-for="notification in notifications" :key="notification.id">
            <v-list-item-content>
              <v-list-item-title>{{ notification.message }}</v-list-item-title>
              <v-list-item-subtitle>{{ formatDate(notification.creationDate) }}</v-list-item-subtitle>
            </v-list-item-content>
            <v-list-item-action>
              <v-icon v-if="!notification.read" color="orange">mdi-bell-ring</v-icon>
              <v-icon v-else color="grey">mdi-bell</v-icon>
            </v-list-item-action>
          </v-list-item>
        </v-list>
        <div v-else 
          class="d-flex align-center justify-center grey--text"
          style="min-height: 100px;"
        >
          No notifications found.
        </div>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn
          color="blue-darken-1"
          variant="text"
          @click="$emit('close-notification-dialog')"
        >
          Close
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Vue, Model, Prop } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
//import Notification from '@/models/notification/Notification';

@Component
export default class NotificationDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ required: true }) readonly userId!: number;
  @Prop({ required: true }) readonly notifications!: Notification[];

  //notifications: Notification[] = [];
  currentUserID: number = -1;

  async created() {
    this.currentUserID = this.userId;
    await this.markNotificationsAsRead();
  }
  
  async markNotificationsAsRead() {
    try {
      await RemoteServices.markNotificationsAsRead(this.currentUserID);
      this.$emit('notifications-read');
    } catch (error) {
      console.error('Failed to mark notifications as read:', error);
    }
  }

  formatDate(dateStr: string): string {
    return new Date(dateStr).toLocaleString();
  }
}
</script>
