describe('Admin', () => {
  beforeEach(() => {
    cy.deleteUsersButArs();

    cy.userLogin("ars", "ars")
  });

  afterEach(() => {
    cy.logout();
  });

  it('see users, create user, delete user', () => {
    cy.intercept('GET', '/users').as(
      'users'
    );
    cy.get('[data-cy="admin"]').click();
    cy.get('[data-cy="adminUsers"]').click();
    cy.wait('@users');

    cy.get('[data-cy="deleteButton"]').should('have.length', 1)


    cy.intercept('POST', '/users/register').as(
      'userRegister'
    );
    cy.get('[data-cy="createButton"]').click();
    cy.get('[data-cy="userNameInput"]').type('Rui');
    cy.get('[data-cy="userUsernameInput"]').type('rui');
    cy.get('[data-cy="userEmailInput"]').type('rui@mail.mail');
    cy.get('[data-cy="userRoleSelect"]').parent().click()
    cy.get(".v-menu__content").contains("VOLUNTEER").click()
    cy.get('[data-cy="saveButton"]').click();
    cy.wait('@userRegister')

    cy.get('[data-cy="deleteButton"]').should('have.length', 2)

    cy.intercept('DELETE', '/users/*').as(
      'deleteUser'
    );
    cy.get('[data-cy="deleteButton"]').first().click();
    cy.wait('@deleteUser');

    cy.get('[data-cy="deleteButton"]').should('have.length', 1)
  });


});
