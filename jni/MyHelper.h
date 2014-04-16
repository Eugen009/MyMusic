#ifndef __MY_HELPER_H__
#define __MY_HELPER_H__
#include <android/log.h>
#include <string>
#define TAG "MyLog"
#define LOG_INFO(...) __android_log_print( ANDROID_LOG_INFO, TAG, __VA_ARGS__ );
#define LOG_DEBUG(...) __android_log_print( ANDROID_LOG_DEBUG, TAG, __VA_ARGS__ );
#define LOG_WARN(...) __android_log_print( ANDROID_LOG_WARN, TAG, __VA_ARGS__ );
#define LOG_ERROR(...) __android_log_print( ANDROID_LOG_FATAL, TAG, __VA_ARGS__ );

std::string jstring2str(JNIEnv* env, jstring jstr);

#endif
