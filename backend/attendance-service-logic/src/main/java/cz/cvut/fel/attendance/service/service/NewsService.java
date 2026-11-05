package cz.cvut.fel.attendance.service.service;

import cz.cvut.fel.attendance.service.mappers.NewsMapper;
import cz.cvut.fel.attendance.service.model.News;
import cz.cvut.fel.attendance.service.model.User;
import cz.cvut.fel.attendance.service.repository.NewsRepository;
import cz.cvut.fel.attendance.service.repository.UserRepository;
import cz.fel.cvut.attendance.service.model.NewsDto;
import cz.fel.cvut.attendance.service.model.PagedNewsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NewsService {

    private final NewsMapper newsMapper;

    private final NewsRepository newsRepository;

    private final MailService mailService;

    private final UserRepository userRepository;

    @Transactional
    public NewsDto createNews(NewsDto newsDto) {
        News news = newsMapper.toEntity(newsDto);
        news.setDate(LocalDate.now());
        newsRepository.save(news);

        sendMail(newsDto);
        return newsMapper.toDto(news);
    }

    public List<NewsDto> getNews() {
        return newsMapper.toDtoList(newsRepository.findAll());
    }

    public PagedNewsDto getPagedNews(int page, int size) {
        Page<News> newsPage = newsRepository.findAllByOrderByDateDescIdDesc(PageRequest.of(page, size));

        List<NewsDto> dtos = newsMapper.toDtoList(newsPage.getContent());

        return new PagedNewsDto(
                dtos,
                newsPage.getNumber(),
                newsPage.getTotalPages(),
                newsPage.getTotalElements()
        );
    }

    @Transactional
    public NewsDto updateNews(Long id, NewsDto newsDto) throws Exception {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new Exception("News with ID " + id + " not found."));

        newsMapper.updateNewsFromDto(newsDto, news);

        newsRepository.save(news);
        return newsMapper.toDto(news);
    }

    @Transactional
    public void deleteNews(Long id) throws Exception {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new Exception("News with ID " + id + " not found."));
        newsRepository.delete(news);
    }

    public void sendMail(NewsDto newsDto) {
        List<String> allUserEmails = userRepository.findByActiveTrue().stream()
                .map(User::getEmail)
                .toList();
        mailService.sendNewsMail(allUserEmails, newsDto);
    }
}
