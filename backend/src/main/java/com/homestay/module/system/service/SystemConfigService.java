package com.homestay.module.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.homestay.module.system.entity.SystemConfig;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface SystemConfigService extends IService<SystemConfig> {
    
    String getConfigValue(String key);
    
    BigDecimal getConfigDecimal(String key);
    
    Integer getConfigInteger(String key);
    
    boolean updateConfig(String key, String value);
    
    List<SystemConfig> getAllConfigs();
    
    Map<String, String> getConfigMap();
}
