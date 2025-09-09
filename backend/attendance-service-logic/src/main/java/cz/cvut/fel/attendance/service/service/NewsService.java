package cz.cvut.fel.attendance.service.service;

import cz.cvut.fel.attendance.service.mappers.NewsMapper;
import cz.cvut.fel.attendance.service.model.News;
import cz.cvut.fel.attendance.service.model.User;
import cz.cvut.fel.attendance.service.repository.NewsRepository;
import cz.cvut.fel.attendance.service.repository.UserRepository;
import cz.fel.cvut.attendance.service.model.NewsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsMapper newsMapper;

    private final NewsRepository newsRepository;

    private final MailService mailService;

    private final UserRepository userRepository;

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

    public NewsDto updateNews(Long id, NewsDto newsDto) throws Exception {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new Exception("News with ID " + id + " not found."));

        newsMapper.updateNewsFromDto(newsDto, news);

        newsRepository.save(news);
        return newsMapper.toDto(news);
    }

    public void deleteNews(Long id) throws Exception {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new Exception("News with ID " + id + " not found."));
        newsRepository.delete(news);
    }

    public void sendMail(NewsDto newsDto) {
        List<String> allUserEmails = userRepository.findAll().stream()
                .map(User::getEmail)
                .toList();

        String subject = "Byla zveřejněna nová aktualita";
        String text = "Vážení rodiče a trenéři,\n\n" +
                "byla zveřejněna nová aktualita.\n\n" +
                newsDto.description() + "\n\n" +
                "Pro více informací navštivte naši webovou stránku.\n\n" +
                "Děkujeme, že využíváte naši platformu.\n" +
                "Tým Judo SG Plzeň";

        mailService.sendMail(allUserEmails, subject, text);
    }
}
