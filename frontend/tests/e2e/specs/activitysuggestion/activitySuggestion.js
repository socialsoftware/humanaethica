describe('Activity Suggestion', () => {
  beforeEach(() => {
    cy.deleteAllButArs();
    cy.createDemoEntities();
    cy.createDatabaseInfoForActivitySuggestions();
  });

  afterEach(() => {
    cy.deleteAllButArs();
  });
  
  it('create activity suggestions', () => {
    const NAME = 'Digital Literacy Workshop';
    const REGION = 'Porto';
    const NUMBER = '4';
    const DESCRIPTION = 'A workshop to teach basic computer skills to seniors';

    cy.demoVolunteerLogin()
    // intercept create activity suggestion request and inject date values in the request body
    cy.intercept('POST', '/activitySuggestions', (req) => {
      req.body = {
        applicationDeadline: '2025-01-11T17:00:00+00:00',
        startingDate: '2025-01-21T09:00:00+00:00',
        endingDate: '2025-01-23T17:00:00+00:00'
      };
    }).as('registerActivitySuggestion'); //TOASK - mudar isto?
    // intercept get volunteer activity suggestions and get institutions
    cy.intercept('GET', '/activitySuggestions/volunteer/*').as('getVolunteerActivitySuggestions');
    cy.intercept('GET', '/institutions').as('getInstitutions');
    // go to create activity suggestion form
    cy.get('[data-cy="activitysuggestions"]').click();
    cy.wait('@getVolunteerActivitySuggestions');

    cy.get('[data-cy="newActivitySuggestion"]').click();
    cy.wait('@getInstitutions');

    // fill form
    cy.get('[data-cy="suggestionNameInput"]').type(NAME);
    cy.get('[data-cy="suggestionRegionInput"]').type(REGION);
    cy.get('[data-cy="suggestionParticipantsNumberInput"]').type(NUMBER);
    cy.get('[data-cy="suggestionDescriptionInput"]').type(DESCRIPTION);
    cy.get('#suggestionApplicationDeadlineInput-input').click();
    cy.selectDateTimePickerDate();
    cy.get('#suggestionStartingDateInput-input').click();
    cy.selectDateTimePickerDate();
    cy.get('#suggestionEndingDateInput-input').click();
    cy.selectDateTimePickerDate();
    // save form
    cy.get('[data-cy="saveActivitySuggestion"]').click()
    // check request was done
    cy.wait('@registerActivitySuggestion')
    // check results
    cy.get('[data-cy="volunteerActivitySuggestionsTable"] tbody tr')
      .should('have.length', 1)
      .eq(0)
      .children()
      .should('have.length', 10)
    cy.get('[data-cy="volunteerActivitySuggestionsTable"] tbody tr')
      .eq(0).children().eq(0).should('contain', NAME)
    cy.get('[data-cy="volunteerActivitySuggestionsTable"] tbody tr')
      .eq(0).children().eq(1).should('contain', "DEMO INSTITUTION")
    cy.get('[data-cy="volunteerActivitySuggestionsTable"] tbody tr')
      .eq(0).children().eq(2).should('contain', DESCRIPTION);
    cy.get('[data-cy="volunteerActivitySuggestionsTable"] tbody tr')
      .eq(0).children().eq(3).should('contain', REGION)
    cy.get('[data-cy="volunteerActivitySuggestionsTable"] tbody tr')
      .eq(0).children().eq(4).should('contain', NUMBER)
    cy.logout();
  });
});
