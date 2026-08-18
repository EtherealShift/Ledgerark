package org.ledgerark.framework.web.exception;

import org.ledgerark.common.entity.Result;
import org.ledgerark.common.enums.ResultCode;
import org.ledgerark.common.exception.user.UserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 
 * @author ruoyi
 */
@RestControllerAdvice
public class GlobalExceptionHandler
{
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<String> handlerMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return Result.failure(ResultCode.PARAM_ERROR, message);
    }

    // 处理业务异常
    @ExceptionHandler(UserException.class)
    public Result<?> handlerUserException(UserException e) {
        return Result.failure(e.getCode(), e.getMessage(), null);
    }

//    /**
//     * 权限校验异常
//     */
//    @ExceptionHandler(AccessDeniedException.class)
//    public AjaxResult handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request)
//    {
//        String requestURI = request.getRequestURI();
//        log.error("请求地址'{}',权限校验失败'{}'", requestURI, e.getMessage());
//        return AjaxResult.error(HttpStatus.FORBIDDEN, "没有权限，请联系管理员授权");
//    }
//
//    /**
//     * 请求方式不支持
//     */
//    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
//    public AjaxResult handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e,
//            HttpServletRequest request)
//    {
//        String requestURI = request.getRequestURI();
//        log.error("请求地址'{}',不支持'{}'请求", requestURI, e.getMethod());
//        return AjaxResult.error(e.getMessage());
//    }
//
//    /**
//     * 业务异常
//     */
//    @ExceptionHandler(ServiceException.class)
//    public AjaxResult handleServiceException(ServiceException e, HttpServletRequest request)
//    {
//        log.error(e.getMessage(), e);
//        Integer code = e.getCode();
//        return StringUtils.isNotNull(code) ? AjaxResult.error(code, e.getMessage()) : AjaxResult.error(e.getMessage());
//    }
//
//    /**
//     * 请求路径中缺少必需的路径变量
//     */
//    @ExceptionHandler(MissingPathVariableException.class)
//    public AjaxResult handleMissingPathVariableException(MissingPathVariableException e, HttpServletRequest request)
//    {
//        String requestURI = request.getRequestURI();
//        log.error("请求路径中缺少必需的路径变量'{}',发生系统异常.", requestURI, e);
//        return AjaxResult.error(String.format("请求路径中缺少必需的路径变量[%s]", e.getVariableName()));
//    }
//
//    /**
//     * 请求参数类型不匹配
//     */
//    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
//    public AjaxResult handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletRequest request)
//    {
//        String requestURI = request.getRequestURI();
//        String value = Convert.toStr(e.getValue());
//        if (StringUtils.isNotEmpty(value))
//        {
//            value = EscapeUtil.clean(value);
//        }
//        log.error("请求参数类型不匹配'{}',发生系统异常.", requestURI, e);
//        return AjaxResult.error(String.format("请求参数类型不匹配，参数[%s]要求类型为：'%s'，但输入值为：'%s'", e.getName(), e.getRequiredType().getName(), value));
//    }
//
//    /**
//     * 拦截未知的运行时异常
//     */
//    @ExceptionHandler(RuntimeException.class)
//    public AjaxResult handleRuntimeException(RuntimeException e, HttpServletRequest request)
//    {
//        String requestURI = request.getRequestURI();
//        log.error("请求地址'{}',发生未知异常.", requestURI, e);
//        return AjaxResult.error(e.getMessage());
//    }
//
//    /**
//     * 系统异常
//     */
//    @ExceptionHandler(Exception.class)
//    public AjaxResult handleException(Exception e, HttpServletRequest request)
//    {
//        String requestURI = request.getRequestURI();
//        log.error("请求地址'{}',发生系统异常.", requestURI, e);
//        return AjaxResult.error(e.getMessage());
//    }
//
//    /**
//     * 自定义验证异常
//     */
//    @ExceptionHandler(BindException.class)
//    public AjaxResult handleBindException(BindException e)
//    {
//        log.error(e.getMessage(), e);
//        String message = e.getAllErrors().get(0).getDefaultMessage();
//        return AjaxResult.error(message);
//    }
//
//    /**
//     * 自定义验证异常
//     */
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public Object handleMethodArgumentNotValidException(MethodArgumentNotValidException e)
//    {
//        log.error(e.getMessage(), e);
//        String message = e.getBindingResult().getFieldError().getDefaultMessage();
//        return AjaxResult.error(message);
//    }

}
