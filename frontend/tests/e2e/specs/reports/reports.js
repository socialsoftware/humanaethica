describe('Report', () => {
  beforeEach(() => {
    cy.deleteAllButArs()
    cy.createDemoEntities();
    cy.createDatabaseInfoForReports()
  });

  afterEach(() => {
    cy.deleteAllButArs()
  });


  it('create report', () => {
    const JUSTIFICATION = 'Justification Exampe';

    cy.intercept('POST', '/activities/1/reports').as('report');
    cy.intercept('GET', '/activities/1/reports').as('reports');

    // volunteer login, report the activtiy and check that is reported
    cy.demoVolunteerLogin()
    cy.get('[data-cy="volunteerActivities"]').click();
    cy.get('[data-cy="volunteerActivitiesTable"] tbody tr')
    .eq(0)
    .find('[data-cy="reportButton"]').click();
    cy.get('[data-cy="justificationInput"]').type(JUSTIFICATION);
    cy.get('[data-cy="saveReport"]').click()
    cy.wait('@report')
    
    cy.get('[data-cy="volunteerActivitiesTable"] tbody tr')
    .eq(0)
    .children()
    .eq(6)
    .should('contain', "REPORTED")

    cy.logout();

    // admin login and check that the activity has 1 report
    cy.demoAdminLogin()
    
    cy.get('[data-cy="admin"]').click();
    cy.get('[data-cy="adminActivities"]').click();
    cy.get('[data-cy="adminActivitiesTable"] tbody tr')
    .eq(0)
    .find('[data-cy="reportedButton"]').click();
    cy.get('[data-cy="activityReportsTable"] tbody tr')
    .eq(0)
    .children()
    .eq(0)
    .should('contain', "DEMO-VOLUNTEER")

    cy.get('[data-cy="activityReportsTable"] tbody tr')
    .eq(0)
    .children()
    .eq(1)
    .should('contain', JUSTIFICATION)

    cy.get('[data-cy="closeReportList"]').click()

    cy.logout();

  });

  it('delete a report', () => {
  const JUSTIFICATION = 'Justification Exampe';

  // first volunteer create an report
  cy.demoVolunteerLogin()
  cy.get('[data-cy="volunteerActivities"]').click();
  cy.get('[data-cy="volunteerActivitiesTable"] tbody tr')
  .eq(0)
  .find('[data-cy="reportButton"]').click();
  cy.get('[data-cy="justificationInput"]').type(JUSTIFICATION);
  cy.get('[data-cy="saveReport"]').click()

  // volunteer delete the report and checks that is APPROVED again
    
  cy.get('[data-cy="volunteerActivitiesTable"] tbody tr')
  .eq(0)
  .find('[data-cy="UnReportButton"]').click();
  
  cy.get('[data-cy="volunteerActivitiesTable"] tbody tr')
  .eq(0)
  .children()
  .eq(6)
  .should('contain', "APPROVED")

  cy.logout();
  
  // admin login and check that the activity is APPROVED
  cy.demoAdminLogin()
    
  cy.get('[data-cy="admin"]').click();
  cy.get('[data-cy="adminActivities"]').click();
  cy.get('[data-cy="adminActivitiesTable"] tbody tr')
  .eq(0)
  .children()
  .eq(11)
  .should('contain', "APPROVED")
    
  });
});