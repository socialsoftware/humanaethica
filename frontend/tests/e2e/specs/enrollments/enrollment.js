describe('Activity', () => {
  beforeEach(() => {
    cy.deleteAllButArs()
    cy.demoMemberLogin()
    cy.logout();
    cy.createActivitiesForEnrollments()
  });

  afterEach(() => {
    cy.deleteAllButArs()
  });

  it('create enrollment', () => {
    const MOTIVATION = 'I am very keen to help other people.';

    cy.intercept('POST', '/activities/1/enrollments').as('enroll');

    // member login and check that there is activity with 0 enrollments
    cy.demoMemberLogin()
    cy.get('[data-cy="institution"]').click();
    cy.get('[data-cy="activities"]').click();
    cy.get('[data-cy="memberActivitiesTable"] tbody tr')
      .should('have.length', 1)
      .eq(0)
      .then(($row) => {
        cy.wrap($row).children()
          .should('have.length', 13)
          .each(($column, columnIndex) => {
            if (columnIndex === 3) {
              cy.wrap($column).invoke('text').should('equal', '0');
            }
          });
      })
    cy.logout();

    // volunteer login, creates enrollments
    cy.demoVolunteerLogin()
    cy.get('[data-cy="volunteerActivities"]').click();
    cy.get('[data-cy="applyButton"]').click();
    cy.get('[data-cy="motivationInput"]').type(MOTIVATION);
    cy.get('[data-cy="saveEnrollment"]').click()
    cy.wait('@enroll')
    cy.logout()

    // member login and check that there is activity with 1 enrollment
    cy.demoMemberLogin()
    cy.get('[data-cy="institution"]').click();
    cy.get('[data-cy="activities"]').click();
    cy.get('[data-cy="memberActivitiesTable"] tbody tr')
      .should('have.length', 1)
      .eq(0)
      .then(($row) => {
        cy.wrap($row).children()
          .should('have.length', 13)
          .each(($column, columnIndex) => {
            if (columnIndex === 3) {
              cy.wrap($column).invoke('text').should('equal', '1');
            }
          });
      })
    cy.logout();
  });
});
