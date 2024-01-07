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
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.InstitutionDocument;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.dto.InstitutionDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.dto.RegisterInstitutionDto;

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
        return institutionService.deleteInstitution(institutionId);
    }

    @PostMapping("/institution/register")
    public void registerInstitution(@Valid @RequestPart("institution") RegisterInstitutionDto registerInstitutionDto, @RequestParam(value = "institutionDocument") MultipartFile institutionDocument, @RequestParam(value = "memberDocument") MultipartFile memberDocument) throws IOException {
        institutionService.registerInstitutionAndMember(registerInstitutionDto, institutionDocument, memberDocument);
    }

    @GetMapping("/institution/{institutionId}/document")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<byte[]> getInstitutionDocument(@PathVariable int institutionId) {
        InstitutionDocument doc = institutionService.getInstitutionDocument(institutionId);
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
    public List<InstitutionDto>  validateInstitution(@PathVariable int institutionId) {
        return institutionService.validateInstitution(institutionId);
    }
}
