package io.javabrains.coronavirustracker.services;

import io.javabrains.coronavirustracker.models.LocationStats;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the service that gives us the data. When the application loads, it calls to the URL and loads the data.
 * Since it's a boot spring service
 */
@Service
public class CoronavirusDataService {

    private String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
    private List<LocationStats> allStats = new ArrayList<>();

    public List<LocationStats> getAllStats() {
        return allStats;
    }

    /**
     * Fetching data daily from the COVID-19 Data Repository by the Center for Systems Science and Engineering (CSSE) at Johns Hopkins University.
     *
     * @throws IOException          Client sending request may cause an unexpected IOException
     * @throws InterruptedException Client sending request may cause an unexpected InterruptedException
     */
    @Scheduled(cron = "* * 1 * * *")
    @PostConstruct
    public void fetchVirusData() throws IOException, InterruptedException {
        final List<LocationStats> newStats = new ArrayList<>();
        final HttpClient client = HttpClient.newHttpClient();
        final HttpRequest request = HttpRequest.newBuilder().uri(URI.create(VIRUS_DATA_URL)).build();

        //
        // Send the client this request.
        //
        final HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        final StringReader csvBodyReader = new StringReader(httpResponse.body());
        final Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);
        for (CSVRecord record : records) {
            final LocationStats locationStat = new LocationStats();
            locationStat.setState(record.get("Province/State"));
            locationStat.setCountry(record.get("Country/Region"));

            int latestCases = Integer.parseInt(record.get(record.size()-1));
            int previousDayCases = Integer.parseInt(record.get(record.size()-2));
            locationStat.setLatestTotalCases(latestCases);
            locationStat.setDiffFromPreviousDay(latestCases - previousDayCases);

            newStats.add(locationStat);
        }

        this.allStats = newStats;
    }

}
