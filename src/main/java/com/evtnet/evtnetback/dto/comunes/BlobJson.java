package com.evtnet.evtnetback.dto.comunes;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BlobJson {
    private String content;     // "cadena binaria" (no base64)
    private String contentType; // p.ej. "image/png"
}