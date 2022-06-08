package pt.ulisboa.tecnico.socialsoftware.humanaethica.institution;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.dto.InstitutionDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.UserApplicationalService;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto.RegisterUserDto;

@RestController
public class IntitutionController {
    
    @Autowired
    private InstitutionService institutionService;

    @Autowired
    private UserApplicationalService userApplicationalService;

    @PostMapping("/institutions/register")
    public void registerInstitution(@Valid @RequestBody InstitutionDto institutionDto, @Valid @RequestBody RegisterUserDto registerUserDto) {
        institutionService.registerInstitution(institutionDto);
        userApplicationalService.registerPendentMember(registerUserDto);
    }

    @PostMapping("/institutions/{institutionId}/validate")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void validateInstitution(@PathVariable int institutionId) {
        institutionService.validateInstitution(institutionId);
    }
}
