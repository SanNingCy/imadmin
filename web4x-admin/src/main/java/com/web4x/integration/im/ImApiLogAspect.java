package com.web4x.integration.im;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NamedThreadLocal;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.support.spring.PropertyPreFilters;
import com.seekweb4.chat.common.annotation.ApiLog;
import com.seekweb4.chat.common.json.AjaxJson;
import com.web4x.common.core.domain.AjaxResult;
import com.web4x.common.core.domain.entity.SysUser;
import com.web4x.common.core.text.Convert;
import com.web4x.common.enums.BusinessStatus;
import com.web4x.common.enums.BusinessType;
import com.web4x.common.enums.OperatorType;
import com.web4x.common.utils.ExceptionUtil;
import com.web4x.common.utils.ServletUtils;
import com.web4x.common.utils.ShiroUtils;
import com.web4x.common.utils.StringUtils;
import com.web4x.framework.aspectj.LogAspect;
import com.web4x.framework.manager.AsyncManager;
import com.web4x.framework.manager.factory.AsyncFactory;
import com.web4x.system.domain.SysOperLog;

/**
 * 将 IM 模块 {@link ApiLog} 操作记录写入若依 {@code sys_oper_log}。
 * 仅记录摘要信息，避免对分页/实体做全量 JSON 序列化引发 StackOverflowError。
 */
@Aspect
@Component
@Order(100)
public class ImApiLogAspect
{
    private static final Logger log = LoggerFactory.getLogger(ImApiLogAspect.class);

    private static final int PARAM_MAX_LENGTH = 2000;

    private static final ThreadLocal<Long> TIME_THREADLOCAL = new NamedThreadLocal<>("ImApiLog Cost Time");

    @Before("@annotation(apiLog)")
    public void doBefore(JoinPoint joinPoint, ApiLog apiLog)
    {
        TIME_THREADLOCAL.set(System.currentTimeMillis());
    }

    @AfterReturning(pointcut = "@annotation(apiLog)", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, ApiLog apiLog, Object jsonResult)
    {
        handleLog(joinPoint, apiLog, null, jsonResult);
    }

    @AfterThrowing(pointcut = "@annotation(apiLog)", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, ApiLog apiLog, Exception e)
    {
        handleLog(joinPoint, apiLog, e, null);
    }

    protected void handleLog(JoinPoint joinPoint, ApiLog apiLog, Exception e, Object jsonResult)
    {
        try
        {
            SysOperLog operLog = new SysOperLog();
            operLog.setStatus(BusinessStatus.SUCCESS.ordinal());
            operLog.setOperIp(safeOperIp());
            operLog.setOperUrl(safeOperUrl());
            fillOperUser(operLog);
            if (e != null)
            {
                operLog.setStatus(BusinessStatus.FAIL.ordinal());
                operLog.setErrorMsg(StringUtils.substring(
                        Convert.toStr(e.getMessage(), ExceptionUtil.getExceptionMessage(e)), 0, 2000));
            }
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            operLog.setMethod(className + "." + methodName + "()");
            operLog.setRequestMethod(ServletUtils.getRequest().getMethod());
            operLog.setTitle(apiLog.value());
            operLog.setBusinessType(resolveBusinessType(apiLog.value()).ordinal());
            operLog.setOperatorType(OperatorType.MANAGE.ordinal());
            operLog.setOperParam(safeOperParam(joinPoint));
            operLog.setJsonResult(safeJsonResult(jsonResult));
            Long start = TIME_THREADLOCAL.get();
            if (start != null)
            {
                operLog.setCostTime(System.currentTimeMillis() - start);
            }
            AsyncManager.me().execute(AsyncFactory.recordOper(operLog));
        }
        catch (Throwable exp)
        {
            log.warn("IM ApiLog 写入操作日志失败: {}", exp.getMessage());
        }
        finally
        {
            TIME_THREADLOCAL.remove();
        }
    }

    private static String safeOperIp()
    {
        try
        {
            return ShiroUtils.getIp();
        }
        catch (Throwable e)
        {
            return "";
        }
    }

    private static String safeOperUrl()
    {
        try
        {
            return StringUtils.substring(ServletUtils.getRequest().getRequestURI(), 0, 255);
        }
        catch (Throwable e)
        {
            return "";
        }
    }

    private static void fillOperUser(SysOperLog operLog)
    {
        try
        {
            SysUser currentUser = ShiroUtils.getSysUser();
            if (currentUser == null)
            {
                return;
            }
            operLog.setOperName(currentUser.getLoginName());
            if (StringUtils.isNotNull(currentUser.getDept())
                    && StringUtils.isNotEmpty(currentUser.getDept().getDeptName()))
            {
                operLog.setDeptName(currentUser.getDept().getDeptName());
            }
        }
        catch (Throwable e)
        {
            // 日志写入不能影响业务请求
        }
    }

