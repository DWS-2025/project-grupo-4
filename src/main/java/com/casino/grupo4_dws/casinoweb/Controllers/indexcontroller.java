package com.casino.grupo4_dws.casinoweb.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class indexcontroller {

    // Este método maneja las solicitudes a la URL '/inicio'
    @GetMapping("/")
    public String mostrarInicio() {return "inicio";}

    @GetMapping("/NJuegos")
    public String mostrarJuegos() {return "NJuegos";}

    @GetMapping("/login")
    public String mostrarLogin() {return "login";}

    @GetMapping("/rule")
    public String mostrarRule() {return "rule";}

    @GetMapping("/slots")
    public String mostrarSlots() {return "slots";}

    @GetMapping("/crash")
    public String mostrarCrash() {return "crash";}
}




