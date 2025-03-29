describe('VolunteerProfile', () => {
  beforeEach(() => {
    cy.createDemoEntities();
    cy.createDatabaseInfoForParticipations()
  });

  afterEach(() => {
      cy.deleteAllButArs();
  });

  it('create volunteerProfile', () => {
      const SHORT_BIO = 'This is an example of a short bio';
      cy.demoVolunteerLogin()

      // intercept post volunteer profile
      cy.intercept('POST', '/profile/volunteer').as('register');
      
      // intercept get operations
      cy.intercept('GET', '/participations/volunteer').as('getParticipations');
      cy.intercept('GET', '/activities').as('getActivities');
      cy.intercept('GET', '/profile/volunteer/3').as('getVolunteerProfile');

      
  })
    

  it('close', () => {

  });
});
