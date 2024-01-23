Cypress.Commands.add('userLogin', (username, password) => {
  cy.visit('/');

  cy.intercept('POST', '/auth/user*').as(
    'userLogin'
  );

  cy.get('[data-cy="userLoginButton"]').click();
  cy.get('[data-cy="usernameField"]').type(username);
  cy.get('[data-cy="passwordField"]').type(password);
  cy.get('[data-cy="submitButton"]').click();

  cy.wait('@userLogin');
});

Cypress.Commands.add('demoAdminLogin', () => {
  cy.visit('/');

  cy.intercept('POST', '/auth/user').as(
    'authAdmin'
  );
  cy.get('[data-cy="demoAdminLoginButton"]').click();
  cy.wait('@authAdmin');
});

Cypress.Commands.add('demoMemberLogin', () => {
  cy.visit('/');

  cy.intercept('GET', '/auth/demo/member').as(
    'authMember'
  );
  cy.get('[data-cy="demoMemberLoginButton"]').click();
  cy.wait('@authMember');
});

Cypress.Commands.add('demoVolunteerLogin', () => {
  cy.visit('/');

  cy.intercept('GET', '/auth/demo/volunteer').as(
    'authVolunteer'
  );
  cy.get('[data-cy="demoVolunteerLoginButton"]').click();
  cy.wait('@authVolunteer');
});

Cypress.Commands.add('logout', () => {

  cy.get('[data-cy="logoutButton"]').click();
});
