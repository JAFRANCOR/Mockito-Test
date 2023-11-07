package com.minsait.services;

import com.minsait.models.Examen;
import com.minsait.repositories.ExamenRepository;
import com.minsait.repositories.PreguntaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ExamenServiceImplTest {
    @Mock
    ExamenRepository examenRepository;
    @Mock
    PreguntaRepository preguntaRepository;
    @InjectMocks
    ExamenServiceImpl service;

    @Captor
    ArgumentCaptor<Long> captor;

    /*@BeforeEach
    void setUp() {
        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
    }*/

    @Test
    void testArgumentCaptor(){
        //Given
        when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntaRepository.findPreguntasByExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        //When
        service.findExamenPorNombreConPreguntas("Matematicas");
        //Then
        verify(preguntaRepository).findPreguntasByExamenId(captor.capture());
        assertEquals(1L, captor.getValue());


    }

    @Test
    void testFindExamenPorNombre() {
        when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);

        String nombre = "Matematicas";
        Optional<Examen> examen = service.findExamenPorNombre(nombre);

        assertTrue(examen.isPresent());
        assertEquals(nombre, examen.get().getNombre());
    }

    @Test
    void testfindExamenPorNombreConPreguntas(){
        when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntaRepository.findPreguntasByExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);

        Examen examen = service.findExamenPorNombreConPreguntas("Fisica");

        assertTrue(examen.getPreguntas().contains("Aritmetica"));
        verify(examenRepository).findAll();
        verify(preguntaRepository).findPreguntasByExamenId(anyLong());
    }

    @Test
    void testException(){
        //Given
        when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntaRepository.findPreguntasByExamenId(anyLong())).thenThrow(IllegalArgumentException.class);
        String nombre = "Matematicas";

        //When - Then
        assertThrows(IllegalArgumentException.class, () -> {
            service.findExamenPorNombreConPreguntas(nombre);
        });
        assertEquals(IllegalArgumentException.class, assertThrows(IllegalArgumentException.class, () -> {
            service.findExamenPorNombreConPreguntas(nombre);
        }).getClass());
    }

    @Test
    void testDoThrow(){
        //Given
        Examen examen = Datos.EXAMEN;
        examen.setPreguntas(Datos.PREGUNTAS);
        doThrow(IllegalArgumentException.class).when(preguntaRepository).savePreguntas(anyList());

        //When - Then
        assertThrows(IllegalArgumentException.class, () -> {
            service.save(examen);
        });
    }

    @Test
    void testDoAnwer(){
        //Given
        when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);
        //when(preguntaRepository.findPreguntasByExamenId(1L)).thenReturn(Datos.PREGUNTAS);
        //when(preguntaRepository.findPreguntasByExamenId(5L)).thenReturn(Collections.emptyList());
        doAnswer(invocation -> {
            Long id = (Long) invocation.getArguments()[0];
            return id == 1L ? Datos.PREGUNTAS : Collections.emptyList();
        }).when(preguntaRepository).findPreguntasByExamenId(anyLong());

        //When
        Examen examen = service.findExamenPorNombreConPreguntas("Matematicas");

        //Then
        assertAll(
                () -> assertEquals(1L, examen.getId()),
                () -> assertFalse(examen.getPreguntas().isEmpty())
                //() -> assertTrue(examen.getPreguntas().isEmpty())
        );
    }

    @Test
    void testSaveExamWithQuestions(){
        //Given
        Examen newExamen = Datos.EXAMEN;
        newExamen.setPreguntas(Datos.PREGUNTAS);
        when(examenRepository.save(any(Examen.class))).thenReturn(Datos.EXAMEN);

        //When
        Examen examen = service.save(newExamen);

        //Then
        assertEquals(1L, examen.getId());
        assertEquals("Matematicas", examen.getNombre());
        assertEquals(5, examen.getPreguntas().size());
        verify(examenRepository).save(any(Examen.class));
        verify(preguntaRepository).savePreguntas(anyList());
    }

    @Test
    void testSaveExamWithoutQuestions(){
        //Given
        Examen newExamen = Datos.EXAMEN;
        when(examenRepository.save(any(Examen.class))).thenReturn(Datos.EXAMEN);

        //When
        Examen examen = service.save(newExamen);

        //Then
        assertEquals(1L, examen.getId());
        assertEquals("Matematicas", examen.getNombre());
        assertEquals(0, examen.getPreguntas().size());
    }

    @Test
    void testGetID(){
        Examen newExamen = new Examen(null, "Matematicas");
        when(examenRepository.save(any(Examen.class))).then(invocationOnMock -> {
            Examen examen = invocationOnMock.getArgument(0);
            examen.setId(5L);
            return examen;
        });

        Examen examen = service.save(newExamen);

        assertEquals(5L, examen.getId());
        assertEquals("Matematicas", examen.getNombre());
        assertEquals(0, examen.getPreguntas().size());
    }

    @Test
    void testSaveExamenInClass(){
        //Given
        Examen newExamen = new Examen(null, "Matematicas");
        //newExamen.setPreguntas(Datos.PREGUNTAS);
        when(examenRepository.save(any(Examen.class))).then(invocationOnMock -> {
            Examen examen = invocationOnMock.getArgument(0);
            examen.setId(5L);
            return examen;
        });

        //When
        Examen examen = service.save(newExamen);

        //Then
        assertEquals(5L, examen.getId());
        assertEquals("Matematicas", examen.getNombre());
        assertEquals(0, examen.getPreguntas().size());
        //assertEquals(3, examen.getPreguntas().size());
        verify(preguntaRepository, times(0)).savePreguntas(anyList());
    }
}