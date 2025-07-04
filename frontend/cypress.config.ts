const { defineConfig } = require('cypress');
const { Client } = require('pg');

async function queryDB(query, credentials) {
  const client = new Client(credentials);
  await client.connect();
  const result = await client.query(query);
  await client.end();

  return result.rows;
}

module.exports = defineConfig({
  defaultCommandTimeout: 10000,
  retries: 3,
  fixturesFolder: 'tests/e2e/fixtures',
  projectId: '13dkbm',
  videoCompression: false,
  screenshotsFolder: 'tests/e2e/screenshots',
  videosFolder: 'tests/e2e/videos',
  e2e: {
    setupNodeEvents(on, config) {
      on('task', {
        queryDatabase({ query, credentials }) {
          return queryDB(query, credentials);
        },
      });
    },
    baseUrl: 'http://localhost:5173',
    specPattern: 'tests/e2e/specs/**/*.{js,jsx,ts,tsx}',
    supportFile: 'tests/e2e/support/index.js',
  },
});
