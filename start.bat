@echo off
md "log" 2>nul

@rem 启动a.exe，工作目录为同名文件夹a
md "log" 2>nul
start "EdgeLogger.exe" /D "log" exe/bin/EdgeLogger.exe

@REM cmd /k