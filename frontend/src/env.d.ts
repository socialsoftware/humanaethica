/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_APP_NAME: string;
  // add more as needed
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}

import 'vue-router';

declare module 'vue-router' {
  interface RouteMeta {
    title?: string;
    requiredAuth?: 'None' | 'Admin' | 'Member' | 'Volunteer';
  }
}