package ru.service.router.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.service.router.models.request.RuleRequest;
import ru.service.router.models.response.dto.RuleDto;
import ru.service.router.services.RuleServiceImpl;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Контроллер для работы с правилами
 */
@RestController
@RequestMapping(path = "/rule")
@RequiredArgsConstructor
public class RuleController {
    private final RuleServiceImpl ruleService;

    @ResponseBody
    @GetMapping(path = "/{id}")
    public RuleDto get(@PathVariable("id") Long id) {
        return ruleService.get(id);
    }

    @ResponseBody
    @PostMapping(path = "/add")
    public RuleDto add(@RequestBody RuleRequest request) {
        return ruleService.add(request);
    }

    @ResponseBody
    @PutMapping(path = "/{id}")
    public RuleDto update(@PathVariable("id") Long id, @RequestBody RuleRequest request) {
        return ruleService.update(id, request);
    }

    @DeleteMapping(path = "/{id}")
    public String delete(@PathVariable("id") Long id) {
        return ruleService.delete(id);
    }

    @ResponseBody
    @GetMapping(path = "/find")
    public String find(HttpServletRequest request) {
        return ruleService.find(request.getQueryString());
    }

    @ResponseBody
    @GetMapping
    public List<RuleDto> getAll() {
        return ruleService.getAll();
    }
}
