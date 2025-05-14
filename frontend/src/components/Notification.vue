<template>
  <v-alert
    v-model="dialog"
    type="error"
    close-text="Close Notification"
    dismissible
  >
    {{ messageList.join(', ') }}
  </v-alert>
</template>

<script lang="ts">
import { defineComponent } from 'vue';

export default defineComponent({
  name: 'AppNotification',
  data() {
    return {
      dialog: this.$store.getters.getNotification as boolean,
      messageList: this.$store.getters.getNotificationMessageList as string[],
    };
  },
  watch: {
    dialog(newVal: boolean) {
      if (!newVal) {
        this.$store.dispatch('clearNotification');
      }
    },
  },
  created() {
    this.$store.watch(
      (state, getters) => getters.getNotification,
      () => {
        this.dialog = this.$store.getters.getNotification;
        this.messageList = this.$store.getters.getNotificationMessageList;
      },
    );
  },
});
</script>

<style scoped lang="scss">
.v-alert {
  z-index: 9999;
  position: absolute;
  left: 20px;
  top: 80px;
  width: calc(100% - 40px);
}
</style>
