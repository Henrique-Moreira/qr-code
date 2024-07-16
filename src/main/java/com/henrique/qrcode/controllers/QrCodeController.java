package com.henrique.qrcode.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.zxing.NotFoundException;
import com.google.zxing.WriterException;
import com.henrique.qrcode.services.QrCodeService;

@RestController
@RequestMapping("/qr-code")
public class QrCodeController {

    @Autowired
    private QrCodeService qrCodeService;

    @PostMapping("/generate")
    public ResponseEntity<byte[]> generateQrCode(@RequestBody String url) {
        try {
            byte[] pngData = qrCodeService.generateQrCode(url);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, "image/png");

            return new ResponseEntity<>(pngData, headers, HttpStatus.OK);
        } catch (WriterException | IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<String> validateQrCode(@RequestParam("file") MultipartFile file, @RequestParam("url") String url) {
        try {
            boolean isValid = qrCodeService.validateQrCode(file, url);

            if (isValid) {
                return new ResponseEntity<>("QR code matches the URL", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("QR code does not match the URL", HttpStatus.BAD_REQUEST);
            }
        } catch (IOException | NotFoundException e) {
            return new ResponseEntity<>("Error processing QR code", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}