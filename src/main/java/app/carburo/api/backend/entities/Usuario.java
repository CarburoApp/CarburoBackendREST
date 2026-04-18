package app.carburo.api.backend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "usuario",
        uniqueConstraints = {
                @UniqueConstraint(name = "usuario_email_key", columnNames = "email"),
                @UniqueConstraint(name = "usuario_uuid_unique", columnNames = "uuid")
        })
@Getter
@Setter
@NoArgsConstructor
public class Usuario {

    // PK
    @Id
    @Column(nullable = false, updatable = false)
    private UUID uuid;

    @Column(name = "fecha_registro", nullable = false)
    private OffsetDateTime fechaRegistro;

    @Column(name = "fecha_actualizacion", nullable = false)
    private OffsetDateTime fechaActualizacion;

    // FK a provincia, unidireccional
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "provincia_favorita",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "usuario_provincia_favorita_fkey"),
            nullable = false
    )
    private Provincia provinciaFavorita;

    @ManyToMany
    @JoinTable(
            name = "combustible_favorito",
            joinColumns = @JoinColumn(name = "uuid_usuario"),
            inverseJoinColumns = @JoinColumn(name = "id_combustible")
    )
    private Set<Combustible> combustiblesFavoritos = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "eess_favoritas",
            joinColumns = @JoinColumn(
                    name = "uuid_usuario",
                    referencedColumnName = "uuid"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "id_eess",
                    referencedColumnName = "id"
            )
    )
    private Set<EstacionDeServicio> eessFavoritas = new HashSet<>();


    public Usuario(UUID uuid,
                   OffsetDateTime fechaRegistro,
                   OffsetDateTime fechaActualizacion,
                   Provincia provinciaFavorita) {
        setUuid(uuid);
        setFechaRegistro(fechaRegistro);
        setFechaActualizacion(fechaActualizacion);
        setProvinciaFavorita(provinciaFavorita);
    }

    public Usuario(UUID uuid,
                   Provincia provinciaFavorita) {
        setUuid(uuid);
        setFechaRegistro(OffsetDateTime.now());
        setFechaActualizacion(OffsetDateTime.now());
        setProvinciaFavorita(provinciaFavorita);
    }

}
