package com.epam.charity.service;

import com.epam.charity.config.log4j.Log4j;
import com.epam.charity.dto.entity.ImageDTO;
import com.epam.charity.dto.entity.LanguageDTO;
import com.epam.charity.dto.entity.NewsDTO;
import com.epam.charity.exceptions.InvalidParamException;
import com.epam.charity.exceptions.ObjectNotFoundException;
import com.epam.charity.repository.ImageRepository;
import com.epam.charity.repository.LangNewsRepository;
import com.epam.charity.repository.NewsRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Service
public class NewsServiceImpl implements NewsService {
  private static @Log4j Logger logger;

  private NewsRepository newsRepository;
  private LangNewsRepository langNewsRepository;
  private ImageService imageService;

  @Autowired
  public NewsServiceImpl(NewsRepository newsRepository,LangNewsRepository langNewsRepository, ImageService imageService) {
    this.newsRepository = newsRepository;
    this.langNewsRepository = langNewsRepository;
    this.imageService = imageService;
  }

  @Override
  public Optional<NewsDTO> getById(String id) {
    Optional<NewsDTO> news = newsRepository.getById(id);
    if (news.isPresent()) {
      insertImgTitleAndLink(news);
      return news;
    }
    throw ObjectNotFoundException.builder().detail(format("News with id=%s not found", id))
        .title("News not found").pointer("").build();
  }

  @Override
  public List<NewsDTO> getNewsPreview(Integer limit, Integer offset, String sortColumn, String lang) {
    List<NewsDTO> newsPreview = newsRepository.getNewsPreview(limit, offset, sortColumn, lang);
    newsPreview.forEach(this::insertImgTitleAndLink);

    return newsPreview;
  }

  @Override
  @Transactional
  public NewsDTO deleteNewsById(String id) {
    Optional<NewsDTO> newsNeededToDelete = getById(id);
    if (newsNeededToDelete.isPresent()) {
      langNewsRepository.deleteAllLangNewsRecords(newsNeededToDelete.get());
      newsRepository.deleteNewsById(id);
      deleteImgTitleAndLink(newsNeededToDelete);
      return newsNeededToDelete.get();
    }
    throw ObjectNotFoundException.builder().detail(format("Album with id=%s not found", id))
        .title("Album not found").pointer("").build();
  }
  
  @Override
  @Transactional
  public NewsDTO updateNews(NewsDTO newsDTO) {
    Optional<NewsDTO> newsNeededToUpdate = getById(newsDTO.getId());
    if (newsNeededToUpdate.isPresent()) {
      langNewsRepository.updateLangNews(newsDTO);

      if(newsDTO.getLanguageDTOSet().isEmpty()){
        throw InvalidParamException.builder().detail("No language selected!").build();
      }
      newsRepository.updateNews(newsDTO);
      insertImgTitleAndLink(newsNeededToUpdate);
      return newsDTO;
    }
    throw ObjectNotFoundException.builder()
        .detail(format("News with id=%s not found", newsDTO.getId())).title("News not found")
        .pointer("").build();

  }

  @Override
  @Transactional
  public NewsDTO addNews(NewsDTO newsDTO) {
    Set<String> langs = newsDTO.getLanguageDTOSet().stream()
            .map(LanguageDTO::getName)
            .collect(Collectors.toSet());

    if(langs.isEmpty()){
      logger.info("Can't create news: no language selected!");
      throw InvalidParamException.builder().detail("No language selected!").build();
    }

    newsDTO = newsRepository.addNews(newsDTO);

    newsDTO = langNewsRepository.addLangNews(newsDTO, langs);
    insertImgTitleAndLink(newsDTO);
    return newsDTO;
  }

  @Override
  public long count(String lang) {
    return newsRepository.count(lang);
  }

  private void insertImgTitleAndLink(Optional<NewsDTO> news) {
    ImageDTO filledImageDTO = imageService.getById(news.get().getImg().getId());
    news.get().getImg().setTitle(filledImageDTO.getTitle());
    news.get().getImg().setLink(filledImageDTO.getLink());
  }

  private void insertImgTitleAndLink(NewsDTO news) {
    ImageDTO filledImageDTO = imageService.getById(news.getImg().getId());
    news.getImg().setTitle(filledImageDTO.getTitle());
    news.getImg().setLink(filledImageDTO.getLink());
  }

  private void deleteImgTitleAndLink(Optional<NewsDTO> newsNeededToDelete) {
    if(newsNeededToDelete.isPresent()){
      imageService.delete(newsNeededToDelete.get().getImg().getId());
    }
  }
}