/// <reference types="vite/client" />

declare module 'showdown';
declare module 'vue-qrcode-reader';
declare module 'vue-qrcode';

declare module '*.vue' {
  import { DefineComponent } from 'vue';
  const component: DefineComponent<{}, {}, any>;
  export default component;
}
