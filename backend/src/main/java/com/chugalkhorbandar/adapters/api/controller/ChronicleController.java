package com.chugalkhorbandar.adapters.api.controller;

import com.chugalkhorbandar.adapters.api.dto.ChronicleResponseDto;
import com.chugalkhorbandar.adapters.api.dto.ChronicleWriteExecutionResponseDto;
import com.chugalkhorbandar.adapters.api.mapper.ChronicleDtoMapper;
import com.chugalkhorbandar.application.chronicle.ChronicleCategory;
import com.chugalkhorbandar.application.chronicle.ChronicleNotFoundException;
import com.chugalkhorbandar.application.chronicle.ChronicleVisibility;
import com.chugalkhorbandar.application.chronicle.ChronicleWriterProperties;
import com.chugalkhorbandar.application.chronicle.ChronicleWriterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chronicles")
@Tag(name = "Chronicles", description = "Permanent Jungle history")
public class ChronicleController {

    private final ChronicleWriterService chronicleWriterService;
    private final ChronicleWriterProperties properties;

    public ChronicleController(ChronicleWriterService chronicleWriterService, ChronicleWriterProperties properties) {
        this.chronicleWriterService = chronicleWriterService;
        this.properties = properties;
    }

    @GetMapping
    @Operation(summary = "List chronicles")
    public List<ChronicleResponseDto> list() {
        return chronicleWriterService.listChronicles().stream().map(ChronicleDtoMapper::toDto).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Chronicle details")
    public ChronicleResponseDto get(@PathVariable String id) {
        return ChronicleDtoMapper.toDto(chronicleWriterService.getChronicle(id));
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "List chronicles by category")
    public List<ChronicleResponseDto> byCategory(@PathVariable String category) {
        return chronicleWriterService
                .listByCategory(ChronicleCategory.valueOf(category.toUpperCase()))
                .stream()
                .map(ChronicleDtoMapper::toDto)
                .toList();
    }

    @GetMapping("/visibility/{visibility}")
    @Operation(summary = "List chronicles by visibility")
    public List<ChronicleResponseDto> byVisibility(@PathVariable String visibility) {
        return chronicleWriterService
                .listByVisibility(ChronicleVisibility.valueOf(visibility.toUpperCase()))
                .stream()
                .map(ChronicleDtoMapper::toDto)
                .toList();
    }

    @GetMapping("/dev/all")
    @Operation(summary = "All chronicles (developer)")
    public List<ChronicleResponseDto> allDev() {
        return list();
    }

    @GetMapping("/dev/execution")
    @Operation(summary = "Latest chronicle write execution (developer)")
    public ChronicleWriteExecutionResponseDto latestExecution() {
        return ChronicleDtoMapper.toDto(chronicleWriterService
                .getLatestWriteRun()
                .orElseThrow(ChronicleNotFoundException::new));
    }

    @PostMapping("/dev/write")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Write chronicles from candidates (developer)")
    public ChronicleWriteExecutionResponseDto write() {
        if (!properties.isDeveloperWriteEnabled()) {
            throw new IllegalStateException("Developer chronicle write is disabled");
        }
        return ChronicleDtoMapper.toDto(chronicleWriterService.writeChronicles());
    }
}
