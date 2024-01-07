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

    cy.get('[data-cy="users"]').should('have.length', 1);
  });


});
