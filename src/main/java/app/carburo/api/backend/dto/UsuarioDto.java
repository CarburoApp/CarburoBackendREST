package app.carburo.api.backend.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
public class UsuarioDto {

    private UUID uuid;
    private String contrasena;
    private String confirmacionContrasena;
    private OffsetDateTime fechaRegistro;
    private OffsetDateTime fechaActualizacion;
    private short provinciaFavorita = 0;

    private String token;

    private List<Integer> eessFavoritas;

    public UsuarioDto(UUID uuid,
                      OffsetDateTime fechaRegistro,
                      OffsetDateTime fechaActualizacion,
                      short provinciaFavorita,
                      String accesoToken) {
        setUuid(uuid);
        setFechaRegistro(fechaRegistro);
        setFechaActualizacion(fechaActualizacion);
        setProvinciaFavorita(provinciaFavorita);
        setToken(accesoToken);
    }

    public UsuarioDto() {
    }
}
