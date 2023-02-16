package pt.ulisboa.tecnico.socialsoftware.humanaethica.institution;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Document;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.dto.InstitutionDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.dto.RegisterInstitutionDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.UserDocument;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto.RegisterUserDto;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@RestController
public class InstitutionController {
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
    public List<InstitutionDto> deleteInstitution(@PathVariable int institutionId) {
        institutionService.deleteInstitution(institutionId);
        return institutionService.getInstitutions();
    }

    @PostMapping("/institution/register")
    public void registerInstitution(@Valid @RequestPart("institution") RegisterInstitutionDto registerInstitutionDto, @RequestParam(value = "institutionDoc") MultipartFile institutionDoc, @RequestParam(value = "memberDoc") MultipartFile memberDoc) throws IOException {
        Document document = new Document();
        document.setName(institutionDoc.getName());
        document.setContent(institutionDoc.getBytes());

        UserDocument memberDocument = new UserDocument();
        memberDocument.setName(memberDoc.getName());
        memberDocument.setContent(memberDoc.getBytes());

        InstitutionDto institutionDto = new InstitutionDto(registerInstitutionDto);
        RegisterUserDto registerUserDto = new RegisterUserDto(registerInstitutionDto);
        registerUserDto.setRole(User.Role.MEMBER);

        institutionService.registerInstitutionMemberPair(institutionDto, document, registerUserDto, memberDocument);
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

    @GetMapping("/document/form")
    public ResponseEntity<byte[]> getForm() throws IOException {
        File f = new File(System.getProperty("user.dir") + "/src/main/resources/termo_responsabildade.pdf");
        byte[] b = Files.readAllBytes(f.toPath());
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.APPLICATION_PDF;
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachement; filename=\"" + f.getName() + "\"");
        return ResponseEntity.ok().contentType(type).headers(headers).body(b);
    }

    @PostMapping("/institution/{institutionId}/validate")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void validateInstitution(@PathVariable int institutionId) {
        institutionService.validateInstitution(institutionId);
    }
}
