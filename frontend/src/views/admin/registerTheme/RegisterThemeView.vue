<template>
    <v-card>
        <v-form ref="form" lazy-validation>
            <v-card-title>
                <span class="headline">Create a new Theme</span>
            </v-card-title>
            <v-card-text class="text-left">
                <v-text-field
                    v-model="theme.name"
                    label="Name"
                    data-cy="themeNameInput"
                    :rules="[(value) => !!value || 'Name is required']"
                    required
                />
            </v-card-text>
            <v-card-actions>
                <v-spacer />
                <v-btn color="blue darken-1" @click="clear" data-cy="clearButton"
                >Clear</v-btn
                >
                <v-btn color="blue darken-1" @click="submit" data-cy="submitButton"
                >Add</v-btn
                >
            </v-card-actions>
        </v-form>
    </v-card>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Theme from '@/models/theme/Theme';
@Component({
    components: {},
})
export default class RegisterActivityView extends Vue {
    theme: Theme = new Theme();
    async submit() {
        //console.log(this.themes.length);
        //console.log(this.themes[0].name);
        try {
            await RemoteServices.registerTheme(this.theme);
            await this.$router.push({ name: 'home' });
        } catch (error) {
            await this.$store.dispatch('error', error);
        }
        await this.$store.dispatch('clearLoading');
    }
    clear() {
        this.theme.name = '';
    }
}
</script>

<style lang="scss" scoped>
.container {
    background-color: grey;
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