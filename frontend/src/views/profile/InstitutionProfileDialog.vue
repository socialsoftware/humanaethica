<template>

</template>
<script lang="ts">
import { Vue, Component, Prop, Model } from 'vue-property-decorator';
import InstitutionProfile from '@/models/profile/InstitutionProfile';
import Assessment from '@/models/assessment/Assessment';
import RemoteServices from '@/services/RemoteServices';
import VueCtkDateTimePicker from 'vue-ctk-date-time-picker';
import 'vue-ctk-date-time-picker/dist/vue-ctk-date-time-picker.css';
import { ISOtoString } from '@/services/ConvertDateService';


Vue.component('VueCtkDateTimePicker', VueCtkDateTimePicker);
@Component({
  methods: { ISOtoString },
})
export default class InstitutionProfileDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Array, required: true }) readonly assessments!: Assessment[];

  institutionProfile: InstitutionProfile = new InstitutionProfile();
  search: string = '';
  selAssessment: Assessment[] = [];
  shortDes: string = ''
  cypressCondition: boolean = false;
  
  headers = [
    { text: 'Volunteer Name', value: 'volunteerName' },
    { text: 'Review', value: 'review' },
    { text: 'Review Date', value: 'reviewDate' }
  ];

  async created() {
  }

}
</script>

<style scoped lang="scss">
.v-input--underlined .v-input__control {
  border-bottom: 2px solid #1976D2; /* Adjust the color or thickness as needed */
}

</style>
