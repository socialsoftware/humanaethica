<template>
  <div class="error-banner-container" v-if="dialog">
    <VAlert
      type="error"
      closable
      close-text="Close Alert"
      @update:modelValue="onDialogClose"
      class="error-banner"
    >
      {{ errorMessage }}
    </VAlert>
  </div>
</template>

<script setup lang="ts">
import { computed, watch } from 'vue';
import { useMainStore } from '@/store/useMainStore';

const store = useMainStore();

const dialog = computed(() => store.error);
const errorMessage = computed(() => store.errorMessage);

function onDialogClose(val: boolean) {
  if (!val) {
    store.clearError();
  }
}

watch(() => store.error, () => {});
</script>

<style scoped lang="scss">
.error-banner-container {
  position: fixed;
  top: 24px;
  left: 0;
  width: 100vw;
  z-index: 9999;
  display: flex;
  justify-content: center;
  pointer-events: none;
}

.error-banner {
  width: 600px;
  max-width: 90vw;
  pointer-events: all;
}
</style>
