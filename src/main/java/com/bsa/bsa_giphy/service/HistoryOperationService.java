package com.bsa.bsa_giphy.service;

import com.bsa.bsa_giphy.dto.History;
import com.bsa.bsa_giphy.exception.DataNotFoundException;
import com.bsa.bsa_giphy.exception.FileProcessingException;
import com.fasterxml.jackson.databind.MappingIterator;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class HistoryOperationService {

    private final CsvMapper csvMapper = new CsvMapper();

    @Value("${app.users.dir}")
    private String users;

    public List<History> getUserHistory(String userId) {

        Path dirPath = Path.of(users + userId),
                filePath = Path.of(dirPath + "/history.csv");

        if(!Files.exists(filePath) || !Files.exists(dirPath)) {
            throw new DataNotFoundException();
        }
        File csvFile = new File(filePath.toString());

        try {

            MappingIterator<History> historyIter =
                    csvMapper.readerWithTypedSchemaFor(History.class).readValues(csvFile);

            List<History> result = historyIter.readAll();
            if(result.isEmpty()) {
                throw new DataNotFoundException();
            }
            return result;

        } catch (IOException e) {
            throw new FileProcessingException("Cannot read values from history");
        }

    }

    public void deleteUserHistory(String userId) {

        Path dirPath = Path.of(users + userId),
                filePath = Path.of(dirPath + "/history.csv");

        if(!Files.exists(filePath) || !Files.exists(dirPath)) {
            throw new DataNotFoundException();
        }
        try {

            PrintWriter pw = new PrintWriter(filePath.toString()); // clean history file
            pw.close();

        } catch (IOException e) {
            throw new FileProcessingException("Could not delete history");
        }

    }
}
