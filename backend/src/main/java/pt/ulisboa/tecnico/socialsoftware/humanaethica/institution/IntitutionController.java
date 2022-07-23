package pt.ulisboa.tecnico.socialsoftware.humanaethica.institution;

import java.io.IOException;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.dto.InstitutionDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.dto.RegisterInstitutionDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto.RegisterUserDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Document;

@RestController
public class IntitutionController {
    
    @Autowired
    private InstitutionService institutionService;

    @Value("${figures.dir}")
    private String figuresDir;


    @GetMapping("/institutions")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<InstitutionDto> getInstitutions() {
        return institutionService.getInstitutions();
    }

    @DeleteMapping("/institutions/{institutionId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteInstitution(@PathVariable int institutionId) {
        institutionService.deleteInstitution(institutionId);
    }

    @PostMapping("/institution/register")
    public void registerInstitution(@Valid @RequestPart("institution") RegisterInstitutionDto registerInstitutionDto, @RequestParam(value="file") MultipartFile doc) throws IOException{
        Document document = new Document();
        document.setName(doc.getName());
        document.setContent(doc.getBytes());

        InstitutionDto institutionDto = new InstitutionDto(registerInstitutionDto);
        RegisterUserDto registerUserDto = new RegisterUserDto(registerInstitutionDto);
        registerUserDto.setRole(User.Role.MEMBER);
        
        institutionService.registerInstitutionMemberPair(institutionDto, document, registerUserDto);
    }

    @GetMapping("/institution/{institutionId}/document")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<byte[]> getInstitutionDocument(@PathVariable int institutionId) {
        Document doc = institutionService.getInstitutionDocument(institutionId);
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.APPLICATION_PDF;
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachement; filename=\"" + doc.getName() + "\"");
        return ResponseEntity.ok().contentType(type).headers(headers).body(doc.getContent());
    }

    @PostMapping("/institution/{institutionId}/validate")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void validateInstitution(@PathVariable int institutionId) {
        institutionService.validateInstitution(institutionId);
    }
}
