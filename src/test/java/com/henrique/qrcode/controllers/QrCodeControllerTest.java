package com.henrique.qrcode.controllers;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.google.zxing.NotFoundException;
import com.google.zxing.WriterException;
import com.henrique.qrcode.services.QrCodeService;

public class QrCodeControllerTest {

    @Mock
    private QrCodeService qrCodeService;

    @InjectMocks
    private QrCodeController qrCodeController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGenerateQrCode() throws WriterException, IOException {
        String url = "https://example.com";
        byte[] pngData = new byte[10];

        when(qrCodeService.generateQrCode(url)).thenReturn(pngData);

        ResponseEntity<byte[]> response = qrCodeController.generateQrCode(url);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);

        verify(qrCodeService).generateQrCode(url);
        assert response.getStatusCode() == HttpStatus.OK;
        assert response.getBody() == pngData;
        assert response.getHeaders().equals(headers);
    }

    @Test
    public void testGenerateQrCodeError() throws WriterException, IOException {
        String url = "https://example.com";

        doThrow(new IOException()).when(qrCodeService).generateQrCode(url);

        ResponseEntity<byte[]> response = qrCodeController.generateQrCode(url);

        verify(qrCodeService).generateQrCode(url);
        assert response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR;
    }

    @Test
    public void testValidateQrCode() throws IOException, NotFoundException {
        String url = "https://example.com";
        MultipartFile file = new MockMultipartFile("file", new byte[10]);

        when(qrCodeService.validateQrCode(file, url)).thenReturn(true);

        ResponseEntity<String> response = qrCodeController.validateQrCode(file, url);

        verify(qrCodeService).validateQrCode(file, url);
        assert response.getStatusCode() == HttpStatus.OK;
        assert response.getBody().equals("QR code matches the URL");
    }

    @Test
    public void testValidateQrCodeMismatch() throws IOException, NotFoundException {
        String url = "https://example.com";
        MultipartFile file = new MockMultipartFile("file", new byte[10]);

        when(qrCodeService.validateQrCode(file, url)).thenReturn(false);

        ResponseEntity<String> response = qrCodeController.validateQrCode(file, url);

        verify(qrCodeService).validateQrCode(file, url);
        assert response.getStatusCode() == HttpStatus.BAD_REQUEST;
        assert response.getBody().equals("QR code does not match the URL");
    }

    @Test
    public void testValidateQrCodeError() throws IOException, NotFoundException {
        String url = "https://example.com";
        MultipartFile file = new MockMultipartFile("file", new byte[10]);

        doThrow(new IOException()).when(qrCodeService).validateQrCode(file, url);

        ResponseEntity<String> response = qrCodeController.validateQrCode(file, url);

        verify(qrCodeService).validateQrCode(file, url);
        assert response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR;
        assert response.getBody().equals("Error processing QR code");
    }
}