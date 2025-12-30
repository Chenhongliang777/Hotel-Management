package com.homestay.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.homestay.module.system.entity.SystemConfig;
import com.homestay.module.system.mapper.SystemConfigMapper;
import com.homestay.module.system.service.SystemConfigService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SystemConfigServiceImpl extends ServiceImpl<SystemConfigMapper, SystemConfig> implements SystemConfigService {

    @Override
    public String getConfigValue(String key) {
        SystemConfig config = this.getOne(new LambdaQueryWrapper<SystemConfig>()
                .eq(SystemConfig::getConfigKey, key));
        return config != null ? config.getConfigValue() : null;
    }

    @Override
    public BigDecimal getConfigDecimal(String key) {
        String value = getConfigValue(key);
        return value != null ? new BigDecimal(value) : BigDecimal.ZERO;
    }

    @Override
    public Integer getConfigInteger(String key) {
        String value = getConfigValue(key);
        return value != null ? Integer.parseInt(value) : 0;
    }

    @Override
    public boolean updateConfig(String key, String value) {
        SystemConfig config = this.getOne(new LambdaQueryWrapper<SystemConfig>()
                .eq(SystemConfig::getConfigKey, key));
        if (config != null) {
            config.setConfigValue(value);
            return this.updateById(config);
        }
        return false;
    }

    @Override
    public List<SystemConfig> getAllConfigs() {
        return this.list();
    }

    @Override
    public Map<String, String> getConfigMap() {
        List<SystemConfig> configs = this.list();
        Map<String, String> map = new HashMap<>();
        for (SystemConfig config : configs) {
            map.put(config.getConfigKey(), config.getConfigValue());
        }
        return map;
    }
}
