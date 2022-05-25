describe('Volunteer', () => {
  beforeEach(() => {
    cy.demoVolunteerLogin()
  });

  afterEach(() => {
    cy.logout();
  });

  it('close', () => {
    cy.get('[data-cy="volunteer"]').click();
  });
});
