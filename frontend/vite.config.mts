import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import vuetify from 'vite-plugin-vuetify';

export default defineConfig({
  css: {
    preprocessorOptions: {
      scss: {
        api: 'modern-compiler' // or "modern"
      }
    }
  },
  plugins: [
    vue(),
    vuetify({ autoImport: true }),
  ],
  define: { 'process.env': {} },
  resolve: {
    alias: {
      '@': '/src',
    },
  },
});