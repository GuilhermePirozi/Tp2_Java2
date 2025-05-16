import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.example.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CalculadoraReembolsoTest {
    private HistoricoConsultasFake historico;
    private AuditoriaSpy auditoria;
    private AutorizadorReembolso autorizador;
    private CalculadoraReembolso calculadora;

    @BeforeEach
    void setup() {
        historico = new HistoricoConsultasFake();
        auditoria = new AuditoriaSpy();
        autorizador = mock(AutorizadorReembolso.class);
        calculadora = new CalculadoraReembolso(historico, auditoria, autorizador);
    }

    @Test
    void calculaReembolsoBasico() {
        when(autorizador.estaAutorizado(any(), any())).thenReturn(true);
        Paciente paciente = new Paciente();
        Consulta consulta = helperConsulta(200.0);
        PlanoSaude plano = () -> 0.7;
        double reembolso = calculadora.calcular(paciente, consulta, plano);
        assertComMargem(140.0, reembolso);
    }

    @Test
    void calculaComCoberturaZeroECem() {
        when(autorizador.estaAutorizado(any(), any())).thenReturn(true);
        Paciente paciente = new Paciente();
        Consulta consultaZero = helperConsulta(0.0);
        Consulta consultaBase = helperConsulta(100.0);
        PlanoSaude planoZero = () -> 0.0;
        PlanoSaude planoCem = () -> 1.0;
        assertComMargem(0.0, calculadora.calcular(paciente, consultaBase, planoZero));
        assertComMargem(100.0, calculadora.calcular(paciente, consultaBase, planoCem));
        assertComMargem(0.0, calculadora.calcular(paciente, consultaZero, planoCem));
    }

    @Test
    void auditoriaEhAcionada() {
        when(autorizador.estaAutorizado(any(), any())).thenReturn(true);
        Consulta consulta = helperConsulta(100.0);
        PlanoSaude plano = () -> 0.5;
        calculadora.calcular(new Paciente(), consulta, plano);
        assertTrue(auditoria.foiChamado());
    }

    @Test
    void lancaExcecaoQuandoNaoAutorizado() {
        when(autorizador.estaAutorizado(any(), any())).thenReturn(false);
        Consulta consulta = helperConsulta(100.0);
        PlanoSaude plano = () -> 0.5;
        assertThrows(RuntimeException.class, () ->
                calculadora.calcular(new Paciente(), consulta, plano));
    }

    @Test
    void aplicaTetoDe150() {
        when(autorizador.estaAutorizado(any(), any())).thenReturn(true);
        Consulta consulta = helperConsulta(1000.0);
        PlanoSaude plano = () -> 0.8;
        double valor = calculadora.calcular(new Paciente(), consulta, plano);
        assertComMargem(150.0, valor);
    }

    @Test
    void integraTodosOsDubles() {
        AutorizadorReembolso autorizadorMock = mock(AutorizadorReembolso.class);
        when(autorizadorMock.estaAutorizado(any(), any())).thenReturn(true);
        HistoricoConsultasFake historicoFake = new HistoricoConsultasFake();
        AuditoriaSpy auditoriaSpy = new AuditoriaSpy();
        CalculadoraReembolso calcCompleta = new CalculadoraReembolso(historicoFake, auditoriaSpy, autorizadorMock);
        Consulta consulta = helperConsulta(500.0);
        PlanoSaude plano = () -> 0.5;
        double valor = calcCompleta.calcular(new Paciente(), consulta, plano);
        assertComMargem(150.0, valor);
        assertTrue(auditoriaSpy.foiChamado());
        assertEquals(1, historicoFake.listarConsultas().size());
    }

    private Consulta helperConsulta(double valor) {
        return new Consulta(valor);
    }

    private void assertComMargem(double esperado, double real) {
        assertTrue(Math.abs(esperado - real) < 0.01,
                "Valor esperado: " + esperado + ", valor obtido: " + real);
    }
}
