package org.example;

public class AuditoriaSpy implements Auditoria {
    private boolean chamada = false;

    public void registrarConsulta(Consulta consulta) {
        chamada = true;
    }

    public boolean foiChamado() {
        return chamada;
    }
}
