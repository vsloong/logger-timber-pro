#include <jni.h>
#include <string>
#include <sys/mman.h>
#include <fcntl.h>
#include <unistd.h>
#include <cstring>
#include <iostream>
#include <android/log.h>


extern "C"
JNIEXPORT jstring JNICALL
Java_com_vsloong_logger_timber_pro_mmap_CPencil_stringFromJNI(
        JNIEnv *env,
        jobject thiz
) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C"
JNIEXPORT void JNICALL
Java_com_vsloong_logger_timber_pro_mmap_CPencil_writeLog(
        JNIEnv *env,
        jobject thiz,
        jstring log_path,
        jstring log_message
) {
    const char *file_path = env->GetStringUTFChars(log_path, nullptr);
    const char *file_content = env->GetStringUTFChars(log_message, nullptr);

    // release
    env->ReleaseStringUTFChars(log_path, file_path);
    env->ReleaseStringUTFChars(log_message, file_content);

    if (file_path == nullptr || file_content == nullptr) {
        return;
    }
    __android_log_print(ANDROID_LOG_ERROR, "JUST_JNI", "the log dir is %s", file_path);

    // create content
    std::string content = std::string(file_content) + "\n";

    // open log file
    int fd = open(file_path, O_RDWR | O_CREAT, S_IRUSR | S_IWUSR);
    if (fd == -1) {
        __android_log_print(ANDROID_LOG_ERROR, "JUST_JNI", "open log file error");
        return;
    }

    // get current file size
    off_t fileSize = lseek(fd, 0, SEEK_END);
    if (fileSize == -1) {
        close(fd);
        __android_log_print(ANDROID_LOG_ERROR, "JUST_JNI", "lseek file error");
        return;
    }

    // new size
    off_t newFileSize = fileSize + content.size();
    if (ftruncate(fd, newFileSize) == -1) {
        __android_log_print(ANDROID_LOG_ERROR, "JUST_JNI", "ftruncate file error");
        close(fd);
        return;
    }

    // mmap
    void *map = (char *) mmap(nullptr, newFileSize, PROT_READ | PROT_WRITE, MAP_SHARED, fd, 0);
    if (map == MAP_FAILED) {
        __android_log_print(ANDROID_LOG_ERROR, "JUST_JNI", "mmap file error");
        close(fd);
        return;
    }

    // write
    memcpy(static_cast<char *>(map) + fileSize, content.c_str(), content.size());

    // msync change
    if (msync(map, newFileSize, MS_SYNC) == -1) {
        __android_log_print(ANDROID_LOG_ERROR, "JUST_JNI", "msync error");
    }

    // unmapping
    if (munmap(map, newFileSize) == -1) {
        __android_log_print(ANDROID_LOG_ERROR, "JUST_JNI", "unmapping error");
    }
    close(fd);

}