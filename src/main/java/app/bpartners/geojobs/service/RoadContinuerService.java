package app.bpartners.geojobs.service;

import static java.lang.Math.PI;

import app.bpartners.geojobs.endpoint.rest.mapper.FileFromMultipartFileMapper;
import app.bpartners.geojobs.endpoint.rest.postprocessing.Geojson;
import app.bpartners.geojobs.endpoint.rest.postprocessing.continuer.LatLonLinesContinuer;
import app.bpartners.geojobs.endpoint.rest.postprocessing.model.TilingConf;
import app.bpartners.geojobs.file.bucket.BucketComponent;
import app.bpartners.geojobs.model.geometry.quadrilateral.model.AlphaConf;
import app.bpartners.geojobs.model.geometry.route.ContinuationConf;
import app.bpartners.geojobs.model.geometry.route.PrettyConf;
import app.bpartners.geojobs.model.geometry.route.RoutesContinuationConf;
import app.bpartners.geojobs.model.geometry.route.UnionConf;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@AllArgsConstructor
@Slf4j
public class RoadContinuerService {
  private static final AlphaConf DEFAULT_ALPHA_CONF = new AlphaConf(0.5d, 1);
  private static final UnionConf DEFAULT_UNION_CONF = new UnionConf(1);
  private static final PrettyConf DEFAULT_PRETTY_CONF = new PrettyConf(0);
  private static final int DEFAULT_NEIGHBOUR_THRESHOLD = 10;
  private static final ContinuationConf DEFAULT_CONTINUATION_CONF =
      new ContinuationConf(PI / 12, PI / 6, 500);

  private final BucketComponent bucketComponent;
  private final FileFromMultipartFileMapper fileFromMultipartFileMapper;

  private static File getGeoJsonFromString(String geoJsonString) throws IOException {
    String uuidName = UUID.randomUUID().toString();
    File tempFile = File.createTempFile("continued-geojson-" + uuidName, ".geojson");
    Files.writeString(tempFile.toPath(), geoJsonString);
    return tempFile;
  }

  private static LatLonLinesContinuer getLatLonContinuer(
      RoutesContinuationConf routesContinuationConf, TilingConf tilingConf) {
    return new LatLonLinesContinuer(
        routesContinuationConf, tilingConf, DEFAULT_NEIGHBOUR_THRESHOLD);
  }

  private static RoutesContinuationConf getRouteContinuationConf() {
    return new RoutesContinuationConf(
        DEFAULT_ALPHA_CONF, DEFAULT_UNION_CONF, DEFAULT_CONTINUATION_CONF, DEFAULT_PRETTY_CONF);
  }

  public Map<String, String> continueRoute(MultipartFile geoJSON, Integer zoom, Integer imgSize)
      throws IOException {
    var geoJSONFile = fileFromMultipartFileMapper.apply(geoJSON);

    var tilingConf = getTilingConf(zoom, imgSize);
    log.info(
        "Continuing route polygons of geojson={} with zoom={} and imgSize={}",
        geoJSONFile.getName(),
        zoom,
        imgSize);

    var continuer = getLatLonContinuer(getRouteContinuationConf(), tilingConf);
    var continuedPolygons = continuer.apply(geoJSONFile);

    File continuedGeoJsonFile = getGeoJsonFromString(new Geojson(continuedPolygons).stringValue());
    log.info("Continuation process finished");

    return getPresignedURL(continuedGeoJsonFile);
  }

  private Map<String, String> getPresignedURL(File continuedGeoJsonFile) {
    var bucketKey = "continuedRoads/" + UUID.randomUUID() + ".geojson";
    bucketComponent.upload(continuedGeoJsonFile, bucketKey);
    String presignURL = bucketComponent.presign(bucketKey);
    log.info("Generated presigned URL: {}", presignURL);
    return Map.of("url", presignURL);
  }

  public TilingConf getTilingConf(Integer zoom, Integer imgSize) {
    var defaultConf = TilingConf.getDefaultInstance();
    int fZoom = (zoom == null) ? defaultConf.z() : zoom;
    int fImgSize = (imgSize == null) ? defaultConf.imgSize() : imgSize;
    return new TilingConf(fZoom, fImgSize);
  }
}
