<template>
  <v-overlay :value="loading" absolute>
    <v-progress-circular indeterminate size="64" />
  </v-overlay>
</template>

<script lang="ts">
import Vue from 'vue';

export default Vue.extend({
  name: 'AppLoading',
  data() {
    return {
      loading: this.$store.getters.getLoading,
    };
  },
  created() {
    // Inicializa e mantém sincronizado com o getter
    this.$store.watch(
      (state, getters) => getters.getLoading,
      (value: boolean) => {
        this.loading = value;

        if (!value) {
          this.$store.dispatch('clearLoading');
        }
      },
    );
  },
});
</script>
