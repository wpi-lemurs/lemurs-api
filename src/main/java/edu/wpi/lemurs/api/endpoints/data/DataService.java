/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.data;

import edu.wpi.lemurs.api.exceptions.EntityDoesNotExistException;
import edu.wpi.lemurs.api.status.DataStatus;

import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** The {@link DataService} is a service that allows for {@link Data} management. */
@Service
@Transactional
public class DataService {

  private DataRepository dataRepository;

  /** Autowires a {@link DataService}. */
  public DataService(DataRepository dataRepository) {
    this.dataRepository = dataRepository;
  }

  /**
   * Gets the data for a given id.
   *
   * @param id The data's id.
   * @return The {@link Data}.
   * @throws EntityDoesNotExistException Thrown if there is no data with the given id.
   */
  public Data getData(Integer id) throws EntityDoesNotExistException {
    Optional<Data> data = dataRepository.findById(id);

    if (data.isEmpty()) {
      throw new EntityDoesNotExistException();
    }

    return data.get();
  }

  /**
   * Saves data to the database.
   *
   * @param dataDto The {@link DataDto} representing the data.
   */
  public void saveData(DataDto dataDto) {
    Data data =
        new Data(null, dataDto.getType(), dataDto.getData().toString(), DataStatus.NOT_PROCESSED);
    dataRepository.save(data);
  }
}
