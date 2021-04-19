
#include "NativeUtils.h"

JNIEXPORT void JNICALL jniInit(JNIEnv *env, jclass clazz, jint version) {
    void *handle = dlopen("libc.so", RTLD_LAZY);
    LogD("%s handle=%x version = %d", __FUNCTION__, handle, version)
}

static const JNINativeMethod gHookMethods[] = {
        {"nativeInit", "(I)V", (void *) jniInit},
};

extern "C" __attribute__ ((visibility("default"))) jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env = NULL;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_4) != JNI_OK) {
        return -1;
    }

    jclass clazz;

    clazz = env->FindClass("com/gameassist/plugin/demo/NativeUtils");
    if (clazz == NULL) {
        return -1;
    }

    if (env->RegisterNatives(clazz, gHookMethods, sizeof(gHookMethods) / sizeof(gHookMethods[0])) <
        0) {
        return -1;
    }
    return JNI_VERSION_1_4;
}
