#ifndef  H__982364234523__
#define  H__982364234523__

#include <jni.h>
#include <android/log.h>
#include <dirent.h>
#include <stdlib.h>
#include <stdarg.h>
#include <errno.h>
#include <stdio.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <time.h>
#include <unistd.h>
#include <arpa/inet.h>
#include <sys/socket.h>
#include <dlfcn.h>
#include <elf.h>
#include <sys/mman.h>

#define LOGD __android_log_print
#define LogD(fmt, ...)    {LOGD(ANDROID_LOG_ERROR, "Plugin", fmt, ##__VA_ARGS__);printf(fmt,##__VA_ARGS__);}

#define ANDROID_ARM_LINKER
#define SOINFO_NAME_LEN 128
typedef struct soinfo {
    const char name[SOINFO_NAME_LEN];
    Elf32_Phdr *phdr;
    int phnum;
    unsigned entry;
    unsigned base;
    unsigned size;

    int unused;  // DO NOT USE, maintained for compatibility.

    unsigned *dynamic;

    unsigned wrprotect_start;
    unsigned wrprotect_end;

    struct soinfo *next;
    unsigned flags;

    const char *strtab;
    Elf32_Sym *symtab;

    unsigned nbucket;
    unsigned nchain;
    unsigned *bucket;
    unsigned *chain;

    unsigned *plt_got;

    Elf32_Rel *plt_rel;
    unsigned plt_rel_count;

    Elf32_Rel *rel;
    unsigned rel_count;

#ifdef ANDROID_SH_LINKER
    Elf32_Rela *plt_rela;
    unsigned plt_rela_count;

    Elf32_Rela *rela;
    unsigned rela_count;
#endif /* ANDROID_SH_LINKER */

    unsigned *preinit_array;
    unsigned preinit_array_count;

    unsigned *init_array;
    unsigned init_array_count;
    unsigned *fini_array;
    unsigned fini_array_count;

    void (*init_func)(void);
    void (*fini_func)(void);

#ifdef ANDROID_ARM_LINKER
    /* ARM EABI section used for stack unwinding. */
    unsigned *ARM_exidx;
    unsigned ARM_exidx_count;
#endif

    unsigned refcount;
//    struct link_map linkmap;
} soinfo;

#endif