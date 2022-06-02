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
    public void registerProvisoryInstitution(@Valid @RequestBody InstitutionDto institutionDto, @Valid @RequestBody RegisterUserDto registerUserDto) {
        institutionService.registerInstitution(institutionDto);
        userApplicationalService.registerUser(registerUserDto);
    }
}
