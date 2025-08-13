package app.bpartners.geojobs.service.event;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import app.bpartners.geojobs.conf.FacadeIT;
import app.bpartners.geojobs.endpoint.event.model.RoadContinuationRequested;
import app.bpartners.geojobs.file.bucket.BucketComponent;
import app.bpartners.geojobs.file.hash.FileHash;
import app.bpartners.geojobs.repository.GeoJsonRoadContinuationRepository;
import app.bpartners.geojobs.service.RoadContinuerService;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

class RoadContinuationServiceIT extends FacadeIT {

  private static final String EXPECTED_PRESIGNED_URL =
      "https://mock-presigned-url/continued-abohimanjaka.geojson";
  private final RoadContinuerService continuer = mock(RoadContinuerService.class);
  @Autowired private RoadContinuationService roadContinuationService;
  @Autowired private GeoJsonRoadContinuationRepository roadContinuationRepository;

  @Test
  void testAcceptWithEmptyResult() throws URISyntaxException, IOException {
    var resource = getClass().getResource("/geojson/ambohimanjaka.geojson");
    assertNotNull(resource);
    var geoJSON = new File(resource.toURI());
    int zoom = 20;
    int imageSize = 1_080;

    var event = new RoadContinuationRequested(geoJSON, zoom, imageSize);
    roadContinuationService.accept(event);
    assertFalse(roadContinuationRepository.findAll().isEmpty());
  }
  @Test
  void should_process_road_continuation_with_anosy_rond_point2() throws URISyntaxException, IOException {
    var resource = getClass().getResource("/geojson/anosy-rond-point-2.geojson");
    assertNotNull(resource);
    var geoJSON = new File(resource.toURI());
    int zoom = 20;
    int imageSize = 1_080;

    var event = new RoadContinuationRequested(geoJSON, zoom, imageSize);
    roadContinuationService.accept(event);
    assertFalse(roadContinuationRepository.findAll().isEmpty());
  }

  @Test
  void should_process_road_continuation_with_ambohijatovo() throws URISyntaxException, IOException {
    var resource = getClass().getResource("/geojson/ambohijatovo-crossed.geojson");
    assertNotNull(resource);
    var geoJSON = new File(resource.toURI());
    int zoom = 20;
    int imageSize = 1_080;

    var event = new RoadContinuationRequested(geoJSON, zoom, imageSize);
    roadContinuationService.accept(event);
    assertFalse(roadContinuationRepository.findAll().isEmpty());
  }


  @TestConfiguration
  static class MockConfig {
    @Bean
    public BucketComponent bucketComponent() {
      BucketComponent mock = mock(BucketComponent.class);
      when(mock.upload(any(File.class), anyString())).thenReturn(mock(FileHash.class));
      when(mock.presign(anyString())).thenReturn(EXPECTED_PRESIGNED_URL);
      return mock;
    }
  }
}
