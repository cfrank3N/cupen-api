package se.cupen.controller;

import org.apache.hc.core5.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import se.cupen.service.MediaUploadService;

@RestController
@RequestMapping("/api")
public class MediaController {
  private final MediaUploadService service;

  public MediaController(MediaUploadService service) {
    this.service = service;
  }

  @PostMapping("/image/upload")
  public ResponseEntity<String> uploadImage(@RequestParam MultipartFile file) {
    return ResponseEntity.status(HttpStatus.SC_CREATED).body(service.uploadImage(file));
  }
}
