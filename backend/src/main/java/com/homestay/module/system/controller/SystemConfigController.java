package com.homestay.module.system.controller;

import com.homestay.common.result.Result;
import com.homestay.module.system.entity.SystemConfig;
import com.homestay.module.system.service.SystemConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "系统配置")
@RestController
@RequestMapping("/config")
public class SystemConfigController {

    @Autowired
    private SystemConfigService systemConfigService;

    @Operation(summary = "获取所有配置")
    @GetMapping("/list")
    public Result<List<SystemConfig>> getAllConfigs() {
        return Result.success(systemConfigService.getAllConfigs());
    }

    @Operation(summary = "获取配置Map")
    @GetMapping("/map")
    public Result<Map<String, String>> getConfigMap() {
        return Result.success(systemConfigService.getConfigMap());
    }

    @Operation(summary = "获取单个配置")
    @GetMapping("/{key}")
    public Result<String> getConfig(@PathVariable String key) {
        return Result.success(systemConfigService.getConfigValue(key));
    }

    @Operation(summary = "更新配置")
    @PutMapping
    public Result<Void> updateConfig(@RequestBody SystemConfig config) {
        systemConfigService.updateConfig(config.getConfigKey(), config.getConfigValue());
        return Result.success();
    }

    @Operation(summary = "批量更新配置")
    @PutMapping("/batch")
    public Result<Void> batchUpdateConfig(@RequestBody Map<String, String> configs) {
        configs.forEach((key, value) -> systemConfigService.updateConfig(key, value));
        return Result.success();
    }
}
