package app.bpartners.geojobs.repository.model.geojson;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;

import static org.hibernate.type.SqlTypes.NAMED_ENUM;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "road_continuation")
@Getter
@Setter
public class GeoJsonRoadContinuation {
  @Id
  @Column(name = "rc_id", nullable = false)
  private String id;

  @Column(name = "original_geojson_path", nullable = false)
  private String originalGeoJsonPath;

  @Column(name = "continued_geojson_path")
  private String continuedGeoJsonPath;

  @Column(name = "image_zoom", nullable = false)
  private Integer imageZoom;

  @Column(name = "image_size", nullable = false)
  private Integer imageSize;

  @Enumerated(EnumType.STRING)
<<<<<<< HEAD
  @JdbcTypeCode(NAMED_ENUM)
=======
  @Column(columnDefinition = "process_status", nullable = false)
>>>>>>> 6b3e4f2301c126f3f4ade93214bb86d37a0a4ebd
  private RoadContinuationProcessStatus status;
}
