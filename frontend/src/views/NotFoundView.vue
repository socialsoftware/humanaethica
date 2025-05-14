<template>
  <div class="container">Not Found</div>
</template>

<script lang="ts">
export default {
  name: 'NotFoundView',

  created(this: any) {
    setTimeout(async () => {
      await this.$router.push({ name: 'home' });
    }, 5000);
  },
};
</script>

<style lang="scss" scoped></style>
