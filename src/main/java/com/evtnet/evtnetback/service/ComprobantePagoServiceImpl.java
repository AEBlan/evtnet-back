package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.dto.comprobante.DTOComprobante;
import com.evtnet.evtnetback.dto.comprobante.DTOComprobanteSimple;
import com.evtnet.evtnetback.entity.ComprobantePago;
import com.evtnet.evtnetback.entity.ItemComprobantePago;
import com.evtnet.evtnetback.repository.BaseRepository;
import com.evtnet.evtnetback.repository.ComprobantePagoRepository;
import com.evtnet.evtnetback.util.CurrentUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneId;
import java.util.List;

@Service
public class ComprobantePagoServiceImpl
        extends BaseServiceImpl<ComprobantePago, Long>
        implements ComprobantePagoService {

    @Value("${app.storage.comprobantes:/app/storage/comprobantes}")
    private String directorio;

    private final ComprobantePagoRepository comprobantePagoRepository;

    public ComprobantePagoServiceImpl(
            BaseRepository<ComprobantePago, Long> baseRepository,
            ComprobantePagoRepository comprobantePagoRepository
    ) {
        super(baseRepository);
        this.comprobantePagoRepository = comprobantePagoRepository;
    }

    // --------------------------------------------------------------
    // OBTENER COMPROBANTE COMPLETO
    // --------------------------------------------------------------
    @Override
    public DTOComprobante obtener(Long numero) throws Exception {

        ComprobantePago c = comprobantePagoRepository.findByNumero(String.valueOf(numero))
                .orElseThrow(() -> new Exception("No se encontró el comprobante"));

        // total bruto = sum(items)
        double totalBruto = c.getItems().stream()
                .mapToDouble(i -> i.getMontoUnitario().doubleValue() * i.getCantidad())
                .sum();

        // map "pago" and "cobro"
        // (assuming all items have same pago/cobro user — typical comprobante structure)
        ItemComprobantePago firstItem = c.getItems().isEmpty() ? null : c.getItems().get(0);

        DTOComprobante.Persona pago = null;
        DTOComprobante.Persona cobro = null;

        if (firstItem != null) {
            if (firstItem.getPago() != null) {
                pago = DTOComprobante.Persona.builder()
                        .nombre(firstItem.getPago().getNombre())
                        .apellido(firstItem.getPago().getApellido())
                        .dni(firstItem.getPago().getDni())
                        .build();
            }
            if (firstItem.getCobro() != null) {
                cobro = DTOComprobante.Persona.builder()
                        .nombre(firstItem.getCobro().getNombre())
                        .apellido(firstItem.getCobro().getApellido())
                        .dni(firstItem.getCobro().getDni())
                        .build();
            }
        }

        ItemComprobantePago comision = c.getItems().stream().filter(i -> i.getDetalle().contains("Comisión")).findFirst().orElse(ItemComprobantePago.builder().montoUnitario(BigDecimal.ZERO).build());

        return DTOComprobante.builder()
                .numero(numero)
                .concepto(c.getConcepto())
                .fechaHoraEmision(c.getFechaHoraEmision().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                .formaDePago("Mercado Pago") // or c.getFormaDePago() if exists
                .pago(pago)
                .cobro(cobro)
                .montoTotalBruto(totalBruto)
                .comision(comision.getMontoUnitario().multiply(BigDecimal.valueOf(comision.getCantidad())).doubleValue())
                .evtnetPagaComision(comision.getPago() == null)
                .build();
    }

    // --------------------------------------------------------------
    // OBTENER ARCHIVO (PDF)
    // --------------------------------------------------------------
    @Override
    public byte[] obtenerArchivo(Long numero) throws Exception {

        ComprobantePago c = comprobantePagoRepository.findByNumero(String.valueOf(numero))
                .orElseThrow(() -> new Exception("No se encontró el comprobante"));

        if (c.getArchivo() == null)
            throw new Exception("El comprobante no tiene archivo PDF asociado");

        Path ruta = Path.of(directorio, c.getArchivo());

        return Files.readAllBytes(ruta);
    }

    // --------------------------------------------------------------
    // LISTAR MIS COMPROBANTES (según username)
    // --------------------------------------------------------------
    @Override
    public List<DTOComprobanteSimple> obtenerMisComprobantes() throws Exception {

        String username = CurrentUser.getUsername()
                .orElseThrow(() -> new Exception("Inicie sesión para ver sus comprobantes"));

        return comprobantePagoRepository.findAllByUsuarioInvolucrado(username).stream()
                .map(c -> DTOComprobanteSimple.builder()
                        .numero(Long.valueOf(c.getNumero()))
                        .concepto(c.getConcepto())
                        .fechaHoraEmision(c.getFechaHoraEmision()
                                .atZone(java.time.ZoneId.systemDefault())
                                .toInstant()
                                .toEpochMilli())
                        .monto(
                                c.getItems().stream()
                                        .mapToDouble(i -> i.getMontoUnitario().doubleValue() * i.getCantidad())
                                        .sum()
                        )
                        .build())
                .toList();
    }
}
