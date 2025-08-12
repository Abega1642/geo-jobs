package app.bpartners.geojobs.service.event;

import app.bpartners.geojobs.conf.FacadeIT;
import app.bpartners.geojobs.endpoint.event.model.RoadContinuationRequested;
import app.bpartners.geojobs.repository.GeoJsonRoadContinuationRepository;
import app.bpartners.geojobs.service.RoadContinuerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.io.File;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class RoadContinuationServiceIT extends FacadeIT {

    @Autowired
    private RoadContinuationService roadContinuationService;

    @Autowired
    private GeoJsonRoadContinuationRepository roadContinuationRepository;

    @TestConfiguration
    static class RoadContinuationServiceTest2Configuration {
        @Bean
        public RoadContinuerService roadContinuerService() {
            return mock(RoadContinuerService.class);
        }
    }

    @Test
    void testAcceptWithEmptyResult() throws URISyntaxException {
        var resource = getClass().getResource("/geojson/ambohimanjaka.geojson");
        assertNotNull(resource);
        var geoJSON = new File(resource.toURI());
        var event = new RoadContinuationRequested(geoJSON, 20, 1080);
        roadContinuationService.accept(event);
        assertFalse(roadContinuationRepository.findAll().isEmpty());
    }
}
