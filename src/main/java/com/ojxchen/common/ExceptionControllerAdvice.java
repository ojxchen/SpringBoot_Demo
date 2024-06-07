package com.ojxchen.common;


import com.ojxchen.R.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

//全局异常和日志处理
@ControllerAdvice
public class ExceptionControllerAdvice {

    private static final Logger LOG = LoggerFactory.getLogger(ExceptionControllerAdvice.class);

    @ResponseBody
    @ExceptionHandler(value =Exception.class)
    public Object exceptionHandler(Exception e){
        // 获取异常信息，记录日志
        StackTraceElement stackTraceElement = e.getStackTrace()[0];
        String className = stackTraceElement.getClassName();
        String fileName = stackTraceElement.getFileName();
        int lineNumber = stackTraceElement.getLineNumber();
        String methodName = stackTraceElement.getMethodName();

        LOG.error("类名:{},文件名:{},行数:{},办法名:{}", className, fileName, lineNumber, methodName);
        LOG.error("异常信息: {}", e.getMessage());

        if (e instanceof CustomException) {
            CustomException ce = (CustomException) e;
            return R.failure(ce.getMessage());
        }

        return R.failure(e.getMessage());
    }
}


