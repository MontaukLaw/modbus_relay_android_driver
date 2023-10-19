cmake_minimum_required(VERSION 3.22.1)

project("modbus485controll")

add_library(${CMAKE_PROJECT_NAME} SHARED
        native-lib.cpp )

target_link_libraries(${CMAKE_PROJECT_NAME}
        ${CMAKE_SOURCE_DIR}/libs/libserial_port.so
        android
        log)