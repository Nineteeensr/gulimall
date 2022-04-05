package com.cyg.gulimall.product.exception;

/**
 * 全局异常处理
 *
 * @author CuiYuangeng
 * @create 2022-04-04 17:19
 */
//@Slf4j
//@RestControllerAdvice(basePackages = "com.cyg.gulimall.product")
//public class GulimallExceptionControllerAdvice {
//
//    @ExceptionHandler(value = MethodArgumentNotValidException.class)
//    public R handleValidException(MethodArgumentNotValidException e) {
//        log.error("数据校验出现问题{}，异常类型：{}", e.getMessage(), e.getClass());
//        BindingResult bindingResult = e.getBindingResult();
//        Map<String, String> errorMap = new HashMap<>();
//        bindingResult.getFieldErrors().forEach((filedError) -> {
//            errorMap.put(filedError.getField(), filedError.getDefaultMessage());
//        });
//        return R.error(BizCodeEnume.VAILD_EXCEPTION.getCode(), BizCodeEnume.VAILD_EXCEPTION.getMsg()).put("data", errorMap);
//    }
//
//    @ExceptionHandler(value = Throwable.class)
//    public R handleException(Throwable throwable) {
//        return R.error(BizCodeEnume.UNKNOW_EXCEPTION.getCode(), BizCodeEnume.UNKNOW_EXCEPTION.getMsg());
//    }
//}
