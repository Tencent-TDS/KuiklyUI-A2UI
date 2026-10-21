# the minimum version of CMake.
cmake_minimum_required(VERSION 3.4.1)
project(kuikly_entry)

set(KUIKLY_ENTRY_ROOT_PATH ${CMAKE_CURRENT_SOURCE_DIR})
set(CMAKE_CXX_STANDARD 17)

# Pull in ohpm's generated CMake package-finder. Both source and artifact
# modes converge here: in source mode ohpm resolves `@kuikly-open/render`
# via the `file:` dep (a workspace-local module), in artifact mode it
# downloads the HAR from the ohpm registry — either way, by the time
# hvigor runs CMake, `oh_modules/@kuikly-open/render/` is populated and
# ohpm has emitted a CMake Config script that exposes `render::kuikly`
# (headers from the HAR's `nativeLib.headerPath`, library from its
# packaged `libkuikly.so`). `PACKAGE_FIND_FILE` is injected by hvigor.
if(EXISTS ${PACKAGE_FIND_FILE})
    include(${PACKAGE_FIND_FILE})
    find_package(render REQUIRED CONFIG)
else()
    message(FATAL_ERROR "PACKAGE_FIND_FILE not found. Please build via DevEco Studio or hvigor so ohpm's CMake exports are available.")
endif()

set(SOURCE_SET
        napi_init.cpp
)

add_library(kuikly_entry SHARED ${SOURCE_SET})
include_directories(${KUIKLY_ENTRY_ROOT_PATH} ${KUIKLY_ENTRY_ROOT_PATH}/include)

# Kuikly render SDK — headers + libkuikly.so wired up by `find_package`.
add_library(kuikly_render ALIAS render::kuikly)

# A2UI Kotlin/Native shared library (contains initKuikly entry point).
# Copied in from gradle linkSharedDebugSharedOhosArm64 by 2.0_a2ui_ohos_build.sh.
add_library(kuikly_shared SHARED IMPORTED)
set_target_properties(kuikly_shared PROPERTIES
    IMPORTED_LOCATION ${CMAKE_CURRENT_SOURCE_DIR}/../../../libs/${OHOS_ARCH}/libshared.so)

target_link_libraries(kuikly_entry PUBLIC
    libace_napi.z.so
    libace_ndk.z.so
    hilog_ndk.z.so
    libnative_drawing.so
    libpixelmap.so
    libimage_source.so
    librawfile.z.so
    kuikly_render
    kuikly_shared
)
