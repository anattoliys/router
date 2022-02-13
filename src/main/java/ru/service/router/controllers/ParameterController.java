package ru.service.router.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.service.router.models.request.ParameterRequest;
import ru.service.router.models.response.dto.ParameterDto;
import ru.service.router.services.ParameterServiceImpl;

import java.util.List;

/**
 * Контроллер для работы с параметрами
 */
@RestController
@RequestMapping(path = "/parameter")
@RequiredArgsConstructor
public class ParameterController {
    private final ParameterServiceImpl parameterService;

    @ResponseBody
    @GetMapping(path = "/{id}")
    public ParameterDto get(@PathVariable("id") Long id) {
        return parameterService.get(id);
    }

    @ResponseBody
    @PostMapping(path = "/add")
    public ParameterDto add(@RequestBody ParameterRequest request) {
        return parameterService.add(request);
    }

    @ResponseBody
    @PutMapping(path = "/{id}")
    public ParameterDto update(@PathVariable("id") Long id, @RequestBody ParameterRequest request) {
        return parameterService.update(id, request);
    }

    @ResponseBody
    @DeleteMapping(path = "/{id}")
    public String delete(@PathVariable("id") Long id) {
        return parameterService.delete(id);
    }

    @ResponseBody
    @GetMapping
    public List<ParameterDto> getAll() {
        return parameterService.getAll();
    }
}
