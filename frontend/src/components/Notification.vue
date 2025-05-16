<template>
  <VAlert
    v-model="dialog"
    type="error"
    close-text="Close Notification"
    closable
    @update:modelValue="onDialogClose"
  >
    {{ messageList.join(', ') }}
  </VAlert>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useMainStore } from '@/store/useMainStore';

const store = useMainStore();

const dialog = computed(() => store.notification);
const messageList = computed(() => store.notificationMessageList);

function onDialogClose(val: boolean) {
  if (!val) {
    store.clearNotification();
  }
}
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
