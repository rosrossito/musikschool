package com.epam.charity.repository;

import com.epam.charity.config.log4j.LogInjector;
import com.epam.charity.dto.entity.ApplicationDTO;
import com.epam.charity.exceptions.EntityAlreadyExistsException;
import com.epam.charity.jooq.dto.tables.Applications;
import org.jooq.Record;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ApplicationRepositoryImplTest {

  private ApplicationRepositoryImpl applicationRepository;

  private Applications applications;

  @Before
  public void initApplicationRepository() {
    applicationRepository = spy(new ApplicationRepositoryImpl(null, null, null));
    LogInjector logInjector = new LogInjector();
    logInjector.postProcessBeforeInitialization(applicationRepository, null);
  }

  @Before
  public void initApplications() {
    applications = Applications.APPLICATIONS;
  }

  @Test
  public void saveApplicationThatWasntBefore() throws Exception {
    ApplicationDTO applicationDTO = new ApplicationDTO();
    doReturn(null).when(applicationRepository).selectOneFromApplicationUsingHash(eq(applications),
        anyString());
    doReturn("data").when(applicationRepository)
        .getMajorInfoInStringFromApplication(applicationDTO);
    doNothing().when(applicationRepository).insertApplication(eq(applicationDTO), eq(applications),
        anyString());
    applicationRepository.save(applicationDTO);

    verify(applicationRepository).insertApplication(eq(applicationDTO), eq(applications),
        anyString());
  }

  @Test(expected = EntityAlreadyExistsException.class)
  public void saveApplicationThatAlreadyExists() throws Exception {
    ApplicationDTO applicationDTO = new ApplicationDTO();
    Record record = mock(Record.class);
    doReturn(record).when(applicationRepository).selectOneFromApplicationUsingHash(eq(applications),
        anyString());
    doReturn("data").when(applicationRepository)
        .getMajorInfoInStringFromApplication(applicationDTO);

    applicationRepository.save(applicationDTO);
  }
}
