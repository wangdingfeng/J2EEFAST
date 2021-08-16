@echo off
rem 初始使用初始数据库脚本
rem /**
rem  * All content copyright http://www.j2eefast.com, unless
rem  * otherwise indicated. All rights reserved.
rem  * No deletion without permission
rem  *
rem  */
echo .
echo     _____                 ____.    _     ____   _____
echo     |_. _|               | |--|   / \   /_/\_\ |_|_|_|
echo    _  | |  ___  __   __  | |__.  /___\  \_\_--|  | |
echo   ( |_| | /. / /__\ /__\ | |--| / |_| \ .___) |  | |
echo    \____| __/__|__. |__. |_|   /_/   \_\|_____|  |_|
echo >>> 初始化数据库。
pause

%~d0
cd %~dp0

cd ..

call mvn clean compile -Dmaven.test.skip=true -U
echo >>> 依赖下载完成，下面开始初始化数据库。
pause

set "MAVEN_OPTS=%MAVEN_OPTS% -Xms512m -Xmx1024m"
call mvn test -Dmaven.test.skip=false -Dtest=com.j2eefast.test.InitDb

cd bin
pause