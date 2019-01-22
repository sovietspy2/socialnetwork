package stream.wortex.socialnetwork.socialnetwork;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Repository
public interface ChapterRepository extends ReactiveCrudRepository<Chapter, String> {

}
