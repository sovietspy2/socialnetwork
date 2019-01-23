package stream.wortex.socialnetwork.socialnetwork.services;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import stream.wortex.socialnetwork.socialnetwork.models.Image;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class ImageService {
    private static final String UPLOAD_ROOT =  "upload-dir";
    private final ResourceLoader resourceLoader;

    public ImageService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    /**
     * Pre-load some test images
     *
     * @return Spring Boot {@link CommandLineRunner} automatically
     * run after app context is loaded.
     */
    @Bean
    CommandLineRunner setUp() throws IOException {
        return (args) -> {
            FileSystemUtils.deleteRecursively(new File(UPLOAD_ROOT));
            Files.createDirectory(Paths.get(UPLOAD_ROOT));
            FileCopyUtils.copy("Test file",
                    new FileWriter(UPLOAD_ROOT +
                            "/learning-spring-boot-cover.jpg"));
            FileCopyUtils.copy("Test file2",
                    new FileWriter(UPLOAD_ROOT +
                            "/learning-spring-boot-2nd-edition-cover.jpg"));
            FileCopyUtils.copy("Test file3",
                    new FileWriter(UPLOAD_ROOT + "/bazinga.png"));
        };
    }

    public Flux<Image> findAllImages() {
        try {
            return Flux.fromIterable(
                    Files.newDirectoryStream(Paths.get(UPLOAD_ROOT)))
                    .map(path-> {
                        return new Image("asd"+path.getFileName().toString(), path.getFileName().toString());
                    });
        } catch (IOException e) {
            return Flux.empty();
        }
    }

    public Mono<Resource> findOneImage(String fileName){
        return Mono.fromSupplier(()->{
            return resourceLoader.getResource("file:"+UPLOAD_ROOT+"/"+fileName);
        });
    }

    public Mono<Void> createImage(Flux<FilePart> files) {
        return files.flatMap(file->file.transferTo(
                Paths.get(UPLOAD_ROOT,file.filename()).toFile())).then();
    }

    public Mono<Void> deleteImage(String filename) {
        return Mono.fromRunnable( ()->{
            try {
                Files.deleteIfExists(Paths.get(UPLOAD_ROOT,filename));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
