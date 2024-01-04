package blossom.project.common.exception;

import blossom.project.common.enums.ResponseCode;

/**
 * @author: ZhangBlossom
 * @date: 2024/1/4 10:35
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * @description:
 */
public class LimitedException  extends BaseException {

 private static final long serialVersionUID = -5534700534739261461L;

 public LimitedException(ResponseCode code) {
  super(code.getMessage(), code);
 }

 public LimitedException(Throwable cause, ResponseCode code) {
  super(code.getMessage(), cause, code);
 }


}