    private static String safeJsonResult(Object jsonResult)
    {
        if (jsonResult == null)
        {
            return null;
        }
        try
        {
            Map<String, Object> summary = new LinkedHashMap<>();
            if (jsonResult instanceof AjaxJson ajaxJson)
            {
                summary.put("success", ajaxJson.get("success"));
                summary.put("code", ajaxJson.get("code"));
                summary.put("msg", ajaxJson.get("msg"));
            }
            else if (jsonResult instanceof AjaxResult ajaxResult)
            {
                summary.put("code", ajaxResult.get(AjaxResult.CODE_TAG));
                summary.put("msg", ajaxResult.get(AjaxResult.MSG_TAG));
            }
            else if (jsonResult instanceof Map<?, ?> map)
            {
                if (map.containsKey("success"))
                {
                    summary.put("success", map.get("success"));
                }
                if (map.containsKey("code"))
                {
                    summary.put("code", map.get("code"));
                }
                if (map.containsKey("msg"))
                {
                    summary.put("msg", map.get("msg"));
                }
            }
            if (summary.isEmpty())
            {
                summary.put("type", jsonResult.getClass().getSimpleName());
            }
            return StringUtils.substring(JSONObject.toJSONString(summary), 0, 2000);
        }
        catch (Throwable e)
        {
            return "{\"msg\":\"response omitted\"}";
        }
    }

    private static String safeOperParam(JoinPoint joinPoint)
    {
        try
        {
            Map<String, String[]> map = ServletUtils.getRequest().getParameterMap();
            if (StringUtils.isNotEmpty(map))
            {
                return StringUtils.substring(JSONObject.toJSONString(map), 0, PARAM_MAX_LENGTH);
            }
            return argsArrayToString(joinPoint.getArgs());
        }
        catch (Throwable e)
        {
            return "";
        }
    }

    private static BusinessType resolveBusinessType(String title)
    {
        if (StringUtils.isEmpty(title))
        {
            return BusinessType.OTHER;
        }
        if (title.contains("删除"))
        {
            return BusinessType.DELETE;
        }
        if (title.contains("导出"))
        {
            return BusinessType.EXPORT;
        }
        if (title.contains("导入"))
        {
            return BusinessType.IMPORT;
        }
        if (title.contains("清空"))
        {
            return BusinessType.CLEAN;
        }
        if (title.contains("新增") || title.contains("添加") || title.contains("创建"))
        {
            return BusinessType.INSERT;
        }
        if (title.contains("修改") || title.contains("更新") || title.contains("保存"))
        {
            return BusinessType.UPDATE;
        }
        if (title.contains("授权") || title.contains("分配"))
        {
            return BusinessType.GRANT;
        }
        return BusinessType.OTHER;
    }

    private static String argsArrayToString(Object[] paramsArray)
    {
        StringBuilder params = new StringBuilder();
        if (paramsArray == null || paramsArray.length == 0)
        {
            return params.toString();
        }
        PropertyPreFilters.MySimplePropertyPreFilter filter = new PropertyPreFilters().addFilter()
                .addExcludes(LogAspect.EXCLUDE_PROPERTIES);
        for (Object o : paramsArray)
        {
            if (StringUtils.isNotNull(o) && !isFilterObject(o))
            {
                try
                {
                    params.append(JSONObject.toJSONString(o, filter)).append(' ');
                    if (params.length() >= PARAM_MAX_LENGTH)
                    {
                        return StringUtils.substring(params.toString(), 0, PARAM_MAX_LENGTH);
                    }
                }
                catch (Throwable e)
                {
                    params.append(o.getClass().getSimpleName()).append(' ');
                }
            }
        }
        return params.toString();
    }

    @SuppressWarnings("rawtypes")
    private static boolean isFilterObject(final Object o)
    {
        Class<?> clazz = o.getClass();
        if (clazz.isArray())
        {
            return clazz.getComponentType().isAssignableFrom(MultipartFile.class);
        }
        if (Collection.class.isAssignableFrom(clazz))
        {
            Collection collection = (Collection) o;
            for (Object value : collection)
            {
                return value instanceof MultipartFile;
            }
        }
        if (Map.class.isAssignableFrom(clazz))
        {
            Map map = (Map) o;
            for (Object value : map.entrySet())
            {
                Map.Entry entry = (Map.Entry) value;
                return entry.getValue() instanceof MultipartFile;
            }
        }
        return o instanceof MultipartFile || o instanceof HttpServletRequest || o instanceof HttpServletResponse
                || o instanceof BindingResult;
    }
}
