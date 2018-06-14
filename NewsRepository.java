package com.epam.charity.repository;

import com.epam.charity.dto.entity.NewsDTO;

import java.util.List;
import java.util.Optional;

public interface NewsRepository {

  Optional<NewsDTO> getById(String id);

  List<NewsDTO> getNewsPreview(Integer limit, Integer offset, String sortColumn, String language);

  NewsDTO updateNews(NewsDTO newsDTO);

  NewsDTO addNews(NewsDTO newsDTO);

  void deleteNewsById(String id);

  long count(String language);
}