<template>
    <v-card class="container" elevation="11">
        <h2>Theme Registration</h2>
        <v-form ref="form" lazy-validation>
            <v-text-field
                    v-model="themeName"
                    label="Name"
                    required
                    :rules="[(v) => !!v || 'Theme Name is required']"
                    @input="$v.themeName.$touch()"
                    @blur="$v.themeName.$touch()"
            ></v-text-field>
            <v-btn
                    class="mr-4"
                    color="orange"
                    @click="submit"
                    :disabled="
          !(
            themeName !== ''
          )
        "
            >
                submit
            </v-btn>
            <v-btn @click="clear"> clear </v-btn>
        </v-form>
    </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
@Component({
    components: {},
})
export default class RegisterThemeView extends Vue {
    themeName: string = '';
    async submit() {
        try {
            if (
                this.themeName.length == 0
            ) {
                await this.$store.dispatch(
                    'error',
                    'Missing information, please check the form again'
                );
                return;
            } else {
                await RemoteServices.registerTheme({
                    themeName: this.themeName,
                });
                await this.$router.push({ name: 'home' });
            }
        } catch (error) {
            await this.$store.dispatch('error', error);
        }
        await this.$store.dispatch('clearLoading');
    }
    readFile() {
        RemoteServices.getForm();
    }
    clear() {
        this.themeName = '';
    }
}
</script>

<style lang="scss" scoped>
.container {
  margin-top: 2rem !important;
  padding: 3rem !important;
  width: 60%;
  flex-direction: row;
  flex-wrap: wrap;
  justify-content: center;
  align-items: stretch;
  align-content: center;
  background-color: rgba(255, 255, 255);
}
.divider {
  margin-top: 2rem !important;
  margin-bottom: 2rem !important;
}
h2 {
  color: black;
  opacity: 80%;
  font-family: 'Open Sans', sans-serif;
  text-align: left;
  font-size: 20px;
  font-weight: 500;
  line-height: 40px;
  margin: 0 0 16px;
}
</style>