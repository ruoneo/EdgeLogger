@echo off
md "log" 2>nul

@rem 启动a.exe，工作目录为同名文件夹a
md "log/3-3-1" 2>nul
start "DataCollector-3-3-1.exe" /D "log/3-3-1" exe/bin/DataCollector-3-3-1.exe

timeout /T 5 /NOBREAK >nul

@rem 启动a.exe，工作目录为同名文件夹a
md "log/3-3-2" 2>nul
start "DataCollector-3-3-2.exe" /D "log/3-3-2" exe/bin/DataCollector-3-3-2.exe

timeout /T 5 /NOBREAK >nul

@rem 启动a.exe，工作目录为同名文件夹a
md "log/3-3-3" 2>nul
start "DataCollector-3-3-3.exe" /D "log/3-3-3" exe/bin/DataCollector-3-3-3.exe

timeout /T 5 /NOBREAK >nul

@rem 启动a.exe，工作目录为同名文件夹a
md "log/3-3-4" 2>nul
start "DataCollector-3-3-4.exe" /D "log/3-3-4" exe/bin/DataCollector-3-3-4.exe

timeout /T 5 /NOBREAK >nul

@rem 启动a.exe，工作目录为同名文件夹a
md "log/3-3-5" 2>nul
start "DataCollector-3-3-5.exe" /D "log/3-3-5" exe/bin/DataCollector-3-3-5.exe

timeout /T 5 /NOBREAK >nul

@rem 启动a.exe，工作目录为同名文件夹a
md "log/3-3-6" 2>nul
start "DataCollector-3-3-6.exe" /D "log/3-3-6" exe/bin/DataCollector-3-3-6.exe

timeout /T 5 /NOBREAK >nul

@rem 启动a.exe，工作目录为同名文件夹a
md "log/3-3-7" 2>nul
start "DataCollector-3-3-7.exe" /D "log/3-3-7" exe/bin/DataCollector-3-3-7.exe

timeout /T 5 /NOBREAK >nul

@rem 启动a.exe，工作目录为同名文件夹a
md "log/3-3-8" 2>nul
start "DataCollector-3-3-8.exe" /D "log/3-3-8" exe/bin/DataCollector-3-3-8.exe

timeout /T 5 /NOBREAK >nul

@rem 启动a.exe，工作目录为同名文件夹a
md "log/3-3-9" 2>nul
start "DataCollector-3-3-9.exe" /D "log/3-3-9" exe/bin/DataCollector-3-3-9.exe

timeout /T 5 /NOBREAK >nul

@rem 启动a.exe，工作目录为同名文件夹a
md "log/3-3-10" 2>nul
start "DataCollector-3-3-10.exe" /D "log/3-3-10" exe/bin/DataCollector-3-3-10.exe

timeout /T 5 /NOBREAK >nul

@rem 启动a.exe，工作目录为同名文件夹a
md "log/3-3-11" 2>nul
start "DataCollector-3-3-11.exe" /D "log/3-3-11" exe/bin/DataCollector-3-3-11.exe

timeout /T 5 /NOBREAK >nul

@rem 启动a.exe，工作目录为同名文件夹a
md "log/3-3-12" 2>nul
start "DataCollector-3-3-12.exe" /D "log/3-3-12" exe/bin/DataCollector-3-3-12.exe


@REM cmd /k