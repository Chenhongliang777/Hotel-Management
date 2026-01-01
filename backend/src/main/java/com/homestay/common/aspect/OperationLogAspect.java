package com.homestay.common.aspect;

import com.homestay.context.UserContextHolder;
import com.homestay.module.system.entity.OperationLog;
import com.homestay.module.system.service.OperationLogService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Aspect
@Component
public class OperationLogAspect {

    @Autowired
    private OperationLogService operationLogService;
    
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Pointcut("execution(* com.homestay.module.*.controller.*.*(..))")
    public void controllerPointcut() {}

    @Around("controllerPointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        // 获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;
        
        // 获取方法信息
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        String className = point.getTarget().getClass().getSimpleName();
        String methodName = method.getName();
        
        // 排除查询类操作（GET请求通常是查询）
        String httpMethod = request != null ? request.getMethod() : "";
        if ("GET".equalsIgnoreCase(httpMethod)) {
            return point.proceed();
        }
        
        // 获取操作描述
        String operation = methodName;
        Operation operationAnnotation = method.getAnnotation(Operation.class);
        if (operationAnnotation != null) {
            operation = operationAnnotation.summary();
        }
        
        // 获取模块名称
        String module = getModuleName(className);
        
        // 获取操作详情
        String operationDetail = getOperationDetail(point, method, operation);
        
        Object result = null;
        int status = 1;
        String errorMsg = null;
        
        try {
            result = point.proceed();
            return result;
        } catch (Throwable e) {
            status = 0;
            errorMsg = e.getMessage();
            throw e;
        } finally {
            long executionTime = System.currentTimeMillis() - startTime;
            
            // 记录操作日志
            try {
                OperationLog operationLog = new OperationLog();
                operationLog.setUserId(UserContextHolder.getUserId());
                operationLog.setUsername(UserContextHolder.getUsername());
                operationLog.setUserType(UserContextHolder.getUserType());
                operationLog.setModule(module);
                operationLog.setOperation(operationDetail);
                operationLog.setMethod(className + "." + methodName);
                operationLog.setIp(request != null ? getIpAddress(request) : "unknown");
                operationLog.setExecutionTime(executionTime);
                operationLog.setStatus(status);
                operationLog.setErrorMsg(errorMsg);
                operationLog.setCreateTime(LocalDateTime.now());
                
                operationLogService.saveLog(operationLog);
            } catch (Exception e) {
                log.error("保存操作日志失败", e);
            }
        }
    }
    
    /**
     * 获取操作详情，包含具体的操作对象信息
     */
    private String getOperationDetail(ProceedingJoinPoint point, Method method, String baseOperation) {
        try {
            Object[] args = point.getArgs();
            String[] paramNames = ((MethodSignature) point.getSignature()).getParameterNames();
            Annotation[][] paramAnnotations = method.getParameterAnnotations();
            
            StringBuilder detail = new StringBuilder(baseOperation);
            Map<String, Object> keyParams = new HashMap<>();
            
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                if (arg == null) continue;
                
                // 检查是否有@PathVariable注解（通常是ID）
                for (Annotation ann : paramAnnotations[i]) {
                    if (ann instanceof PathVariable) {
                        String name = paramNames[i];
                        if (name.toLowerCase().contains("id")) {
                            keyParams.put("ID", arg);
                        }
                    }
                }
                
                // 检查是否有@RequestBody注解（请求体）
                for (Annotation ann : paramAnnotations[i]) {
                    if (ann instanceof RequestBody) {
                        extractKeyFields(arg, keyParams);
                    }
                }
            }
            
            // 构建详情字符串
            if (!keyParams.isEmpty()) {
                detail.append(" [");
                boolean first = true;
                for (Map.Entry<String, Object> entry : keyParams.entrySet()) {
                    if (!first) detail.append(", ");
                    detail.append(entry.getKey()).append(":").append(entry.getValue());
                    first = false;
                }
                detail.append("]");
            }
            
            return detail.toString();
        } catch (Exception e) {
            log.debug("获取操作详情失败", e);
            return baseOperation;
        }
    }
    
    /**
     * 从请求体对象中提取关键字段
     */
    private void extractKeyFields(Object obj, Map<String, Object> keyParams) {
        try {
            if (obj == null) return;
            Class<?> clazz = obj.getClass();
            
            // 尝试获取常见的关键字段
            tryGetField(obj, clazz, "id", "ID", keyParams);
            tryGetField(obj, clazz, "orderNo", "订单号", keyParams);
            tryGetField(obj, clazz, "roomNumber", "房间号", keyParams);
            tryGetField(obj, clazz, "roomId", "房间ID", keyParams);
            tryGetField(obj, clazz, "guestName", "宾客", keyParams);
            tryGetField(obj, clazz, "name", "名称", keyParams);
            tryGetField(obj, clazz, "username", "用户名", keyParams);
            tryGetField(obj, clazz, "itemName", "商品", keyParams);
        } catch (Exception e) {
            log.debug("提取关键字段失败", e);
        }
    }
    
    private void tryGetField(Object obj, Class<?> clazz, String fieldName, String displayName, Map<String, Object> keyParams) {
        try {
            java.lang.reflect.Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            Object value = field.get(obj);
            if (value != null && !value.toString().isEmpty()) {
                keyParams.put(displayName, value);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // 字段不存在，忽略
        }
    }
    
    private String getModuleName(String className) {
        if (className.contains("Order")) return "订单管理";
        if (className.contains("Room")) return "房间管理";
        if (className.contains("Guest")) return "宾客管理";
        if (className.contains("Employee")) return "员工管理";
        if (className.contains("Cleaning")) return "清洁管理";
        if (className.contains("Inventory")) return "库存管理";
        if (className.contains("Pos")) return "消费管理";
        if (className.contains("Auth")) return "认证管理";
        if (className.contains("System") || className.contains("Config")) return "系统管理";
        if (className.contains("Log")) return "日志管理";
        if (className.contains("File")) return "文件管理";
        return "其他";
    }
    
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个代理时取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
