#!/bin/bash
# 初始使用初始数据库脚本
# /**
#  * All content copyright http://www.j2eefast.com, unless
#  * otherwise indicated. All rights reserved.
#  * No deletion without permission
#  *
#  */
echo ""
echo "    _____                 ____.    _     ____   _____"
echo "    |_. _|               | |--|   / \   /_/\_\ |_|_|_|"
echo "   _  | |  ___  __   __  | |__.  /___\  \_\_--|  | |"
echo "  ( |_| | /. / /__\ /__\ | |--| / |_| \ .___) |  | |"
echo "   \____| __/__|__. |__. |_|   /_/   \_\|_____|  |_|"
echo ">>> 初始化数据库。"
echo ""
read -s -n1 -p "请按任意键继续 ... "
cd ..
echo ""
echo ""
mvn clean compile -Dmaven.test.skip=true -U
echo ""
echo ">>> 依赖下载完成，下面开始初始化数据库。"
echo ""
read -s -n1 -p "请按任意键继续 ... "
echo ""
echo ""

MAVEN_OPTS="$MAVEN_OPTS -Xms512m -Xmx1024m"
mvn test -Dmaven.test.skip=false -Dtest=com.j2eefast.test.InitDb

cd bin