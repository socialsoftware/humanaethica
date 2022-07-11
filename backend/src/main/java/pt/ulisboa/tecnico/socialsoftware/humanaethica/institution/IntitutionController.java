package pt.ulisboa.tecnico.socialsoftware.humanaethica.institution;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @Value("${figures.dir}")
    private String figuresDir;

    @PostMapping("/institution/register")
    public void registerInstitution(@Valid @RequestBody RegisterInstitutionDto registerInstitutionDto, @RequestParam("file") MultipartFile doc) throws IOException{
        int lastIndex = Objects.requireNonNull(doc.getContentType()).lastIndexOf('/');
        String type = doc.getContentType().substring(lastIndex + 1);

        InstitutionDto institutionDto = new InstitutionDto(registerInstitutionDto);
        Institution i = institutionService.registerInstitution(institutionDto, type);

        String url = i.getDocument().getUrl();

        Files.copy(doc.getInputStream(), getTargetLocation(url), StandardCopyOption.REPLACE_EXISTING);

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

    private Path getTargetLocation(String url) {
        String fileLocation = figuresDir + url;
        return Paths.get(fileLocation);
    }
}
