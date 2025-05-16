package org.example;

public interface AutorizadorReembolso {
    boolean estaAutorizado(Paciente paciente, Consulta consulta);
}
