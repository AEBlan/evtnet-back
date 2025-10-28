package com.evtnet.evtnetback.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/hello")
    public String hello() {
        return "¡Hola! La aplicación EVTNET está funcionando correctamente.";
    }

    @GetMapping("/status")
    public String status() {
        return "OK - Aplicación activa";
    }
}
