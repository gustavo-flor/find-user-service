package com.github.gustavoflor.finduserservice.infrastructure.persistence;

import com.github.gustavoflor.finduserservice.core.User;
import com.opencsv.CSVReader;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserSeeder {

    @Value("classpath:data.csv")
    private Resource dataFile;

    @Value("classpath:first-relevance-list.txt")
    private Resource firstRelevanceListFile;

    @Value("classpath:second-relevance-list.txt")
    private Resource secondRelevanceListFile;

    private final UserRepository userRepository;

    @EventListener
    public void seed(ContextRefreshedEvent event) throws IOException {
        if (userRepository.isEmpty()) {
            userRepository.insert(getUsersFromDataFile());
        }
    }

    private BufferedReader getResourceReader(Resource resource) throws IOException {
        return new BufferedReader(new InputStreamReader(resource.getInputStream()));
    }

    private List<String> getResourceLines(Resource resource) throws IOException {
        return getResourceReader(resource).lines().collect(Collectors.toList());
    }

    private List<User> getUsersFromDataFile() throws IOException {
        List<String> firstRelevanceList = getResourceLines(firstRelevanceListFile);
        List<String> secondRelevanceList = getResourceLines(secondRelevanceListFile);
        CSVReader csvReader = new CSVReader(getResourceReader(dataFile));
        List<User> users = new ArrayList<>();
        String[] line;
        while ((line = csvReader.readNext()) != null) {
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
