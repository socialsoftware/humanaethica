import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import pinia from './store';
import vuetify from './plugins/vuetify';

import 'vuetify/styles';
import '@mdi/font/css/materialdesignicons.css';

const app = createApp(App);
app.use(router);
app.use(pinia);
app.use(vuetify);
app.mount('#app');
