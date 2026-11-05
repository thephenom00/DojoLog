package cz.cvut.fel.attendance.service.mappers;

import cz.cvut.fel.attendance.service.model.News;
import cz.fel.cvut.attendance.service.model.NewsDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NewsMapper {
    NewsDto toDto(News newsEntity);
    News toEntity(NewsDto newsDto);

    List<NewsDto> toDtoList(List<News> news);

    List<News> toEntityList(List<NewsDto> newsDtos);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateNewsFromDto(NewsDto newsDto, @MappingTarget News news);
}
