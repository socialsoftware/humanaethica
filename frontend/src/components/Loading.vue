<template>
  <VOverlay :model-value="loading" absolute>
    <VProgressCircular indeterminate size="64" />
  </VOverlay>
</template>

<script setup lang="ts">
import { computed, watch } from 'vue';
import { useMainStore } from '@/store/useMainStore';

const store = useMainStore();

const loading = computed(() => store.loading);

watch(loading, (val) => {
  if (!val) {
    store.clearLoading();
  }
});
</script>
