package pt.ulisboa.tecnico.socialsoftware.humanaethica.institution;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.dto.InstitutionDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.dto.RegisterInstitutionDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.UserApplicationalService;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto.RegisterUserDto;

@RestController
public class IntitutionController {
    
    @Autowired
    private InstitutionService institutionService;

    @Autowired
    private UserApplicationalService userApplicationalService;

    @PostMapping("/institution/register")
    public void registerInstitution(@Valid @RequestBody RegisterInstitutionDto registerInstitutionDto) {
        InstitutionDto institutionDto = new InstitutionDto(registerInstitutionDto);
        Institution i = institutionService.registerInstitution(institutionDto);
        RegisterUserDto registerUserDto = new RegisterUserDto(registerInstitutionDto);
        registerUserDto.setInstitutionId(i.getId());
        registerUserDto.setRole(User.Role.MEMBER);
        userApplicationalService.registerPendentMember(registerUserDto);
    }

    @PostMapping("/institution/{institutionId}/validate")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void validateInstitution(@PathVariable int institutionId) {
        institutionService.validateInstitution(institutionId);
    }
}
