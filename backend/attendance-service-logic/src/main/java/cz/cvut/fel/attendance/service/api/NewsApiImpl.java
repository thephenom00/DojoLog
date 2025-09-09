package cz.cvut.fel.attendance.service.api;

import cz.cvut.fel.attendance.service.service.NewsService;
import cz.fel.cvut.attendance.service.api.NewsApi;
import cz.fel.cvut.attendance.service.model.NewsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class NewsApiImpl implements NewsApi {

    private final NewsService newsService;

    @Override
    public ResponseEntity<NewsDto> createNews(NewsDto newsDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(newsService.createNews(newsDto));
    }

    @Override
    public ResponseEntity<List<NewsDto>> getNews() {
        return ResponseEntity.ok(newsService.getNews());
    }

    @Override
    public ResponseEntity<NewsDto> updateNews(Long id, NewsDto newsDto) throws Exception {
        return ResponseEntity.ok(newsService.updateNews(id,newsDto));
    }

    @Override
    public ResponseEntity<Void> deleteNews(Long id) throws Exception {
        newsService.deleteNews(id);
        return ResponseEntity.ok().build();
    }
}
