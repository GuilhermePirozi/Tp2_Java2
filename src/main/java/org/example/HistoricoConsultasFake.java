package org.example;

import java.util.ArrayList;
import java.util.List;

public class HistoricoConsultasFake implements HistoricoConsultas {
    private List<Consulta> consultas = new ArrayList<>();

    public void adicionarConsulta(Consulta consulta) {
        consultas.add(consulta);
    }

    public List<Consulta> listarConsultas() {
        return consultas;
    }
}
