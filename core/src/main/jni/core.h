//
// Created by EDY on 2023/10/14.
//

#ifndef BP_DEF_NATIVE_METHOD
#define BP_DEF_NATIVE_METHOD(ret, className, functionName, ...)                \
  extern export "C" ret Java_org_lsposed_lspd_nativebridge_## className ## _ ## functionName (JNI_START, ##  __VA_ARGS__)
#endif
