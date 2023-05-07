<template>
    <v-card class="table">
        <v-data-table
                :headers="headers"
                :items="themes"
                :search="search"
                disable-pagination
                :hide-default-footer="true"
                :mobile-breakpoint="0"
        >
            <template v-slot:top>
                <v-card-title>
                    <v-text-field
                            v-model="search"
                            append-icon="search"
                            label="Search"
                            class="mx-2"
                    />
                </v-card-title>
            </template>
        </v-data-table>
    </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Theme from '@/models/theme/Theme';
@Component({
    components: {},
})
export default class ThemesView extends Vue {
    themes: Theme[] | [] = [];
    search: string = '';
    headers: object = [
        { text: 'Name', value: 'name', align: 'left', width: '25%' },
    ];
    async created() {
        await this.$store.dispatch('loading');
        try {
            this.themes = await RemoteServices.getThemes();
        } catch (error) {
            await this.$store.dispatch('error', error);
        }
        await this.$store.dispatch('clearLoading');
    }
}
</script>

<style lang="scss" scoped></style>