package com.henrique.qrcode.services;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import com.google.zxing.NotFoundException;
import com.google.zxing.WriterException;

public class QrCodeServiceTest {

    @InjectMocks
    private QrCodeService qrCodeService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGenerateQrCode() throws WriterException, IOException {
        String url = "https://example.com";
        byte[] qrCode = qrCodeService.generateQrCode(url);

        // Verifica se o QR Code gerado não está vazio
        assertTrue(qrCode.length > 0);

        // Verifica se a imagem gerada pode ser lida
        ByteArrayInputStream inputStream = new ByteArrayInputStream(qrCode);
        assertTrue(ImageIO.read(inputStream) != null);
    }

    @Test
    public void testValidateQrCode() throws IOException, NotFoundException, WriterException {
        String url = "https://example.com";
        byte[] qrCode = qrCodeService.generateQrCode(url);

        MultipartFile file = mock(MultipartFile.class);
        when(file.getBytes()).thenReturn(qrCode);

        boolean isValid = qrCodeService.validateQrCode(file, url);

        // Verifica se o QR Code é validado corretamente
        assertTrue(isValid);
    }
}