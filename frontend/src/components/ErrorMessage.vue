<template>
  <VDialog v-model="dialog">
    <VAlert
      v-model="dialog"
      type="error"
      close-text="Close Alert"
      closable
      @update:modelValue="onDialogClose"
    >
      {{ errorMessage }}
    </VAlert>
  </VDialog>
</template>

<script setup lang="ts">
import { computed, watch } from 'vue';
import { useMainStore } from '@/store/useMainStore';

const store = useMainStore();

// Computed bindings to the Pinia store
const dialog = computed(() => store.error);
const errorMessage = computed(() => store.errorMessage);

// Watch dialog visibility and clear error when closed
function onDialogClose(val: boolean) {
  if (!val) {
    store.clearError();
  }
}

// Optional: watch for changes (e.g., to trigger transitions or logs)
watch(() => store.error, () => {
  // console.log('Error state changed');
});
</script>

<style scoped lang="scss">
.v-dialog__container {
  display: unset !important;
}

.v-alert {
  z-index: 9999;
  position: absolute;
  left: 20px;
  top: 80px;
  width: calc(100% - 40px);
}
</style>
