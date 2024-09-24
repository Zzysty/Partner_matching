package com.zzy.yupaobackend.exception;

import com.zzy.yupaobackend.common.BaseResponse;
import com.zzy.yupaobackend.common.ErrorCode;
import com.zzy.yupaobackend.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public BaseResponse businessExceptionHandler(BusinessException e) {
        log.info("BusinessException:" + e.getMessage(), e);
        return ResultUtils.error(e.getCode(), e.getMessage(), e.getDescription());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse runtimeExceptionHandler(RuntimeException e) {
        log.info("RuntimeException:", e);
        return ResultUtils.error(ErrorCode.SYSTRM_ERROR, e.getMessage(), "");
    }
}
