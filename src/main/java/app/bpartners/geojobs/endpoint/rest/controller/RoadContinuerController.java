package app.bpartners.geojobs.endpoint.rest.controller;

import app.bpartners.geojobs.endpoint.rest.postprocessing.model.TilingConf;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
public class RoadContinuerController {

    private RoadContinuerContinuerService roadContinuerContinuerService;

    @PostMapping("/roadcontinuer")
    public String  roadContinuer(@RequestBody String geojson, @RequestParam int zoom, @RequestParam int imageSize){
        var tilingConf = new TilingConf(zoom, imageSize);
        return roadContinuerContinuerService.continueRoute(geojson, tilingConf);
    }
}
