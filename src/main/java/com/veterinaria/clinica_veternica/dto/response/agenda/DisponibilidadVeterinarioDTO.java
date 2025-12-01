package com.veterinaria.clinica_veternica.dto.response.agenda;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * DTO de Response para la disponibilidad de un veterinario en una fecha específica.
 * 
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisponibilidadVeterinarioDTO {

    /**
     * Identificador único del veterinario.
     */
    private Long idVeterinario;

    /**
     * Nombre completo del veterinario.
     */
    private String nombreVeterinario;

    /**
     * Fecha de la disponibilidad.
     */
    private LocalDate fecha;

    /**
     * Nombre del día de la semana en español.
     */
    private String diaSemana;

    /**
     * Indica si el veterinario tiene horarios configurados para este día.
     */
    private Boolean tieneHorarios;

    /**
     * Lista de horarios configurados para este día.
     */
    private List<HorarioDisponibleDTO> horarios;

    /**
     * Lista de slots de tiempo disponibles.
     */
    private List<SlotDisponibleDTO> slotsDisponibles;

    /**
     * Lista de citas ocupadas en este día.
     */
    private List<CitaOcupadaDTO> citasOcupadas;

    /**
     * DTO para representar un horario disponible.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class HorarioDisponibleDTO {
        private Long idHorario;
        private LocalTime horaInicio;
        private LocalTime horaFin;
        private Integer duracionCitaMinutos;
        private Boolean activo;
    }

    /**
     * DTO para representar un slot de tiempo disponible.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SlotDisponibleDTO {
        private LocalTime hora;
        private Boolean disponible;
        private String motivoNoDisponible;
    }

    /**
     * DTO para representar una cita ocupada.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CitaOcupadaDTO {
        private Long idCita;
        private LocalTime hora;
        private String estado;
        private String nombreMascota;
        private String nombreServicio;
    }
}

