package com.epam.charity.service;

import com.epam.charity.dto.entity.NewsDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface NewsService {
  Optional<NewsDTO> getById(String id);

  List<NewsDTO> getNewsPreview(Integer limit, Integer offset, String sortColumn, String lang);

  NewsDTO deleteNewsById(String id);

  NewsDTO updateNews(NewsDTO newsDTO);

  NewsDTO addNews(NewsDTO newsDTO);

  long count(String lang);
}