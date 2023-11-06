package com.minsait.services;

import com.minsait.models.Examen;
import com.minsait.repositories.ExamenRepository;
import com.minsait.repositories.PreguntaRepository;

import java.util.Optional;

public class ExamenServiceImpl implements ExamenService{
    private ExamenRepository examenRepository;
    private PreguntaRepository preguntaRepository;
    @Override
    public Optional<Examen> findExamenPorNombre(String nombre) {
        return examenRepository.findAll()
                .stream()
                .filter(examen -> examen.getNombre().equals(nombre))
                .findFirst();
    }

    @Override
    public Examen findExamenPorNombreConPreguntas(String nombre) {
        Optional<Examen> examenOptional = findExamenPorNombre(nombre);
        Examen examen = null;
        if(examenOptional.isPresent()){
            examen = examenOptional.orElseThrow();
            examen.setPreguntas(preguntaRepository.findPreguntasByExamenId(examen.getId()));
        }
        return examen;
    }

    @Override
    public Examen save(Examen examen) {
        if (!examen.getPreguntas().isEmpty()) {
            preguntaRepository.savePreguntas(examen.getPreguntas());
        }
        return examenRepository.save(examen);
    }
}
