package kr.ac.ync.radar.controller;

import kr.ac.ync.radar.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class DashboardController {

	private static final int PAGE_SIZE = 30;

	private final DashboardService service;

	@GetMapping("/")
	public String index(Model model) {
		model.addAttribute("summary", service.summary());
		model.addAttribute("hourly", service.hourlyNew());
		model.addAttribute("keywords", service.keywordTop());
		model.addAttribute("articles", service.recentArticles());
		model.addAttribute("runs", service.recentRuns());
		model.addAttribute("sources", service.allSources());
		return "dashboard";
	}

	@GetMapping("/articles")
	public String articles(@RequestParam(defaultValue = "0") int page, Model model) {
		model.addAttribute("page", service.articlePage(page, PAGE_SIZE));
		return "articles";
	}

	@PostMapping("/sources/{id}/toggle")
	public String toggle(@PathVariable Long id) {
		service.toggleSource(id);
		return "redirect:/";
	}
}
