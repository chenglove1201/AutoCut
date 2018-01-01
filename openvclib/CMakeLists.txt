cmake_minimum_required(VERSION 3.4.1)

include_directories(opencv/header
                    src/main/cpp/header)

add_library(opencv_imgproc STATIC IMPORTED)
add_library(opencv_core STATIC IMPORTED)

set_target_properties(opencv_imgproc PROPERTIES IMPORTED_LOCATION ${PROJECT_SOURCE_DIR}/opencv/lib/${ANDROID_ABI}/libopencv_imgproc.a)
set_target_properties(opencv_core PROPERTIES IMPORTED_LOCATION ${PROJECT_SOURCE_DIR}/opencv/lib/${ANDROID_ABI}/libopencv_core.a)

add_library( auto_cut
             SHARED
             src/main/cpp/scanner.cpp
             src/main/cpp/auto_cut.cpp
             src/main/cpp/android_utils.cpp)

find_library( log-lib
              log)

find_library(jnigraphics-lib
             jnigraphics)

target_link_libraries( auto_cut
                       opencv_imgproc
                       opencv_core
                       ${log-lib}
                       ${jnigraphics-lib})