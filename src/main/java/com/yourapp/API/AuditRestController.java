package com.yourapp.API;

import com.yourapp.dto.AuditStartRequest;
import com.yourapp.dto.AuditStartResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/audits")
@CrossOrigin
public class AuditRestController {

    @PostMapping(
            value = "/start",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public AuditStartResponse startAudit(
            @RequestPart("data") AuditStartRequest request,
            @RequestPart("files") List<MultipartFile> files
    ) {

        System.out.println("=== AUDIT START REQUEST ===");
        System.out.println("Project ID: " + request.getProjectId());
        System.out.println("Partner ID: " + request.getPartnerId());
        System.out.println("Files received: " + files.size());

        files.forEach(f ->
                System.out.println("File: " + f.getOriginalFilename())
        );

        // Pour l'instant on simule un audit créé
        return new AuditStartResponse(
                1L,
                "Audit reçu avec succès"
        );
    }
}
