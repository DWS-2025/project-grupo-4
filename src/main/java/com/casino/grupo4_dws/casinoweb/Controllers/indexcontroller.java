package com.casino.grupo4_dws.casinoweb.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class indexcontroller {

    // Este método maneja las solicitudes a la URL '/inicio'
    @GetMapping("/inicio")
    public String mostrarInicio() {
        // Devuelve el nombre del archivo HTML que se debe mostrar
        return "inicio"; // Esto busca 'src/main/resources/templates/inicio.html'
    }
}

