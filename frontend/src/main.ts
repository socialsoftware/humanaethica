import { createApp } from 'vue';
import App from './App.vue';
import { createPinia } from 'pinia';
import router from './router';
import vuetify from './plugins/vuetify';

import 'vuetify/styles';
import '@/assets/css/_global.scss';
import '@mdi/font/css/materialdesignicons.css';

const app = createApp(App);

app.use(router);
app.use(createPinia());
app.use(vuetify);

app.mount('#app');
