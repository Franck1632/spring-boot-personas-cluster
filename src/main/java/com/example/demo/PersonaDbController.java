package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/db/personas")
public class PersonaDbController {

    @Autowired
    private PersonaRepository personaRepository;

    @Value("${app.estudiante.nombre:Franckarlos Barbosa}")
    private String nombreEstudiante;

    @GetMapping("/autor")
    public String obtenerAutor() {
        return "Aplicacion compilada y desplegada por: [" + nombreEstudiante + "]";
    }

    // 1. OBTENER LISTA PAGINADA ORGANIZADA Y CON TU NOMBRE
    @GetMapping
    public Map<String, Object> listarPersonas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<PersonaEntity> personasPage = personaRepository.findAll(PageRequest.of(page, size));

        Map<String, Object> respuesta = new HashMap<>();
        // Cabecera con tus datos
        respuesta.put("estudiante", nombreEstudiante);
        respuesta.put("materia", "Sistemas Distribuidos");
        
        // Paginación resumida y organizada
        respuesta.put("paginaActual", personasPage.getNumber());
        respuesta.put("totalPaginas", personasPage.getTotalPages());
        respuesta.put("totalRegistros", personasPage.getTotalElements());

        // Solamente la lista de datos (sin objetos 'pageable' o 'sort' gigantes)
        respuesta.put("datos", personasPage.getContent());

        return respuesta;
    }

    @PostMapping
    public PersonaEntity crearPersona(@RequestBody PersonaEntity persona) {
        return personaRepository.save(persona);
    }

    @PutMapping("/{id}")
    public PersonaEntity actualizarPersona(@PathVariable Long id, @RequestBody PersonaEntity personaDetalles) {
        Optional<PersonaEntity> persona = personaRepository.findById(id);
        if (persona.isPresent()) {
            PersonaEntity p = persona.get();
            p.setNombre(personaDetalles.getNombre());
            p.setCargo(personaDetalles.getCargo());
            p.setEstado(personaDetalles.getEstado());
            return personaRepository.save(p);
        }
        return null;
    }

    @DeleteMapping("/{id}")
    public String eliminarPersona(@PathVariable Long id) {
        personaRepository.deleteById(id);
        return "Registro eliminado con ID: " + id;
    }
}