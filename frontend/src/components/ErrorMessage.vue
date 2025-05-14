<template>
  <v-dialog v-model="dialog">
    <v-alert v-model="dialog" type="error" close-text="Close Alert" dismissible>
      {{ errorMessage }}
    </v-alert>
  </v-dialog>
</template>

<script lang="ts">
import Vue from 'vue';

export default Vue.extend({
  name: 'ErrorMessage',
  data() {
    return {
      dialog: this.$store.getters.getError as boolean,
      errorMessage: this.$store.getters.getErrorMessage as string,
    };
  },
  created() {
    this.dialog = this.$store.getters.getError;
    this.errorMessage = this.$store.getters.getErrorMessage;

    this.$store.watch(
      (state, getters) => getters.getError,
      () => {
        this.dialog = this.$store.getters.getError;
        this.errorMessage = this.$store.getters.getErrorMessage;
      },
    );
  },
  watch: {
    dialog(val: boolean) {
      if (!val) {
        this.$store.dispatch('clearError');
      }
    },
  },
});
</script>

<style scoped lang="scss">
/*https://github.com/vuetifyjs/vuetify/issues/9175*/
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
