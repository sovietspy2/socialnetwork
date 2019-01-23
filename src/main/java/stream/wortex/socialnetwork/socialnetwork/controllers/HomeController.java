package stream.wortex.socialnetwork.socialnetwork.controllers;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import stream.wortex.socialnetwork.socialnetwork.services.ImageService;

import java.io.IOException;

@RestController
public class HomeController {

    private static final String BASE_PATH = "/images";
    private static final String FILENAME = "{filename:.+}";
    private final ImageService imageService;

    public HomeController(ImageService imageService) {
        this.imageService = imageService;
    }


    @GetMapping("/test")
    public String greeting(@RequestParam(required = false, defaultValue = "Penis") String name) {
        return " hey there "+name+"!";
    }



    @GetMapping(value = BASE_PATH + "/" + FILENAME + "/RAW", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public Mono<ResponseEntity<?>> oneRawImage(@PathVariable String fileName) {
        return imageService.findOneImage(fileName)
                .map(resource -> {
                    try {
                        return ResponseEntity.ok()
                                .contentLength(resource.contentLength())
                                .body(new InputStreamResource(resource.getInputStream()));
                    } catch (IOException e) {
                        return ResponseEntity.badRequest().body("Could not find it");
                    }
                });
    }

    @PostMapping(value = BASE_PATH)
    public Mono<String> createFile(@RequestPart(name="file") Flux<FilePart> files) {
        return imageService.createImage(files).then(Mono.just("redirect:/"));
    }

    @DeleteMapping(BASE_PATH+"/"+FILENAME)
    public Mono<String> deleteFile(@PathVariable String fileName) {
        return imageService.deleteImage((fileName)).then(Mono.just("redirect:/"));
    }


}
