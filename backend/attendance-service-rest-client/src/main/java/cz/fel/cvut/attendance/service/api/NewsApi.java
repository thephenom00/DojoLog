package cz.fel.cvut.attendance.service.api;

import cz.fel.cvut.attendance.service.model.NewsDto;
import cz.fel.cvut.attendance.service.model.PagedNewsDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@RequestMapping("/news")
public interface NewsApi {

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value="/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<NewsDto> createNews(@RequestBody NewsDto newsDto);

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    ResponseEntity<List<NewsDto>> getNews();

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/paged")
    ResponseEntity<PagedNewsDto> getPagedNews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size
    );

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping(value="/{id}")
    ResponseEntity<NewsDto> updateNews(@PathVariable Long id, @RequestBody NewsDto newsDto) throws Exception;

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value="/{id}")
    ResponseEntity<Void> deleteNews(@PathVariable Long id) throws Exception;
}
