//#include <jni.h>
//#include <dobby.h>
//#include <lsplant.hpp>
//#include <sys/mman.h>
//#include "elf_util.h"
//#include "logging.h"
//
//JNIEXPORT jint JNICALL
//JNI_OnLoad(JavaVM* vm, void* reserved) {
//    JNIEnv* env;
//    if (vm->GetEnv((void**) &env, JNI_VERSION_1_6) != JNI_OK) {
//        return JNI_ERR;
//    }
//    SandHook::ElfImg art("libart.so");
//#if !defined(__i386__)
//    dobby_enable_near_branch_trampoline();
//#endif
//    lsplant::InitInfo initInfo{
////            .inline_hooker = InlineHooker,
////            .inline_unhooker = InlineUnhooker,
//            .art_symbol_resolver = [&art](std::string_view symbol) -> void* {
//                return art.getSymbAddress(symbol);
//            },
//            .art_symbol_prefix_resolver = [&art](auto symbol) {
//                return art.getSymbPrefixFirstOffset(symbol);
//            },
//    };
//    init_result = lsplant::Init(env, initInfo);
//    return JNI_VERSION_1_6;
//}