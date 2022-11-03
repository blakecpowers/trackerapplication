package io.javabrains.coronavirustracker.controllers;

import io.javabrains.coronavirustracker.models.LocationStats;
import io.javabrains.coronavirustracker.services.CoronavirusDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * This is our Spring controller.
 */
@Controller
public class HomeController {

    // Since this is a service we can AutoWire it into the controller.
    @Autowired
    CoronavirusDataService coronavirusDataService;


    /**
     * Home controller method.
     *
     * model: Spring is giving us this model object and lets us put attributes onto it.
     * Then, it will be available for us when we want to render the HTML.
     */
    @GetMapping("/")
    public String home(Model model) {
        List<LocationStats> allStats = coronavirusDataService.getAllStats();
        int totalReportedCases = allStats.stream().mapToInt(stat -> stat.getLatestTotalCases()).sum();
        int totalNewCases = allStats.stream().mapToInt(stat -> stat.getDiffFromPreviousDay()).sum();

        model.addAttribute("locationStats", allStats);
        model.addAttribute("totalReportedCases", totalReportedCases);
        model.addAttribute("totalNewCases", totalNewCases);

        return "home";
    }

}
