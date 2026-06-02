package se.cupen.service;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpStatus;

import se.cupen.exception.ValidationException;

@Service
public class MediaUploadService {

  private final Cloudinary cloudinary;

  public MediaUploadService(Cloudinary cloudinary) {
    this.cloudinary = cloudinary;
  }

  public String uploadImage(MultipartFile file) {
    try {
      Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
          "resource_type", "auto"));

      return (String) result.get("secure_url");
    } catch (Exception e) {
      throw new ValidationException("Bad boy", HttpStatus.SC_BAD_REQUEST);
    }
  }

}
