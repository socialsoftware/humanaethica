<template>
  <div class="not-found-container">
    <h1>Page Not Found</h1>
    <p>You will be redirected to the homepage in 5 seconds...</p>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

onMounted(() => {
  setTimeout(() => {
    router.push({ name: 'home' });
  }, 5000);
});
</script>

<style scoped lang="scss">
.not-found-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 80vh;
  text-align: center;
  font-size: 1.5rem;
  color: #666;
}
</style>
