package kr.ac.ync.radar.controller;

import kr.ac.ync.radar.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class DashboardController {

	private static final int PAGE_SIZE = 30;

	private final DashboardService service;

	@GetMapping("/")
	public String index(Model model) {
		model.addAttribute("menu", "dashboard");
		model.addAttribute("refreshNote", "60초마다 자동 갱신");
		model.addAttribute("summary", service.summary());
		model.addAttribute("hourly", service.hourlyNew());
		model.addAttribute("keywords", service.keywordTop());
		model.addAttribute("runs", service.recentRuns());
		return "dashboard";
	}

	// D2: title search + source filter + server-side paging
	@GetMapping("/articles")
	public String articles(@RequestParam(defaultValue = "0") int page,
			@RequestParam(required = false) String q,
			@RequestParam(required = false) Long sourceId,
			Model model) {
		model.addAttribute("menu", "articles");
		model.addAttribute("page", service.searchArticles(q, sourceId, page, PAGE_SIZE));
		model.addAttribute("sources", service.allSources());
		model.addAttribute("q", q == null ? "" : q);
		model.addAttribute("sourceId", sourceId);
		return "articles";
	}

	// D4: keyword registration
	@GetMapping("/keywords")
	public String keywords(Model model) {
		model.addAttribute("menu", "keywords");
		model.addAttribute("items", service.keywordCounts());
		return "keywords";
	}

	@PostMapping("/keywords")
	public String addKeyword(@RequestParam String word, RedirectAttributes redirect) {
		String error = service.addKeyword(word);
		if (error == null) {
			redirect.addFlashAttribute("message", "키워드를 등록했습니다.");
		} else {
			redirect.addFlashAttribute("error", error);
		}
		return "redirect:/keywords";
	}

	@PostMapping("/keywords/{id}/delete")
	public String deleteKeyword(@PathVariable Long id, RedirectAttributes redirect) {
		service.deleteKeyword(id);
		redirect.addFlashAttribute("message", "키워드를 삭제했습니다.");
		return "redirect:/keywords";
	}

	// D5: full crawl history
	@GetMapping("/runs")
	public String runs(@RequestParam(defaultValue = "0") int page, Model model) {
		model.addAttribute("menu", "runs");
		model.addAttribute("page", service.runPage(page, PAGE_SIZE));
		return "runs";
	}

	// D6: source on/off and interval
	@GetMapping("/sources")
	public String sources(Model model) {
		model.addAttribute("menu", "sources");
		model.addAttribute("sources", service.allSources());
		return "sources";
	}

	@PostMapping("/sources/{id}/toggle")
	public String toggle(@PathVariable Long id) {
		service.toggleSource(id);
		return "redirect:/sources";
	}

	@PostMapping("/sources/{id}/interval")
	public String interval(@PathVariable Long id, @RequestParam int minutes, RedirectAttributes redirect) {
		try {
			service.changeInterval(id, minutes);
			redirect.addFlashAttribute("message", "수집 주기를 " + minutes + "분으로 바꿨습니다. 다음 실행(tick)부터 반영됩니다.");
		} catch (IllegalArgumentException e) {
			redirect.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/sources";
	}
}
