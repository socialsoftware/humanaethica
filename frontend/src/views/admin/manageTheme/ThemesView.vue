<template>
    <v-card class="table">
        <v-data-table
                :headers="headers"
                :items="theme"
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
            <template v-slot:[`item.action`]="{ item }">
                <v-tooltip bottom v-if="!item.state">
                    <template v-slot:activator="{ on }">
                        <v-icon
                                class="mr-2 action-button"
                                color="green"
                                v-on="on"
                                data-cy="validateButton"
                        >mdi-check-bold</v-icon
                        >
                    </template>
                    <span>Validate theme</span>
                </v-tooltip>
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
    theme: Theme[] = [];
    search: string = '';
    headers: object = [
        { text: 'Name', value: 'name', align: 'left', width: '25%' },
    ];
    async created() {
        await this.$store.dispatch('loading');
        try {
            this.theme = await RemoteServices.getThemes();
        } catch (error) {
            await this.$store.dispatch('error', error);
        }
        await this.$store.dispatch('clearLoading');
    }
}
</script>

<style lang="scss" scoped></style>