package com.epam.charity.repository;

import static org.jooq.impl.DSL.row;

import com.epam.charity.config.log4j.Log4j;
import com.epam.charity.dto.entity.ImageDTO;
import com.epam.charity.dto.entity.LanguageDTO;
import com.epam.charity.dto.entity.NewsDTO;
import com.epam.charity.exceptions.ConflictException;
import com.epam.charity.exceptions.EntityAlreadyExistsException;
import com.epam.charity.jooq.dto.tables.*;
import com.epam.charity.jooq.dto.tables.records.LanguageNewsRecord;
import com.epam.charity.jooq.dto.tables.records.LanguageRecord;
import com.epam.charity.jooq.dto.tables.records.NewsRecord;
import com.epam.charity.utils.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.jooq.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


@Repository
//@Transactional
public class NewsRepositoryImpl implements NewsRepository {

    private static @Log4j
    Logger logger;

    private DSLContext dslContext;

    @Autowired
    public NewsRepositoryImpl(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    /**
     * Get simple news
     *
     * @param id news id
     * @return news preview with requested id
     */
    @Override
    public Optional<NewsDTO> getById(String id) {

        logger.info("Requested news with id " + id);

        // Get all languages for news with indicated id
        List<Record1<String>> langs = this.dslContext
                .select(Language.LANGUAGE.LANGUAGE_)
                .from(LanguageNews.LANGUAGE_NEWS)
                .join(Language.LANGUAGE).on(LanguageNews.LANGUAGE_NEWS.LANGUAGE_ID.eq(Language.LANGUAGE.ID))
                .where(LanguageNews.LANGUAGE_NEWS.NEWS_ID.eq(Long.parseLong(id)))
                .fetch();

        // make set of language DTO
        Set<LanguageDTO> languageDTOSet = new HashSet<>();
        for (Record1<String> lang: langs){
            languageDTOSet.add(new LanguageDTO(lang.get(Language.LANGUAGE.LANGUAGE_)));
        }

        // get news by id
        Optional<Record> recordOpt = Optional.ofNullable(this.dslContext
                .select()
                .from(News.NEWS)
                .where(News.NEWS.ID.eq(Long.parseLong(id)))
                .fetchOne());

        return recordOpt.map(record -> NewsDTO.builder().id("" + record.get(News.NEWS.ID))
                .content(record.get(News.NEWS.CONTENT))
                /*converting to millis*/
                .date(record.get(News.NEWS.CDATE).toInstant().toEpochMilli())
                .header(record.get(News.NEWS.HEADER))
                .img(new ImageDTO(record.get(News.NEWS.IMG_ID), "", ""))
                .url(record.get(News.NEWS.URL))
                .languageDTOSet(languageDTOSet)
                .build());
    }

    /**
     * Get all news previews ordered by date
     *
     * @return list of ordered news preview objects
     */
    @Override
    public List<NewsDTO> getNewsPreview(Integer limit, Integer offset, String sortColumn, String language) {

        logger.info("getNewsPreview(" + limit + ", " + offset + ", " + sortColumn + ")");
        Field field = News.NEWS.field(sortColumn);
        if (field != null) {
            String sortFieldName = field.getName();
            if (!sortFieldName.equals(sortColumn)) // sortColumn is not in the list of fields in db
            {
                sortColumn = "cdate";
            }
        }
        if (sortColumn.equals("date")) {
            sortColumn = "cdate";
        }
        SortField sortField = News.NEWS.field(sortColumn).desc();


        Result<Record> records = this.dslContext.select().from(News.NEWS)
                .join(LanguageNews.LANGUAGE_NEWS).on(News.NEWS.ID.eq(LanguageNews.LANGUAGE_NEWS.NEWS_ID))
                .join(Language.LANGUAGE).on(LanguageNews.LANGUAGE_NEWS.LANGUAGE_ID.eq(Language.LANGUAGE.ID))
                .where(Language.LANGUAGE.LANGUAGE_.eq(language))
                .orderBy(sortField)
                .limit(limit).offset(offset).fetch();


        return records.stream()
                .map(record -> NewsDTO.builder().id("" + record.get(News.NEWS.ID))
                        .content(record.get(News.NEWS.CONTENT))
                        /*converting to millis*/
                        .date(record.get(News.NEWS.CDATE).toInstant().toEpochMilli())
                        .header(record.get(News.NEWS.HEADER))
                        .img(new ImageDTO(record.get(News.NEWS.IMG_ID), "", ""))
                        .url(record.get(News.NEWS.URL))
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public NewsDTO updateNews(NewsDTO newsDTO) {
        try {
            int numRowsAffected = this.dslContext.update(News.NEWS)
                    .set(
                            row(News.NEWS.CDATE, News.NEWS.HEADER, News.NEWS.CONTENT, News.NEWS.IMG_ID,
                                    News.NEWS.URL),
                            row(new java.sql.Timestamp(newsDTO.getDate()), newsDTO.getHeader(),
                                    newsDTO.getContent(), newsDTO.getImg().getId(), newsDTO.getUrl()))
                    .where(News.NEWS.ID.eq(Long.parseLong(newsDTO.getId()))).execute();
            if (numRowsAffected < 1) {
                throw new ConflictException("SQL update failed", "Now rows affected", "");
            }
            return newsDTO;
        } catch (org.jooq.exception.DataAccessException ex) {
            logger.info(ex.getMessage());
            throw ConflictException.builder().detail(String
                    .format("Can't update News with id=%d: due to some params constraint", newsDTO.getId()))
                    .title("News update fail").pointer("").build();
        }
    }


    @Override
    public NewsDTO addNews(NewsDTO newsDTO) {
        try {
            String hash = StringUtils.getHashUsingLettersAndDigit(getMajorInfoInStringFromNews(newsDTO));
            checkNewsForExistingUsingHash(hash);

            NewsRecord newsRecord = insertNewsRecord(newsDTO, hash);

            return getById("" + newsRecord.get(News.NEWS.ID)).orElseThrow(
                    () -> ConflictException.builder().detail("Can't save News due to some params constraint")
                            .title("News update fail").pointer("").build());
        } catch (org.jooq.exception.DataAccessException ex) {
            logger.info(ex.getMessage());
            throw ConflictException.builder().detail("Can't save News due to some params constraint")
                    .title("News update fail").pointer("").build();
        }
    }

    public NewsRecord insertNewsRecord(NewsDTO newsDTO, String hash) {
        NewsRecord newsRecord;
        if (newsDTO.getDate() == 0) {
            newsRecord = this.dslContext
                    .insertInto(News.NEWS, News.NEWS.HEADER, News.NEWS.CONTENT, News.NEWS.IMG_ID,
                            News.NEWS.URL, News.NEWS.HASH)
                    .values(newsDTO.getHeader(), newsDTO.getContent(), newsDTO.getImg().getId(),
                            newsDTO.getUrl(), hash)
                    .returning(News.NEWS.ID).fetchOne();
        } else {
            newsRecord = this.dslContext
                    .insertInto(News.NEWS, News.NEWS.CDATE, News.NEWS.HEADER, News.NEWS.CONTENT,
                            News.NEWS.IMG_ID, News.NEWS.URL, News.NEWS.HASH)
                    .values(new java.sql.Timestamp(newsDTO.getDate()), newsDTO.getHeader(),
                            newsDTO.getContent(), newsDTO.getImg().getId(), newsDTO.getUrl(), hash)
                    .returning(News.NEWS.ID).fetchOne();
        }

        return newsRecord;
    }

    private void checkNewsForExistingUsingHash(String hash) {
        logger.info("News request made with hash " + hash);

        Optional.ofNullable(selectOneFromNewsUsingHash(hash)).ifPresent((s) -> {
            throw EntityAlreadyExistsException.builder().detail("News already exists")
                    .title("Registration error").build();
        });
    }

    public Record selectOneFromNewsUsingHash(String hash) {
        return dslContext.select().from(News.NEWS).where(News.NEWS.HASH.eq(hash)).fetchOne();
    }

    @Override
    public void deleteNewsById(String id) {
        int numRowsAffected =
                this.dslContext.delete(News.NEWS).where(News.NEWS.ID.eq(Long.parseLong(id))).execute();
        if (numRowsAffected < 1) {
            throw new ConflictException("SQL delete failed", "Now rows affected", "");
        }
    }

    @Override
    public long count(String language) {
        return this.dslContext.selectCount().from(News.NEWS)
                .join(LanguageNews.LANGUAGE_NEWS).on(News.NEWS.ID.eq(LanguageNews.LANGUAGE_NEWS.NEWS_ID))
                .join(Language.LANGUAGE).on(Language.LANGUAGE.ID.eq(LanguageNews.LANGUAGE_NEWS.LANGUAGE_ID))
                .where(Language.LANGUAGE.LANGUAGE_.eq(language)).
                        fetchOne(0, long.class);
    }

    private String getMajorInfoInStringFromNews(NewsDTO news) {
        StringBuilder builder = new StringBuilder();
        builder.append(news.getContent());
        builder.append(news.getHeader());
        builder.append(news.getUrl());
        return builder.toString();
    }
}