package com.epam.charity.controller;

import com.epam.charity.config.log4j.Log4j;
import com.epam.charity.dto.entity.DataWrapperDTO;
import com.epam.charity.dto.entity.GetAllDataWraperDTO;
import com.epam.charity.dto.entity.NewsDTO;
import com.epam.charity.service.NewsService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Locale;


@RestController
@RequestMapping(Mappings.NEWS)
public class NewsRestController {

  private static @Log4j Logger logger;

  private NewsService newsService;

  @Autowired
  public NewsRestController(NewsService newsService) {
    this.newsService = newsService;
  }

  @RequestMapping(value = Mappings.VAR_ID, method = RequestMethod.GET)
  public DataWrapperDTO<NewsDTO> getById(@PathVariable("id") String id) {
    logger.info("News requested with id " + id);
    return new DataWrapperDTO<>(newsService.getById(id).orElse(null));
  }

  @RequestMapping(method = RequestMethod.GET)
  public GetAllDataWraperDTO<List> getNewsPreview(
      @RequestParam(name = "_limit", required = false, defaultValue = "8") Integer limit,
      @RequestParam(name = "_sort", required = false, defaultValue = "cdate") String sortColumn,
      @RequestParam(name = "_start", required = false, defaultValue = "0") Integer offset,
      @RequestParam(name = "_lang", required = false, defaultValue = "ru") String language) {

    long count = newsService.count(language);

    return new GetAllDataWraperDTO<>(newsService.getNewsPreview(limit, offset, sortColumn, language), count);
  }

  @RequestMapping(value = Mappings.VAR_ID, method = RequestMethod.DELETE)
  public DataWrapperDTO<NewsDTO> deleteNews(@PathVariable String id) {
    logger.info("News requested delete id " + id);
    return new DataWrapperDTO<>(newsService.deleteNewsById(id));
  }

  @RequestMapping(value = Mappings.VAR_ID, method = RequestMethod.PUT)
  public DataWrapperDTO<NewsDTO> updateNews(@PathVariable String id,
      @Valid @RequestBody NewsDTO newsDTO) {
    logger.info("News requested update id " + id);
//    newsDTO.setId(id);
    return new DataWrapperDTO<>(newsService.updateNews(newsDTO));
  }

  @RequestMapping(method = RequestMethod.POST)
  public DataWrapperDTO<NewsDTO> addNews(@Valid @RequestBody NewsDTO newsDTO) {
    logger.info("Requested: insert news.");
    return new DataWrapperDTO<>(newsService.addNews(newsDTO));
  }
}