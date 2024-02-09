Cypress.Commands.add('selectDateTimePickerDate', () => {
  cy.get(
    '.datetimepicker > .datepicker > .datepicker-buttons-container > .datepicker-button > .datepicker-button-content'
  )
    .first()
    .click({force: true});
});


