describe('Activity Suggestion', () => {
  beforeEach(() => {
    cy.deleteAllButArs();
    cy.createDemoEntities();
    //TODO
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
        applicationDeadline: '2025-01-10T17:00:00+00:00',
        startingDate: '2025-01-21T09:00:00+00:00',
        endingDate: '2025-01-23T17:00:00+00:00'
      };
    }).as('register suggestion'); //TOASK - mudar isto?
    // intercept ???
    // TODO
    // go to create activity suggestion form
    cy.get('[data-cy="activitysuggestions"]').click();

    cy.get('[data-cy="newActivitySuggestion"]').click();
    // cy.wait TODO

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
    

    
  });
});
