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

  it('suspend activity', () => {
    const JUSTIFICATION = 'This activity will be suspended for testing.'

    cy.createDemoEntities()
    cy.createDatabaseInfoForEnrollments()
    cy.demoAdminLogin()

    cy.intercept('GET', '/institutions').as('activities')

    // List activities.
    cy.get('[data-cy="admin"]').click()
    cy.get('[data-cy="adminActivities"]').click()
    cy.wait('@activities')

    // Suspend first activity.
    cy.get('[data-cy="suspendButton"]').first().click()
    cy.get('[data-cy="suspensionReasonInput"]').type(JUSTIFICATION)
    cy.get('[data-cy="suspendActivity"]').click()

    // Check if first activity is suspended.
    cy.get('[data-cy="adminActivitiesTable"] tbody tr')
      .should('have.length', 3)
      .should('contain', "SUSPENDED")

    cy.logout();
  });
});
