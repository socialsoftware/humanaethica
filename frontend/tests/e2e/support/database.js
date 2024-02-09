const credentials = {
  user: Cypress.env('psql_db_username'),
  host: Cypress.env('psql_db_host'),
  database: Cypress.env('psql_db_name'),
  password: Cypress.env('psql_db_password'),
  port: Cypress.env('psql_db_port'),
};

Cypress.Commands.add('deleteAllButArs', () => {
  cy.task('queryDatabase', {
    query: "DELETE FROM enrollment",
    credentials: credentials,
  })
  cy.task('queryDatabase', {
    query: "DELETE FROM ACTIVITY",
    credentials: credentials,
  })
  cy.task('queryDatabase', {
    query: "DELETE FROM AUTH_USERS WHERE NOT (username = 'ars')",
    credentials: credentials,
  });
  cy.task('queryDatabase', {
    query: "DELETE FROM USERS WHERE NOT (name = 'ars')",
    credentials: credentials,
  });
});

Cypress.Commands.add('createActivitiesForEnrollments', () => {
  cy.task('queryDatabase', {
    query: "INSERT INTO " +
      "activity (id, application_deadline, creation_date, description, ending_date, name, participants_number_limit, region, starting_date, state, institution_id)" +
      "VALUES ('1', '2024-08-06 17:58:21.402146', '2024-08-06 17:58:21.402146', 'Inserted-by-sql', '2024-08-08 17:58:21.402146', 'A1', '1', 'Lisbon',  '2024-08-07 17:58:21.402146', 'APPROVED', '1')",
    credentials: credentials,
  })
});

