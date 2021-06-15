package com.github.gustavoflor.finduserservice.infrastructure.persistence;

import com.github.gustavoflor.finduserservice.core.User;
import com.opencsv.CSVReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserSeeder {

    private static final String TEST_PROFILE = "test";

    @Value("classpath:data.csv")
    private Resource dataFile;

    @Value("classpath:first-relevance-list.txt")
    private Resource firstRelevanceListFile;

    @Value("classpath:second-relevance-list.txt")
    private Resource secondRelevanceListFile;

    private final UserRepository userRepository;

    @EventListener
    public void seed(ContextRefreshedEvent event) {
        if (isNotTest(event) && userRepository.isEmpty()) {
            log.info("> Starting database seed in {}", this.getClass().getName());
            try {
                userRepository.insert(getUsersFromDataFile());
            } catch (IOException exception) {
                log.error("> Error on database seed", exception);
            }
            log.info("> Finished database seed");
        }
    }

    private boolean isNotTest(ContextRefreshedEvent event) {
        String[] profiles = event.getApplicationContext().getEnvironment().getActiveProfiles();
        return Arrays.stream(profiles).noneMatch(TEST_PROFILE::equals);
    }

    private BufferedReader getResourceReader(Resource resource) throws IOException {
        return new BufferedReader(new InputStreamReader(resource.getInputStream()));
    }

    private List<String> getResourceLines(Resource resource) throws IOException {
        return getResourceReader(resource).lines().collect(Collectors.toList());
    }

    private List<User> getUsersFromDataFile() throws IOException {
        log.info("> Reading relevance's list");
        List<String> firstRelevanceList = getResourceLines(firstRelevanceListFile);
        List<String> secondRelevanceList = getResourceLines(secondRelevanceListFile);
        CSVReader csvReader = new CSVReader(getResourceReader(dataFile));
        List<User> users = new ArrayList<>();
        String[] line;
        log.info("> Reading data CSV lines");
        while ((line = csvReader.readNext()) != null) {
            log.info("> CSV Line detail: {}", Arrays.toString(line));
            String id = line[0];
            Integer relevance = getRelevance(id, firstRelevanceList, secondRelevanceList);
            User user = User.builder().id(id).name(line[1]).username(line[2]).relevance(relevance).build();
            users.add(user);
        }
        csvReader.close();
        return users;
    }

    private Integer getRelevance(String id, List<String> firstRelevanceList, List<String> secondRelevanceList) {
        if (firstRelevanceList.contains(id)) {
            return 1;
        }
        if (secondRelevanceList.contains(id)) {
            return 2;
        }
        return 3;
    }

}
