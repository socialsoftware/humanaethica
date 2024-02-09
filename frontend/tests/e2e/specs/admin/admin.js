describe('Admin', () => {
  beforeEach(() => {
    cy.deleteAllButArs();
  });

  afterEach(() => {
    cy.deleteAllButArs();
  });

  it('admin demon login and get users', () => {
    cy.intercept('GET', '/users').as(
      'users'
    );

    cy.demoAdminLogin();

    cy.get('[data-cy="admin"]').click();
    cy.get('[data-cy="adminUsers"]').click();
    cy.wait('@users');

    cy.get('[data-cy="users"]').should('have.length', 1);

    cy.logout();
  });

  it('ars login and get users', () => {
    cy.intercept('GET', '/users').as(
      'users'
    );

    cy.userLogin("ars", "ars")

    cy.get('[data-cy="admin"]').click();
    cy.get('[data-cy="adminUsers"]').click();
    cy.wait('@users');

    cy.get('[data-cy="users"]').should('have.length', 1);

    cy.logout();
  });




});
