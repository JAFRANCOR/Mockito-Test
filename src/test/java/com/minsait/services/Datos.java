package com.minsait.services;

import com.minsait.models.Examen;

import java.util.List;

public class Datos {
    public static final List<Examen> EXAMENES = List.of(
            new Examen(1L, "Matematicas"),
            new Examen(2L, "Fisica"),
            new Examen(3L, "Quimica")
    );
    public static final Examen EXAMEN = new Examen(1L, "Matematicas");
    public static final List<String> PREGUNTAS = List.of(
            "Aritmetica",
            "Integrales",
            "Derivadas",
            "Trigonometria",
            "Geometria"
    );
}
