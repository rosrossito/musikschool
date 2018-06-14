package com.epam.charity.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.cloudinary.utils.ObjectUtils;
import com.epam.charity.config.log4j.LogInjector;
import com.epam.charity.service.CloudinaryServiceImpl.UploadStatus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created on 11/15/2017.
 *
 * @author Serhii Petrusha
 */
public class CloudinaryServiceImplTest {

  private Cloudinary cloudinary;

  private CloudinaryService cloudinaryService;

  @Before
  public void setUp() throws Exception {

    cloudinary = Mockito.mock(Cloudinary.class);
    cloudinaryService = new CloudinaryServiceImpl(cloudinary);
    LogInjector logInjector = new LogInjector();
    logInjector.postProcessBeforeInitialization(cloudinaryService, null);

  }

  @Test
  public void uploadImage_returnsValidURLonTheImage() throws Exception {

    String expectedUrl = "good";

    byte[] bytes = " ".getBytes();

    Uploader uploader = Mockito.mock(Uploader.class);
    Mockito.when(cloudinary.uploader()).thenReturn(uploader);
    Mockito.when(uploader.upload(any(byte[].class), anyMap()))
        .thenReturn(ObjectUtils.asMap("url", "good"));

    UploadStatus uploadImage = cloudinaryService.uploadImage(bytes);
    assertEquals(expectedUrl, uploadImage.getURL());

  }


  @Test
  public void updateImage_callsUploaderWithValidParams() throws Exception {

    String expectedPublicID = "ID";

    MultipartFile file = Mockito.mock(MultipartFile.class);

    Uploader uploader = Mockito.mock(Uploader.class);
    Mockito.when(cloudinary.uploader()).thenReturn(uploader);
    Mockito
        .when(uploader.upload(any(),
            eq(ObjectUtils.asMap("public_id", expectedPublicID, "overwrite", true))))
        .thenReturn(ObjectUtils.asMap("public_id", expectedPublicID));
    UploadStatus updateImage = cloudinaryService.updateImage(file, expectedPublicID);

    assertEquals(expectedPublicID, updateImage.getPublicID());

  }

  @Test
  public void deleteImage_callsDestroyMethodOfUploader() throws Exception {

    Uploader uploader = Mockito.mock(Uploader.class);
    Mockito.when(cloudinary.uploader()).thenReturn(uploader);
    Mockito.when(uploader.destroy(anyString(), anyMap())).thenReturn(ObjectUtils.asMap());

    cloudinaryService.deleteImage("file");

    Mockito.verify(cloudinary.uploader()).destroy(anyString(), anyMap());

  }
}
