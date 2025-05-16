package org.example;

public class CalculadoraReembolso {
    private HistoricoConsultas historico;
    private Auditoria auditoria;
    private AutorizadorReembolso autorizador;

    public CalculadoraReembolso(HistoricoConsultas historico, Auditoria auditoria, AutorizadorReembolso autorizador) {
        this.historico = historico;
        this.auditoria = auditoria;
        this.autorizador = autorizador;
    }

    public double calcular(Paciente paciente, Consulta consulta, PlanoSaude plano) {
        if (!autorizador.estaAutorizado(paciente, consulta)) {
            throw new RuntimeException("Consulta não autorizada");
        }

        double reembolso = consulta.getValor() * plano.getCobertura();
        reembolso = Math.min(reembolso, 150.0);

        historico.adicionarConsulta(consulta);
        auditoria.registrarConsulta(consulta);

        return reembolso;
    }
}