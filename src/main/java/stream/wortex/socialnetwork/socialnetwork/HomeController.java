package stream.wortex.socialnetwork.socialnetwork;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {


    @GetMapping
    public String greeting(@RequestParam(required = false, defaultValue = "Penis") String name) {
        return " hey there "+name+"!";
    }
}
