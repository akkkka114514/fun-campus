@echo off
echo.
echo [﷿﷿﷿﷿] ﷿﷿﷿﷿Web﷿﷿﷿﷿﷿﷿﷿﷿﷿﷿node_modules﷿﷿﷿﷿﷿﷿
echo.

%~d0
cd %~dp0

cd ..
npm install --registry=https://registry.npmmirror.com

pause