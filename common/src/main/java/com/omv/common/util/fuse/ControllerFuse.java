package com.omv.common.util.fuse;

import com.omv.common.util.basic.ValueUtil;
import com.omv.common.util.error.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

/**
 * Created by zwj on 2018/3/13.
 */
@ControllerAdvice
@Order(100)
public class ControllerFuse {
    Logger logger = LoggerFactory.getLogger(ControllerFuse.class);

    @ExceptionHandler(value = {Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public String handlweOtherExceptions(final Exception ex, final WebRequest req) {
        ex.printStackTrace();
        logger.error(ex.getMessage());
        if (ex instanceof CustomException) {
            return ValueUtil.toError(((CustomException) ex).getCode(), ex.getMessage());
        } else if (ex instanceof IllegalArgumentException) {
            return ValueUtil.toError("710", ex.getMessage());
        }
        return ValueUtil.toError("740", ex.getMessage());
    }
}
